package matt.openLibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("rawtypes")
public class OpenLibrary {
	
	Connection con;
	
	OpenLibrary(Connection con) {
		this.con = con;
	}
	
	public void run() {
		String pathAuthors = new String("/home/cody/openLibraryDumb/ol_dump_authors_2011-09-30.txt");
		authorsToDB(pathAuthors);
		
		String pathEditions = new String("/home/cody/openLibraryDumb/ol_dump_editions_2011-09-30.txt");
		editionsToDB(pathEditions);
		
		String outFile = new String("/home/cody/openLibraryDumb/out.txt");
		backToJSON(outFile);
	}
	
	private void authorsToDB(String pathAuthors) {
		Map json;
		String trimmedJson;
		JSONParser parser = new JSONParser();
		PreparedStatement prestmt;
		
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}
			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String oneLineFromFile;
		
		String authorID = "";
		String authorName = "";
		
		try {
			BufferedReader inauthors = new BufferedReader(new FileReader(new File(pathAuthors)));

			while (null != (oneLineFromFile = inauthors.readLine())) {
				int startindex = oneLineFromFile.indexOf('{');
				trimmedJson = oneLineFromFile.substring(startindex);
				
				try {
					json = (Map) parser.parse(trimmedJson,containerFactory);
					

					authorID = ((String) json.get("key")).substring(9);
					authorName = (String) json.get("name");
					
					if (authorID == null || authorName == null) {
						continue;
					}

					parser.reset();
					
					prestmt = this.con.prepareStatement("insert into LibraryUTF8.authors values (?,?)");
					prestmt.setString(1, authorID);
					prestmt.setString(2, authorName);
					prestmt.executeUpdate();
					prestmt.close();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void editionsToDB(String pathEditions) {
		Map json;
		String trimmedJson;
		JSONParser parser = new JSONParser();
		PreparedStatement prestmt;
		
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}
			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String oneLineFromFile;
		
		String id = "";
		String title = "";
		String author = "";
		int pubyear = -1;
		String coverurl = "";
		String subjecttime = "";
		int pages = -1;
		String subjects = "";
		
		try {
			BufferedReader ineditions = new BufferedReader(new FileReader(new File(pathEditions)));
			
			while (null != (oneLineFromFile = ineditions.readLine())) {
				int startindex = oneLineFromFile.indexOf('{');
				trimmedJson = oneLineFromFile.substring(startindex);
				
				try {
					json = (Map) parser.parse(trimmedJson,containerFactory);

					id = ((String) json.get("key")).substring(7);
					title = (String) json.get("title");
					
					if (id == null || title == null) {
						continue;
					}

					try {
						String a = (json.get("authors").toString()).substring(15, (json.get("authors").toString()).length() - 2);
						
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("select name from LibraryUTF8.authors where id='"+a+"'");
						while(rs.next()){
							 author = rs.getString(1);
						 }
						
						rs = null;
						st.close();
						st = null;
						a = null;
					} catch (Exception e) {
						
						continue;
					}
					
					if (author == null) {
						continue;
					}

					try {
						subjects = (json.get("subjects").toString()).substring(1, (json.get("subjects").toString()).length() - 2);
					} catch (Exception e) {
						continue;
					}

					try {
						pubyear = Integer.parseInt((String) json.get("publish_date"));
					} catch (Exception e) {
						continue;
					}

					try {
						coverurl = json.get("covers").toString();
					} catch (Exception e) {
						continue;
					}

					try {
						subjecttime = (String) json.get("subject_time").toString();
					} catch (Exception e) {
						continue;
					}

					try {
						pages = Integer.parseInt(json.get("number_of_pages").toString());
					} catch (Exception e) {
						continue;
					}

					coverurl = "http://covers.openlibrary.org/b/olid/"+ id + "-M.jpg";
					
					//LibraryUTF8 is the Database and booksutf8 is the name of the Table
					prestmt = con.prepareStatement("insert into LibraryUTF8.books values (?,?,?,?,?,?,?,?)");

					prestmt.setString(1, id);
					prestmt.setString(2, title);
					prestmt.setString(3, author);
					prestmt.setInt(4, pubyear);
					prestmt.setString(5, coverurl);
					prestmt.setString(6, subjecttime);
					prestmt.setInt(7, pages);
					prestmt.setString(8, subjects);
					
					//System.out.println(author);

					prestmt.executeUpdate();
					
					prestmt.close();
					
					author = null;
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void backToJSON (String outFile){
		BufferedWriter bufferedWriter = null;
        try {
			Statement st = this.con.createStatement();
			ResultSet rs = st.executeQuery("select * from LibraryUTF8.bookselection");
			bufferedWriter = new BufferedWriter(new FileWriter(outFile));
			//bufferedWriter.write("[");
			while(rs.next()){
				 String title = rs.getString(2);
				 String author = rs.getString(3);
				 int pubyear = rs.getInt(4);
				 String coverurl = rs.getString(5);
				 String subjecttime = rs.getString(6);
				 int pages = rs.getInt(7);
				 String subjects = rs.getString(8);
				 
				 JSONObject obj=new JSONObject();
				  obj.put("title",title);
				  obj.put("author",author);
				  obj.put("pubyear",pubyear);
				  obj.put("coverurl",coverurl);
				  obj.put("subjecttime",subjecttime);
				  obj.put("pages",pages);
				  obj.put("subjects",subjects);
				  System.out.println(obj.toJSONString());
				  bufferedWriter.write(obj.toJSONString());
				 // bufferedWriter.write(",");
		          bufferedWriter.newLine();
				 
			 }
			//bufferedWriter.write("{}]");

            
            //Construct the BufferedWriter object
            
            //Start writing to the output stream
           // bufferedWriter.write();
           // bufferedWriter.newLine();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e){
        	e.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}
