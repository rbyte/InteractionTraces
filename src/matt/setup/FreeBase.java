package matt.setup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;

import matt.parameters.Params;
import matt.util.Regex;
import matt.util.StringHandling;
import matt.util.Util;

/**
 * Executes queries to Freebase and uses them to fill a database with the harvested results.
 * Queries have to be provided according to the Freebase query language.
 * 
 * @author Matthias Graf
 * 
 */
public class FreeBase {
	
	private File[] queryFiles;
	private DatabaseConnection db;
	
	/**
	 * @param basePathToQueries All files within this path that end with the JSON extension are considered to be queries.
	 */
	FreeBase(DatabaseConnection con) {
		this.db = con;
		this.queryFiles = Util.getAllFiles(Params.pathToFreebaseQueries, "", ".json");
		
		System.out.println("FreeBase: found the following query files:");
		for (int i = 0; i<queryFiles.length; i++) {
			System.out.println("\t"+i+": "+queryFiles[i].getName());
		}
	}
	
	/**
	 * Harvests the Freebase. Stores the results in the "output" folder of the query base directory.
	 * 
	 * Since we harvest potentially huge amounts of data, Freebase returns the results in chunks. Every chunk comes with a cursor.
	 * This cursor has to be put into the subsequent (sub)query to signal a continuation of the harvest.
	 * 
	 * The cursor "false" is returned when this chunk was the last one.
	 * 
	 * Hence, for every query, results are stored in several files, depending on the output size.
	 * If the harvest is aborted and restarted later on, existing output files will be skipped.
	 *
	 */
	public void harvest() {
		for (File queryF : this.queryFiles) {
			String cursor = "true";
			int counter = 0;
			String query = getQueryURLcompatible(queryF);
			System.out.println("Running harvest for: " + queryF.getName());
			boolean skippedSome = false;
			while(!cursor.equals("false")) {
				String queryURL = "http://api.freebase.com/api/service/mqlread?query={%22cursor%22:" + cursor + ",%20%22query%22:" + query + "}";
				String outFileName = StringHandling.getFileNameWithoutExtension(queryF)+"_"+counter+".json";
				String outFilePath = queryF.getParent()+"/output/"+outFileName;
				
				try {
					File outFile = new File(outFilePath);
					if(!outFile.exists() || outFile.length() == 0) {
						if (skippedSome) {
							System.out.println("already existing, skipped until: "+(counter-1)+", ");
							skippedSome = false;
						}
						
						outFile.createNewFile();
						
						BufferedWriter out = new BufferedWriter(new FileWriter(outFilePath));
						BufferedReader in = new BufferedReader(new InputStreamReader(new URL(queryURL).openStream()));
						
						String inputLine;
						while ((inputLine = in.readLine()) != null) {
							try{
				  			  	out.write("\n" + inputLine);
			  			  	} catch (Exception e){
			  			  		e.printStackTrace();
			  			  	}
						}
						
						in.close();
						out.close();
						
						System.out.println("done: "+counter+", ");
					} else {
						skippedSome = true;
					}
					
					cursor = extractCursor(outFilePath);
					counter++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (skippedSome) {
				System.out.println("\tall output files already there. no need to do anyting.");
			}
		}
	}
	
	/**
	 * Puts all the harvested results into the database. You have to run harvest() first.
	 */
	public void fillDatabase() {
		for (File queryF : this.queryFiles) {
			try {
				FreeBaseQueryManager querySQLstatementIssuer = new FreeBaseQueryManager(queryF, this.db);
				querySQLstatementIssuer.run();
			} catch (IOException e1) {
				System.err.println("skipped: "+queryF.getName()+". Not found in FreeBaseQueryManager.Queries");
				continue;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads in the query file and removes line breaks and spaces and converts quotations marks to a URL compatible form.
	 */
	private String getQueryURLcompatible(File file) {
		String result = "";
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));
	        
			String line;
			while ((line = bReader.readLine()) != null) {
				result += line.replaceAll("\"", "%22").replaceAll(" ", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String extractCursor(String filePath) {
		String cursor = "";
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(filePath));

			String line = bReader.readLine();
			line += bReader.readLine();
			line += bReader.readLine();
			line += bReader.readLine();
			
			try {
				cursor = Regex.runRegex("\"cursor\": (.*),", line, 1, 1);
			} catch (Exception e) {
				e.printStackTrace();
				cursor = "false";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public void createMasterTable(String masterTable) {
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(this.db);
		
		if (!dbex.tableExists(masterTable)) {
			String tempTable = "temp";
			dbex.dropTable(tempTable);
			String query1 = "CREATE TABLE "+tempTable+" ("
					+" SELECT"
						+" id, title, author, isbn, pages, pubDate,"
						+" id_subject_dewey.dewey, id_subject_dewey.subject,"
						+" id_genre.genre,"
						+" id_article.article,"
						+" CONCAT(id_subject_dewey.subject, id_genre.genre) AS keyword"
					+" FROM id_title_author_isbn_pages_pubDate"
					
					+" INNER JOIN id_subject_dewey USING (id)"
					+" INNER JOIN id_genre USING (id)"
					+" INNER JOIN id_article USING (id)"
				+" )";
			dbex.runQuery(query1);
			
			String query2 = "CREATE TABLE "+masterTable+" ("
					+" SELECT * FROM "+tempTable+" GROUP BY title, author"
				+" )";
			dbex.runQuery(query2);
			
			dbex.dropTable(tempTable);
			System.out.println("Created master table.");
		} else {
			System.out.println("Master table already existed.");
		}
	}
	
}
