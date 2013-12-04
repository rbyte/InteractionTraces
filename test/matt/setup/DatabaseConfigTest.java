package matt.setup;

import static org.junit.Assert.*;

import java.sql.SQLException;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseAccessSetup;
import matt.parameters.Params.DatabaseTableSetup;
import matt.setup.DatabaseConfig;
import matt.setup.DatabaseConnection;
import matt.setup.DatabaseQueryIssuer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseConfigTest {

	DatabaseQueryIssuer dbex;
	
	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		this.dbex = new DatabaseQueryIssuer(new DatabaseConnection(DatabaseAccessSetup.userDefined));
	}
	
	@Test
	public void test() throws ClassNotFoundException, SQLException {
		// requirement: the master table has to be setup
		DatabaseConnection database = new DatabaseConnection(Params.databaseAccessSetup);
		
		for (DatabaseTableSetup dts : DatabaseTableSetup.values()) {
			new DatabaseConfig(database).run(dts);
			run(dts);
		}
	}
	
	@After
	public void tearDown() throws SQLException {
		this.dbex.closeDatabaseConnection();
	}
	
	private void run(DatabaseTableSetup dbs) {
		for (String keyword : this.dbex.getStringColumn(dbs.allKeywordsTable, dbs.columnName)) {
			// TODO escape regexp
			long v1 = this.dbex.getLong("SELECT COUNT(*) FROM "
				+dbs.masterTable+" WHERE "+dbs.columnName+" REGEXP \"<"+keyword+">\"");
			long v2 = this.dbex.getLong("SELECT booksCount FROM "
				+dbs.allKeywordsTable+" WHERE "+dbs.columnName+" = \""+keyword+"\"");
			long v3 = this.dbex.getLong("SELECT COUNT(isbn) FROM "
				+dbs.linkedTable+" WHERE "+dbs.columnName+" = \""+keyword+"\""+" GROUP BY "+dbs.columnName);
			
			try {
				if (v2 != 0 && v3 != 0) { // if they where not filtered
					assertTrue(v1 == v2 && v2 == v3);
				}
			} catch (AssertionError e) {
				System.out.println(keyword+": "+v1+", "+v2+", "+v3);
				assertTrue(false);
			}
		}
	}

}
