package matt.setup;

import static org.junit.Assert.*;

import java.util.Collection;

import matt.parameters.Params.DatabaseAccessSetup;
import matt.setup.DatabaseConnection;
import matt.setup.DatabaseQueryIssuer;
import matt.util.StringHandling;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseQueryIssuerTest {

	DatabaseQueryIssuer dbex;
	
	String testTableName = "testTable";
	String sqlTableVarDef = "id INT primary key, text VARCHAR(100)";
	boolean tableHasBeenCreatedByTheTest = false;
	
	@Before
	public void setUp() throws Exception {
		this.dbex = new DatabaseQueryIssuer(new DatabaseConnection(DatabaseAccessSetup.userDefined));
	}

	@Test
	public void test() {
		if (dbex.tableExists(testTableName)) {
			fail("Table "+testTableName+" already exists.");
		} else {
			dbex.createTableIfNonexisting(testTableName, sqlTableVarDef);
			assertTrue(dbex.tableExists(testTableName));
			tableHasBeenCreatedByTheTest = true;
			assertTrue(dbex.countColumns(testTableName) == 2);
			assertTrue(dbex.count(testTableName) == 0);
			dbex.dropTable(testTableName);
			assertTrue(!dbex.tableExists(testTableName));
			dbex.createTableIfNonexisting(testTableName, sqlTableVarDef);
			assertArrayEquals(new String[] {"id", "text"}, dbex.getColumnNames(testTableName));
			for (int i = 0; i < 10; i++) {
				dbex.runQuery("INSERT INTO "+testTableName+" VALUES ("
					+i+", '"+StringHandling.concat(".", i, "")+"')");
			}
			assertTrue(dbex.count(testTableName) == 10);
			assertArrayEquals(new int[] {0,1,2,3,4,5,6,7,8,9}, dbex.getIntColumn(testTableName, "id"));
			String query = "SELECT * FROM "+testTableName+" WHERE id < 5";
			dbex.createTableFromQuery(testTableName+2, query);
			assertTrue(dbex.tableExists(testTableName+2));
			assertTrue(dbex.countColumns(testTableName+2) == 2);
			assertArrayEquals(new String[] {"id", "text"}, dbex.getColumnNames(testTableName+2));
			assertTrue(dbex.count(query) == dbex.count(testTableName+2));
			assertArrayEquals(new int[] {0,1,2,3,4}, dbex.getIntColumn(testTableName+2, "id"));
			assertArrayEquals(
				new String[] {"",".","..","...","...."},
				dbex.getStringArray("SELECT text FROM "+testTableName+2)
			);
			Collection<String[]> result = dbex.getTable(testTableName).values();
			result.contains(new String[] {"0", "."});
			result.contains(new String[] {"3", "..."});
			result.contains(new String[] {"9", "........."});
			
			// TODO make it work
//			assertTrue(dbex.getRow(testTableName, "3").get("text").equals("...."));
		}
	}
	
	@After
	public void tearDown() throws Exception {
		if (tableHasBeenCreatedByTheTest) {
			dbex.dropTable(testTableName);
			dbex.dropTable(testTableName+2);
		}
		dbex.closeDatabaseConnection();
	}

}
