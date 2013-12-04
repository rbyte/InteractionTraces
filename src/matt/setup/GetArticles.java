package matt.setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;

import matt.parameters.Params;
import matt.util.ProgressBar;

public class GetArticles {
	
	public static void main(String[] args) {
		new GetArticles().run();
	}
	
	// grab: http://api.freebase.com/api/trans/raw/guid/9202a8c04000641f8000000007b682bc
	public void run() {
		try {
			DatabaseQueryIssuer dbex = new DatabaseQueryIssuer();
			String tableName = "articles";
			String[] articlesIdS = dbex.getStringColumn(Params.masterTable, "article");
			dbex.dropTable(tableName);
			dbex.createTableIfNonexisting(tableName, "id VARCHAR(200) primary key, text LONGTEXT");
			ProgressBar progressBar = new ProgressBar(articlesIdS.length);
			int counter = 0;
			for (String articleId : articlesIdS) {
				if (counter++ < 99999) {
//					System.out.println(articleId.substring(1));
					String queryURL = "http://api.freebase.com/api/trans/raw/guid/"+articleId.substring(1);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(new URL(queryURL).openStream()));
					
					String completeArticle = "";
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						completeArticle += inputLine+" ";
					}
					if (completeArticle.startsWith("<p>"))
						completeArticle = completeArticle.substring(3);
					if (completeArticle.endsWith("</p> "))
						completeArticle = completeArticle.substring(0, completeArticle.length()-5);
					completeArticle = completeArticle.replace('"', '\'');
//					System.out.println(completeArticle);
					dbex.insertStringValuesIntoTable(tableName, new String[] {articleId.substring(1), completeArticle});
				}
				progressBar.progress();
			}
			progressBar.done();
			dbex.closeDatabaseConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
