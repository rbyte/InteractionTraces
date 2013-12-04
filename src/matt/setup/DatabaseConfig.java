package matt.setup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matt.parameters.Params.DatabaseTableSetup;

public class DatabaseConfig {
	
	private DatabaseQueryIssuer dbex;
	
	public DatabaseConfig(DatabaseConnection con) {
		this.dbex = new DatabaseQueryIssuer(con);
	}
	
	public void run(DatabaseTableSetup databaseTableSetup) {
		setupTables(databaseTableSetup);
	}
	
	public void setupTables(DatabaseTableSetup databaseTableSetup) {
		getKeywordColumnIntoSeperateTable(databaseTableSetup.masterTable, databaseTableSetup.columnName, databaseTableSetup.allKeywordsTable, databaseTableSetup.filterMinBookCount);
		createTableMatchingKeywordToBook(databaseTableSetup.masterTable, databaseTableSetup.allKeywordsTable, databaseTableSetup.columnName, databaseTableSetup.linkedTable);
		System.out.println("Tables setup for: "+databaseTableSetup.toString());
	}
	
	private void getKeywordColumnIntoSeperateTable(String tableName, String columnName, String tableNameForKeywords, int minBookCount) {
		if (!dbex.tableExists(tableNameForKeywords)) {
			HashMap<String, Integer> hist = getAllKeywordsWithBooksCount(tableName, columnName);
			System.out.println("Unique entries of "+columnName+": "+hist.size());
			
			dbex.createTableIfNonexisting(tableNameForKeywords,
				columnName+" VARCHAR(200) primary key, booksCount INT");
			for (Map.Entry<String, Integer> entry : hist.entrySet()) {
				if (entry.getValue() >= minBookCount) {
					dbex.runQuery("INSERT INTO "+tableNameForKeywords+" VALUES (\""
							+entry.getKey()+"\", "+entry.getValue()+")");
				}
			}
		}
	}
	
	/**
	 * Assuming column columnName contains values like <key1><key2><key3>, it returns a (unique) collection of all keywords.
	 */
	private HashMap<String, Integer> getAllKeywordsWithBooksCount(String tableName, String columnName) {
		HashMap<String, Integer> hist = new HashMap<String, Integer>();
		for (String keywordList : dbex.getStringColumn(tableName, columnName)) {
			for (String keyword : extractKeywordsOfOneBook(keywordList)) {
				Integer ding = 0;
				if ((ding = hist.get(keyword)) != null) {
					hist.put(keyword, ding+1);
				} else {
					hist.put(keyword, 1);
				}
			}
		}
		return hist;
	}
	
	public static HashSet<String> extractKeywordsOfOneBook(String keywordList) {
		Pattern p = Pattern.compile("<([^<>]*)>");
		Matcher m = p.matcher(keywordList);
		
		HashSet<String> allUniqueKeywordsForOneBook = new HashSet<String>();
		while (m.find()) {
			String keyword = m.group(1);
			if (!allUniqueKeywordsForOneBook.add(keyword)) {
				// book contains keyword multiple times
			}
		}
		return allUniqueKeywordsForOneBook;
	}
	
	private void createTableMatchingKeywordToBook(String mainLookupTable, String tableName, String columnName, String newTable) {
		if (!dbex.tableExists(newTable)) {
			String[] keywords = dbex.getStringColumn(tableName, columnName);
			dbex.createTableIfNonexisting(newTable, columnName+" VARCHAR(200), isbn BIGINT");
			
			for (String keyword : keywords) {
				// TODO escape special characters in regexp (like Pattern.quote() for Java)
				for (long l : dbex.getLongArray("SELECT isbn FROM "+mainLookupTable+" WHERE "
						+columnName+" REGEXP \"<"+keyword+">\"")) {
					dbex.runQuery("INSERT INTO "+newTable+" VALUES (\""+keyword+"\", "+l+");");
				}
			}
		}
	}
	
}
