package matt.parameters;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import matt.setup.DatabaseConnection;
import matt.util.StringHandling;
import matt.util.Testing;

public class Params {
	
	public static final String dirDelimiter = "/";
	public static final String projectName = "MattsProjectCalgary2011_12";
	public static final String propertiesFileName = "parameters.properties";
	
	private static enum ProjectPathDeterminationMethod {byExistingParamsFile, byPathName}
	private static ProjectPathDeterminationMethod projectPathDeterminationMethod =
			ProjectPathDeterminationMethod.byExistingParamsFile;
	
	public static String pathToProject = getProjectPath();
	public static final Properties properties = loadProperties();
	
	private static boolean isProjectPath(File file) {
		switch (projectPathDeterminationMethod) {
		case byExistingParamsFile:
			return new File(file.getAbsolutePath()+dirDelimiter+propertiesFileName).isFile();
		case byPathName:
			return file.getAbsolutePath().endsWith(projectName);
		default:
			throw new AssertionError();
		}
	}
	
	private static String getProjectPath() {
		// TODO this does not work, if the path contains whitespaces
		File file = new File(Params.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		File file = new File(System.getProperty("user.dir")+dirDelimiter);
//		File file = new File("");
		if (file.isFile())
			file = file.getParentFile();
		assertTrue("Could not determine project path. (1)", file.isDirectory());
		
		// iterate up the path hierarchy until project name folder is found
		try {
			while (!isProjectPath(file))
				file = file.getParentFile();
		} catch (NullPointerException e) {
			throw new AssertionError("Could not determine project path. (2)");
		}
		return file.getAbsolutePath()+dirDelimiter;
	}
	
	private static Properties loadProperties() {
		File propertiesFile = new File(pathToProject+propertiesFileName);
		Properties prop = new Properties();
		try {
			assertTrue("Properties file not found.", propertiesFile.isFile());
			prop.load(new FileInputStream(propertiesFile));
		} catch (IOException ex) {
			throw new AssertionError("IOException while loading properties file.");
		}
		
		return prop;
	}
	
	// project dependencies
	public static final String pathToFFmpeg_executable = properties.getProperty("pathToFFmpeg_executable");
	public static final String pathToR_executable = properties.getProperty("pathToR_executable");
	public static final String pathToInkscape_executable = properties.getProperty("pathToInkscape_executable");
	
	// unfortunately, JBox2D throughs AssertionErrors (see http://code.google.com/p/jbox2d/issues/detail?id=32)
	// that do not seems to have an obvious negative effect when ignored
	// assertion checking in this project is therefore done through JUnits assertTrue()
	// which does not require javas assertions to be enabled
	public static final boolean requireEnabledAssertions = false;
	// found to be incredibly slow when enabled, for some unknown reason
	// depends on AspectJ when enabled
	public static final boolean requireContract4JEnforcement = false;
	
	// clone some of the project files to this (manually set up) ram disk to increase performance (optional)
	public static final String pathToRamDisk = properties.getProperty("pathToRamDisk");
	public static final String pathToCovers = getFastPath("covers");
	public static final String pathToVideoOut = getFastPath("video");
	public static final String pathToTextShapes = getFastPath("textShapes");
	public static final String pathToFreebaseQueries = getFastPath("freebaseQueries");
	public static final String pathToProcessing = getFastPath("Processing");
	public static final String pathToThesis = getFastPath("thesis");
	public static final String pathToLibs = getFastPath("libs");
	public static final String pathToInteractionTraces = getFastPath("interactionTraces");
	public static final String pathToTextures = getFastPath("textures");
	public static final String pathToScreenshots = getFastPath("screenshots");
	public static final String pathToRprojectFiles = getFastPath("R");
	
	public static String getFastPath(String dirName) {
		return (new File(pathToRamDisk+dirName+dirDelimiter).isDirectory()
				? pathToRamDisk
				: pathToProject)
			+ dirName + dirDelimiter;
	}
	
	public static final File textTemplateSVG = new File(Params.pathToTextShapes+"raw.svg");
	
	// when enabled, this will cause the style of the PShape to be ignored (disableStyle())
	// and rather the processing settings will determine how the PShape is drawn
	// the alternative is to draw the (white) PShape on an image and recolour it after being drawn.
	// performance is similar for both methods
	public static final boolean colourKTextThroughFill = true;
	// if enabled, adds the overhead of having to draw in a buffer (image) first and then put it on the screen
	// if a lot of threads are available though, this turns out to be faster
	public static final boolean loadKTextInParallel = false;
	// not faster either ...
	// problematic: overlapping edges flicker because their order is undetermined
	public static final boolean drawLogInParallel = false;
	// extremely slow because curves have to be drawn on an image first that is then blended
	// and blending requires sequential processing
	public static final boolean drawLogInAdditiveMode = false;
	// obviously, twice as time-consuming
	public static final boolean drawLogEdgesAsInnerAndOutterLine = false;
	
	// significantly speeds up loading time
	public static final boolean loadDummyCover = false;
	public static final String dummyCover = pathToCovers + "dummy.jpg";
	
	public static final int jigglingMaxNumberOfIterations = 20;
	public static final int randomTraceGenerationDistributionInfluence = 3;
	// in [0,1], higher is better
//	public static final float videoQuality = 0.92f;
	public static final float videoQuality = 0.60f;
	
	// memory consumption may skyrocket with increasing instances of inkscape opened
	public static final int loadShapesNumberOfProcesses = 10;
	
	// JBox2D
	public static final float radiusWorldBubble = 15;
	public static final boolean worldGroundBodyIsSphere = false;
	public static final int worldGroundSqhereEdges = 30;
	public static final int velocityIterations = 6;
	public static final int positionIterations = 4;
	public static final float forceDampeningFactor = 0.00005f;
	// to reduce continued shaking of books, this reduces the velocityAdd over time
	public static final long ticksTilDampeningFactorFactorTurnsZero = 20000;
	// if true, speed of physics is frame rate independent. while video recording, you made want that though.
	public static final boolean tieWorldPhysicsSpeedToSystemTickInsteadOfFramerate = false;
	
	// decreasing will increase performance (at the cost of reduced maximum blurring radius)
	public static final float maxBlurFactor = 0.4f;
	public static final long kTextOnBookSpaceFadingTicks = 2000;
	public static final boolean limitLogSize = false;
	public static final int maxLogSize = 1000;
	public static final long minNecessaryHeapSpaceInMebibytes = 1600;
	// may be used to preserve memory space
	public static final float coverSizeMultiplicator = 1;
	public static final boolean loadSketchyTextShapes = false;
	
	public static final String returnIn0to1 = "0 <= $return && $return <= 1";
	
	public static final VisualSetupInterface visualS = new VisualSetup(properties);
	public static final DatabaseAccessSetup databaseAccessSetup = DatabaseAccessSetup.userDefined;
	public static final String masterTable = "Merge3";	
	public static final MDSsetup mdsSetup = MDSsetup.v4;
	public static final DatabaseTableSetup databaseTableSetup = DatabaseTableSetup.keywords_20;
	
	// for manual testing
	public static void main(String[] args) {
		System.out.println(pathToCovers);
		System.out.println(properties.getProperty("pathToR_executable"));
	}

	@Test
	public void test() {
		System.out.println("Testing Parameters... ");
		if (requireEnabledAssertions)
			assertTrue("Assertions are disabled but are required to be enabled." +
				"Enable assertions by adding the \"-ea\" parameter to the virtual machine options.",
				Testing.assertionsAreEnabled());
		if (requireContract4JEnforcement)
			assertTrue("Contract enforcement not working but is required to be.",
				Testing.checkForContractEnforcement());
		
		assertTrue("Database is not accessible! Check params.properties file database configuration.\n" +
				"Tried the following setup: "+Params.databaseAccessSetup,
			DatabaseConnection.databaseIsAccessible(Params.databaseAccessSetup));
		
		assertTrue(new File(pathToProject).isDirectory());
		assertTrue(new File(pathToCovers).isDirectory());
		assertTrue(new File(pathToVideoOut).isDirectory());
		assertTrue(new File(pathToTextShapes).isDirectory());
		assertTrue(textTemplateSVG.isFile());
		assertTrue(new File(pathToInteractionTraces).isDirectory());
		assertTrue(new File(pathToTextures).isDirectory());
		assertTrue(new File(pathToScreenshots).isDirectory());
		assertTrue(new File(pathToRprojectFiles).isDirectory());
		assertTrue(new File(dummyCover).isFile());
		
//		assertTrue(new File(pathToProcessing).isDirectory());
//		assertTrue(new File(pathToLibs).isDirectory());
//		assertTrue(new File(pathToFreebaseQueries).isDirectory());
		
		long heapSpaceMaxSizeMebibytes = Runtime.getRuntime().maxMemory()/1024/1024;
		assertTrue("Not enough heap space available! Need at least "+minNecessaryHeapSpaceInMebibytes+
			"MiB of heap space, but got only "+heapSpaceMaxSizeMebibytes,
			heapSpaceMaxSizeMebibytes > minNecessaryHeapSpaceInMebibytes);
		
		// TODO in Unix, those test require an absolute path. not good.
		if (!new File(pathToFFmpeg_executable).isFile())
			System.err.println("Warning: FFmpeg executable not found. Video conversion will not work.");
		if (!new File(pathToR_executable).isFile())
			System.err.println("Warning: R executable not found. Multidimensional scaling will not work.");
		if (!new File(pathToInkscape_executable).isFile())
			System.err.println("Warning: Inkscape executable not found. Text shape generation will not work.");
		System.out.println("done.");
	}
	
	public static enum DatabaseAccessSetup {
		// the program currently is only compatible with mysql
		userDefined (),
		defaultConfig ("jdbc:mysql://ilab51.cpsc.ucalgary.ca:3306/", "Bookbox", "viewer", "rubernecksDelight"),
		;
		
		public final String databaseURL, databaseName, databaseUser, databasePassword;
		
		DatabaseAccessSetup() {
			this(
				properties.getProperty("databaseURL"),
				properties.getProperty("databaseName"),
				properties.getProperty("databaseUser"),
				properties.getProperty("databasePassword")
			);
		}
		
		DatabaseAccessSetup(
	    		String databaseURL,
	    		String databaseName,
	    		String databaseUser,
	    		String databasePassword) {
	    	this.databaseURL		= databaseURL;
	        this.databaseName		= databaseName;
	        this.databaseUser		= databaseUser;
	        this.databasePassword	= databasePassword;
	    }
		
		public String toString() {
			return "Database Access Setup: URL: <"+databaseURL+">, DB Name: <"+databaseName
					+">, User name: <"+databaseUser+">, Password: <"+databasePassword+">.";
		}
	}
	
	public static enum DatabaseTableSetup {
		genres (Params.masterTable, "allGenres", "genre", "allGenresLinked", 4),
		subjects (Params.masterTable, "allSubjects", "subject", "allSubjectsLinked", 4),
		keywords_20 (Params.masterTable, "allKeywords", "keyword", "allKeywordsLinked", 20),
		;
		
		public final String masterTable, allKeywordsTable, columnName, linkedTable;
	    public final int filterMinBookCount;
	    
	    DatabaseTableSetup(
	    		String masterTable,
	    		String allKeywordsTable,
	    		String columnName,
	    		String linkedTable,
	    		int filterMinBookCount) {
	    	this.masterTable		= masterTable;
	        this.allKeywordsTable	= allKeywordsTable;
	        this.columnName			= columnName;
	        this.linkedTable		= linkedTable;
	        this.filterMinBookCount = filterMinBookCount;
	    }
	}
	
	public static enum MDSsetup {
		v4 (DistanceFunction.myIndex, AdjustmentMethod.none, MDSfunction.smacofSym),
		v5jaccard (DistanceFunction.jaccard, AdjustmentMethod.none, MDSfunction.smacofSym),
		;
		
		public enum DistanceFunction {intersection, union, myIndex, jaccard};
		public enum AdjustmentMethod {sqrt2, sqrt3, reciprocal, none};
		public enum MDSfunction {cmdscale, smacofSym, smacofRect};
		
		public DistanceFunction distFunc;
		public AdjustmentMethod adjMeth;
		public MDSfunction mdsFunc;
		
		MDSsetup(
	    		DistanceFunction distFunc,
	    		AdjustmentMethod adjMeth,
	    		MDSfunction mdsFunc) {
	    	this.distFunc	= distFunc;
	        this.adjMeth	= adjMeth;
	        this.mdsFunc	= mdsFunc;
	    }
		
		public File getDistanceMatrixFile(DatabaseTableSetup dbSetup) {
			return new File(pathToRprojectFiles + getDistanceMatrixFileAsString(dbSetup)+".csv");
		}
		
		private String getDistanceMatrixFileAsString(DatabaseTableSetup dbSetup) {
			String result = "mds_"+dbSetup+"_"+distFunc;
			result += adjMeth == AdjustmentMethod.none ? "" : "_"+adjMeth;
			return result;
		}
		
		public File getRoutFile(DatabaseTableSetup dbSetup) {
			return new File(pathToRprojectFiles + getRfilename(dbSetup)+".csv");
		}
		
		public File getRscriptFile(DatabaseTableSetup dbSetup) {
			return new File(pathToRprojectFiles + getRfilename(dbSetup)+".R");
		}
		
		public String getRfilename(DatabaseTableSetup dbSetup) {
			return getDistanceMatrixFileAsString(dbSetup)+"_"+mdsFunc;
		}
		
		public File getKeywordsFile(DatabaseTableSetup dbSetup) {
			return StringHandling.appendToFileName(getRoutFile(dbSetup), "++");
		}
	}
	
}
