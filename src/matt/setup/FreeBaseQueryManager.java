package matt.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import matt.util.ProgressBar;
import matt.util.StringHandling;
import matt.util.Util;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * Essentially processes the harvest and loads it into a MySQL database.
 * 
 * Every original query will create one table in the database. Those are intended to be merged later on, if needed.
 * 
 * In order for the results of a query to be processed, this class needs to know how to interpret the output. This information is hard-coded into this class.
 * 
 * If a query needs to be configured, 4 changes have to be made:
 *  - the query file (json) itself has to be adapted, or added (just put the file into the query base dir)
 *  - the Queries enum in this class has to
 *    - contain the file name of all queries that are to be put into the database and
 *    - contain a declaration of the table variables the query will need in the database
 *    - in the prepareStatement method, contain a description of which values are to be extracted from the output file and put into the database
 *  
 *  You may also need to add new methods to the JSONvalueExtractor if you want to extract values from an output file that have not been described yet.
 * 
 * @author Matthias Graf
 * 
 */
public class FreeBaseQueryManager {	
	
	public enum Queries {
		id_author_isbn_pages_pubDate (
				"id VARCHAR(200) primary key, title VARCHAR(400), author VARCHAR(200), isbn BIGINT, pages BIGINT, pubDate INT",
				"id_title_author_isbn_pages_pubDate"),
//		id_openLibraryID (
//				"id VARCHAR(200) primary key, openLibraryID VARCHAR(15)",
//				"id_openLibraryID"),
		id_subject_dewy_author (
				"id VARCHAR(200) primary key, subject VARCHAR(400), dewey INT",
				"id_subject_dewey"),
		id_genre_dewey (
				"id VARCHAR(200) primary key, genre VARCHAR(200)",
				"id_genre"),
		id_image_author_pubDate (
				"id VARCHAR(200) primary key, image VARCHAR(40)",
				"id_image"),
		// actually this is just the articleID
		id_author_article (
				"id VARCHAR(200) primary key, article VARCHAR(100)",
				"id_article"),
		;
		
	    private final String sqlTableVarDef;
	    private final String tableName;

	    Queries(String sqlTableVarDef, String tableName) {
	        this.sqlTableVarDef = sqlTableVarDef;
	        this.tableName = tableName;
	    }
	    
	    private String getsqlTableVarDef() {
	    	return this.sqlTableVarDef;
	    }
	    
	    private String getTableName() {
	    	return this.tableName;
	    }
	    
		@SuppressWarnings("rawtypes")
		private PreparedStatement prepareStatement(PreparedStatement result, LinkedHashMap row) throws SQLException {
			JSONvalueExtractor extr = new JSONvalueExtractor(row);
			switch (this) {
			case id_author_isbn_pages_pubDate:
				result.setString(1, extr.getID());
				result.setString(2, extr.getTitle());
				result.setString(3, extr.getAuthor());
				result.setLong(4, extr.getISBN());
				result.setLong(5, extr.getPages());
				result.setInt(6, extr.getPubDate());
				break;
//			case id_openLibraryID:
//				result.setString(1, extr.getID());
//				result.setString(2, extr.getOpenLibraryID());
//				break;
			case id_subject_dewy_author:
				result.setString(1, extr.getID());
				result.setString(2, StringHandling.concat(
					extr.getSubtreeByName("/book/written_work/subjects"), "", "<", ">"));
				result.setInt(3, extr.getDewey());
				break;
			case id_genre_dewey:
				result.setString(1, extr.getID());
				result.setString(2, StringHandling.concat(
					extr.getSubtreeByName("genre"), "", "<", ">"));
				break;
			case id_image_author_pubDate:
				result.setString(1, extr.getID());
				result.setString(2, extr.getImage());
				break;
			case id_author_article:
				result.setString(1, extr.getID());
				result.setString(2, extr.getArticleID());
				break;
			default:
				throw new RuntimeException("forgotten case or break");
			}
			return result;
		}
		
		@SuppressWarnings("rawtypes")
		public class JSONvalueExtractor {
			private LinkedHashMap row;
			
			JSONvalueExtractor(LinkedHashMap row) {
				this.row = row;
			}
			
			public Long getPages() {
				return
					(Long) (
						(LinkedHashMap) (
							(LinkedList) (
								(LinkedHashMap) (
									(LinkedList) (
										row
									).get("number_of_pages")
								).getFirst()
							).get("numbered_pages")
						).getFirst()
					).get("value")
				;
			}
			
			public String getID() {
				return (String) row.get("id");
			}
			
			public Long getISBN() {
				return
					Long.parseLong(
						(String) (
							(LinkedHashMap) (
								row
							).get("isbn")
						).get("name")
					)
				;
			}
			
			public String getAuthor() {
				return
					(String) (
						(LinkedHashMap) (
							(LinkedList) (
								(LinkedHashMap) (
									(LinkedList) (
										row
									).get("book")
								).getFirst()
							).get("/book/written_work/author")
						).getFirst()
					).get("name")
				;
			}
			
			public String getTitle() {
				return
					(String) (
						(LinkedHashMap) (
							(LinkedList) (
								row
							).get("book")
						).getFirst()
					).get("name")
				;
			}
			
			public Integer getPubDate() {
				return Integer.parseInt(
						(String) (
							row
						).get("publication_date")
					)
				;
			}
			
			public String[] getSubtreeByName(String name) {
				LinkedList subtree =
					(LinkedList) (
						(LinkedHashMap) (
							(LinkedList) (
								row
							).get("book")
						).getFirst()
					).get(name)
				;
				
				// remove empty entries first
				for (int i=0; i<subtree.size(); i++) {
					if (((String) ((LinkedHashMap) subtree.get(i)).get("name")).equals("")) {
						subtree.remove(i);
					}
				}
				
				if (subtree.size() <= 0) {
					throw new NoSuchElementException();
				}
				String[] result = new String[subtree.size()];
				for (int i=0; i<subtree.size(); i++) {
					result[i] = ((String) ((LinkedHashMap) subtree.get(i)).get("name"));
				}
				return result;
			}
			
			public String getOpenLibraryID() {
				return
					(String) (
						(LinkedHashMap) (
							(LinkedList) (
								row
							).get("openlibrary_id")
						).getFirst()
					).get("value")
				;
			}
			
			public Integer getDewey() {
				return
					Integer.parseInt(
						(String) (
							(LinkedHashMap) (
								(LinkedList) (
									row
								).get("dewey_decimal_number")
							).getFirst()
						).get("value")
					)
				;
			}
			
			public String getImage() {
				return
					(String) (
						(LinkedHashMap) (
							row
						).get("/common/topic/image")
					).get("id")
				;
			}
			
			public String getArticleID() {
				return
					(String) (
						(LinkedHashMap) (
							(LinkedHashMap) (
								(LinkedList) (
									row
								).get("book")
							).getFirst()
						).get("/common/topic/article")
					).get("guid")
				;
			}
		}
		
	}
	
	Queries query;
	File[] outputFiles;
	DatabaseConnection db;
	DatabaseQueryIssuer dbex;
	
	FreeBaseQueryManager(File file, DatabaseConnection db) throws FileNotFoundException {
		boolean foundMatch = false;
		for (Queries q : Queries.values()) {
			if (StringHandling.getFileNameWithoutExtension(file).equals(q.toString())) {
				this.query = q;
				foundMatch = true;
				break;
			}
		}
		if (!foundMatch) {
			throw new FileNotFoundException();
		}
		this.db = db;
		this.dbex = new DatabaseQueryIssuer(this.db);
		this.outputFiles = Util.getAllFiles(file.getParent()+"/output/",
			StringHandling.getFileNameWithoutExtension(file), ".json");
		this.dbex.createTableIfNonexisting(query.getTableName(), query.getsqlTableVarDef());
	}
	
	/**
	 * Does the actual work. For all chunks of the output harvest, puts the data into the database.
	 * A table "meta_processed" is created that holds information on what chunks have already been processed.
	 * If interrupted and rerun, those are then skipped to avoid unnecessary work.
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("rawtypes")
	public void run() throws IOException, ClassNotFoundException, SQLException {
		int rowsCounter = 0;
		int processedRowsCounter = 0;
		boolean skippedFiles = false;
		
		ProgressBar progressBar = new ProgressBar("Processing: "+query.toString()+", "+outputFiles.length+" files.", outputFiles.length);
		
		for (File chunk : outputFiles) {
			if (hasAlreadyBeenProcessed(chunk)) {
				skippedFiles = true;
			} else {
				if (skippedFiles) {
					System.out.println("\t skipped some files. database already contained them");
					skippedFiles = false;
				}
				
				String completeChunkAsString = StringHandling.readFileAsString(chunk);
				
				LinkedHashMap jsonParsedChunk = new LinkedHashMap();
				JSONParser parser = new JSONParser();
				
				
				ContainerFactory containerFactory = new ContainerFactory() {
					public LinkedList creatArrayContainer() {
						return new LinkedList();
					}
					public LinkedHashMap createObjectContainer() {
						return new LinkedHashMap();
					}
				};

				try {
					jsonParsedChunk = (LinkedHashMap) parser.parse(completeChunkAsString, containerFactory);
					@SuppressWarnings("unchecked")
					LinkedList<LinkedHashMap> allRowsInOneChunk =
						(LinkedList<LinkedHashMap>) jsonParsedChunk.get("result");
					
					// TODO optimization: combine multiple rows in one query to the database
					for (LinkedHashMap row : allRowsInOneChunk) {
						rowsCounter++;
						parser.reset();
						
						try {
							PreparedStatement result = this.db.getConnection().prepareStatement("insert into "
								+this.db.getDatabaseName()+"."+query.getTableName()+" values ("
								+StringHandling.concat("?", query.getsqlTableVarDef().split(",").length, ",")+");");
							result = query.prepareStatement(result, row);
							
							try {
								result.executeUpdate();
							} catch(MySQLIntegrityConstraintViolationException e) {
								// duplicate entries
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
							try {
								result.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
						} catch(NoSuchElementException e) {
							// ignore this row if not all the values required have been found
							continue;
						} catch(NumberFormatException e) {
							continue;
						} catch (SQLException e) {
							e.printStackTrace();
						}
							
						processedRowsCounter++;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				progressBar.progress();
				logDoneFileToDatabase(chunk);
				// reopen connection to flush memory (otherwise con will be a memory leak)
				this.db.reopen();
			}
		}
		progressBar.done();
		if (skippedFiles) {
			System.out.println("\tskipped ALL files because database already contained them");
		} else {
			System.out.println(".. rows: "+rowsCounter+", processed: "+processedRowsCounter);
		}
	}
	
	private void logDoneFileToDatabase(File oneOutFile) {
		this.dbex.createTableIfNonexisting("meta_processed", "queryFileDone VARCHAR(100) primary key");
		String[] values = {oneOutFile.getName()+"_in_"+query.getTableName()};
		this.dbex.insertStringValuesIntoTable("meta_processed", values);
	}
	
	private boolean hasAlreadyBeenProcessed(File oneOutFile) {
		// TODO rewrite to use dbex
		try {
			this.dbex.createTableIfNonexisting("meta_processed", "queryFileDone VARCHAR(100) primary key");
        	
			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM "+this.db.getDatabaseName()
				+".meta_processed WHERE queryFileDone='"+ oneOutFile.getName()+"_in_"+query.getTableName() +"';");
        	if (rs.next()) {
        		Long result = (Long) rs.getObject(1);
        		return result != 0L;
            } else {
            	// TODO handle error here
            	return false;
            }
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return false;
	}

}
