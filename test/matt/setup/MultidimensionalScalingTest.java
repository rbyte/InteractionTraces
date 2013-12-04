package matt.setup;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;
import static org.junit.Assert.*;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseAccessSetup;
import matt.parameters.Params.DatabaseTableSetup;
import matt.parameters.Params.MDSsetup;
import matt.setup.DatabaseConnection;
import matt.setup.MultidimensionalScaling;
import matt.util.StringHandling;

public class MultidimensionalScalingTest {

	@Test
	public void test() throws ClassNotFoundException, SQLException {
		DatabaseTableSetup dbSetup = Params.databaseTableSetup;
		MDSsetup mds = Params.mdsSetup;
		DatabaseAccessSetup databaseAccessSetup = Params.databaseAccessSetup;
		DatabaseConnection database = new DatabaseConnection(databaseAccessSetup);
		test1(databaseAccessSetup, dbSetup, mds, database);
		database.closeDatabaseConnection();
	}
	
	public void test1(
			DatabaseAccessSetup databaseAccessSetup,
			DatabaseTableSetup dbSetup,
			MDSsetup mds,
			DatabaseConnection database) throws ClassNotFoundException, SQLException {
		// requires that the database has been setup correctly beforehand
		try {
			new MultidimensionalScaling(database).run(dbSetup, mds);
//			DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(database);
			
			assert(mds.getDistanceMatrixFile(dbSetup).exists());
			assert(mds.getRoutFile(dbSetup).exists());
			assert(mds.getKeywordsFile(dbSetup).exists());
			
			Float[][] rOutCoords = StringHandling.parse(StringHandling.readCSV(mds.getRoutFile(dbSetup), true, true));
			String[][] result = StringHandling.readCSV(mds.getKeywordsFile(dbSetup));
			String[] keywordsFromDistMat = StringHandling.readCSV(mds.getDistanceMatrixFile(dbSetup), true, 1)[0];
			
			assertTrue(keywordsFromDistMat.length == rOutCoords.length);
			assertTrue(rOutCoords.length == result.length);
			assertTrue(result[0].length == 4);
			assertTrue(rOutCoords[0].length == 2);
			
			for (int i = 0; i < keywordsFromDistMat.length; i++) {
				assertTrue(keywordsFromDistMat[i].equals(result[i][0]));
				assertTrue(rOutCoords[i][0] == Float.parseFloat(result[i][2]));
				assertTrue(rOutCoords[i][1] == Float.parseFloat(result[i][3]));
				
	//			assertTrue(dbex.count("SELECT DISTINCT isbn FROM "+dbSetup.linkedTable+" WHERE "
	//				+dbSetup.columnName+" = \""+keywordsFromDistMat[i]+"\"") == Long.parseLong(result[i][1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
}
