package matt.setup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseTableSetup;
import matt.util.ProgressBar;
import matt.util.StringHandling;
import matt.util.Util;


/**
 * Grabs book cover images off the internet. The cover url has to contain a value for the ISBN of the referred to book.
 */
public class CoverImageHarvester {
	
	public enum BookCoverSources {
		librarything ("http://covers.librarything.com/devkey/b2d09660936b29a432618d8f636ad772/large/isbn/", "", "325472601571f31e1bf00674c368d335"), // allows only 1000 downloads per day
		powells ("http://content-3.powells.com/cgi-bin/imageDB.cgi?isbn=", "", "929041933ebf01fa79e4e6c9030b22bb"),
		openlibrary ("http://covers.openlibrary.org/b/isbn/", "-L.jpg", "10e0c38f29fb91bed65e950b022e5054"),
		;
		
		// the syntax of the cover url is: urlPrefix + isbn + urlSuffix
	    public final String urlPrefix;
	    public final String urlSuffix;
	    // if the source did not contain the cover, is usually returns a placeholder image. to filter those, a md5 hash is used.
	    public final String md5HashOfImageIndicatingNoCoverFound;

	    BookCoverSources(String urlPrefix, String urlSuffix, String md5HashOfImageIndicatingNoCoverFound) {
	        this.urlPrefix = urlPrefix;
	        this.urlSuffix = urlSuffix;
	        this.md5HashOfImageIndicatingNoCoverFound = md5HashOfImageIndicatingNoCoverFound;
	    }
	}
	
	private DatabaseConnection db;
	
	CoverImageHarvester(DatabaseConnection con) {
		this.db = con;
	}
	
	@SuppressWarnings("unused")
	public void harvest() {
		if (false) {
			getHash(new File(Params.pathToCovers+"9780816149018"+".jpg"));
			analyseCovers();
		}
		
		for (BookCoverSources src : BookCoverSources.values()) {
			System.out.println("Getting covers from: "+src.name());
			getImageCovers(src, false);
		}
	}
	
	private void getImageCovers(BookCoverSources src, boolean deleteFiles) {
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(this.db);
		long[] result = dbex.getLongColumnAll(Params.masterTable, "isbn");
		ProgressBar progressBar = new ProgressBar(true);
		for (long isbn : result) {
			File outFile = new File(Params.pathToCovers+isbn+".jpg");
			if (!outFile.exists()) {
				try {
					saveImage(src.urlPrefix+isbn+src.urlSuffix, outFile);
					if (determineWhetherFileIsNoCoverSubstitute(outFile, src, deleteFiles)) {
						progressBar.progress('.');
					} else {
						progressBar.progress('!');
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		progressBar.done();
		System.out.println("Added: "+progressBar.getReportedProgressTicks()+" cover images.");
	}
	
	private void saveImage(String imageUrl, File outFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(outFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
	
	private void getHash(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			System.out.println(md5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean determineWhetherFileIsNoCoverSubstitute(File outFile, boolean deleteFiles) {
		for (BookCoverSources src : BookCoverSources.values()) {
			if (determineWhetherFileIsNoCoverSubstitute(outFile, src, deleteFiles)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean determineWhetherFileIsNoCoverSubstitute(File outFile, BookCoverSources src, boolean deleteFiles) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(outFile);
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			if (md5.equals(src.md5HashOfImageIndicatingNoCoverFound)) {
				fis.close();
				if (deleteFiles) {
					outFile.delete();
				}
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void analyseCovers() {
		for (File file : Util.getAllFiles(Params.pathToCovers, "", ".jpg")) {
			try {
				BufferedImage image = ImageIO.read(file);
			    
			    double aspect = (double) image.getHeight() / (double) image.getWidth();
			    if (aspect < 1) {
			    	System.out.println(file.getName()+", "+image.getHeight()+", "+image.getWidth());
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void filterMasterTableToContainOnlyBooksWithExistingCovers(DatabaseTableSetup dbs) {
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(this.db);
		
		String oldMasterTable = dbs.masterTable+"_old";
		dbex.runQuery("RENAME TABLE "+dbs.masterTable+" TO "+oldMasterTable);
		
		String m21 = "Merge21_coverISBNs";
		dbex.createTableIfNonexisting(m21, "isbn BIGINT primary key");
		for (File file : Util.getAllFiles(Params.pathToCovers, "", ".jpg")) {
			if (!determineWhetherFileIsNoCoverSubstitute(file, false)) {
				dbex.insertLongValuesIntoTable(m21,
					new Long[] {Long.valueOf(StringHandling.getFileNameWithoutExtension(file))});
			}
		}
		
		String query3 = "CREATE TABLE "+dbs.masterTable+" ("
				+" SELECT * FROM "+oldMasterTable
				+" INNER JOIN "+m21+" USING (isbn)"
			+" )";
		dbex.runQuery(query3);
		
		dbex.dropTable(oldMasterTable);
		dbex.dropTable(m21);
		System.out.println("Filtered master table to contain only books with covers available.");
	}

}
