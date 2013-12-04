package matt.setup;

import java.io.IOException;

import matt.meta.AuthorInformation;
import matt.parameters.Params;
import matt.util.ProgressBar;

@AuthorInformation
public class Setup {
	
	public static void main(String[] args) {
		ProgressBar progress = new ProgressBar("Running Setup");
		new Params().test();
		setup();
		progress.done();
	}
	
	@SuppressWarnings("unused")
	private static void setup() {
		try {
			DatabaseConnection database = new DatabaseConnection(Params.databaseAccessSetup);
			if (false)	{
				FreeBase fb = new FreeBase(database);
				fb.harvest();
				fb.fillDatabase();
				fb.createMasterTable(Params.masterTable);
			}
			
			if (false) {
				CoverImageHarvester cih = new CoverImageHarvester(database);
				cih.harvest();
				cih.filterMasterTableToContainOnlyBooksWithExistingCovers(Params.databaseTableSetup);
			}
			
			if (false) new DatabaseConfig(database).run(Params.databaseTableSetup);
			
			if (false)	{
				try {
					new MultidimensionalScaling(database).run(Params.databaseTableSetup, Params.mdsSetup);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			database.closeDatabaseConnection();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
}
