package matt.setup;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseTableSetup;
import matt.parameters.Params.MDSsetup;
import matt.parameters.Params.MDSsetup.DistanceFunction;
import matt.parameters.Params.MDSsetup.MDSfunction;
import matt.util.ProgressBar;
import matt.util.StringHandling;
import matt.util.Util;

public class MultidimensionalScaling {
	
	DatabaseConnection db;
	
	public MultidimensionalScaling(DatabaseConnection con) {
		this.db = con;
	}
	
	public void run(DatabaseTableSetup dbSetup, MDSsetup mds) throws IOException, ClassNotFoundException, SQLException {
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(this.db);
		HashMap<String, Long> keywords = new HashMap<String, Long>();
		for (String keyword : dbex.getStringArray("SELECT "+dbSetup.columnName+" FROM "+dbSetup.allKeywordsTable+" GROUP BY "+dbSetup.columnName)) {
			keywords.put(keyword, dbex.count("SELECT DISTINCT isbn FROM "+dbSetup.linkedTable+" WHERE "+dbSetup.columnName+" = \""+keyword+"\""));
		}
		
		computeDistanceMatrix(
			dbSetup,
			mds,
			keywords);
		assembleAndRunScript(
			mds.getDistanceMatrixFile(dbSetup),
			mds.getRscriptFile(dbSetup),
			mds.getRoutFile(dbSetup),
			mds.mdsFunc);
		putKeywordsInRoutputFile(
			mds.getRoutFile(dbSetup),
			mds.getKeywordsFile(dbSetup),
			keywords);
	}
	
	private File computeDistanceMatrix(
			DatabaseTableSetup dbSetup, 
			MDSsetup mds,
			HashMap<String, Long> keywords) throws IOException, ClassNotFoundException, SQLException {
		switch (mds.distFunc) {
		case intersection:
			return computeDistanceMatrix(dbSetup, mds, keywords, null, null);
		case union:
			return computeDistanceMatrix(dbSetup, mds, keywords, null, null);
		case myIndex:
		case jaccard:
			DistanceFunction orig = mds.distFunc;
			mds.distFunc = DistanceFunction.intersection;
			Float[][] distMat_intersection = StringHandling.parse(StringHandling.readCSV(
				computeDistanceMatrix(dbSetup, mds, keywords), true, false));
			mds.distFunc = DistanceFunction.union;
			Float[][] distMat_union	= StringHandling.parse(StringHandling.readCSV(
				computeDistanceMatrix(dbSetup, mds, keywords), true, false));
			mds.distFunc = orig;
			return computeDistanceMatrix(dbSetup, mds, keywords, distMat_intersection, distMat_union);
		default:
			throw new RuntimeException("Fell through switch case.");
		}
	}
	
	private File computeDistanceMatrix(
			DatabaseTableSetup dbSetup,
			MDSsetup mds,
			HashMap<String, Long> keywords,
			Float[][] distMat_intersection,
			Float[][] distMat_union) throws IOException, ClassNotFoundException, SQLException {
		File outFile = mds.getDistanceMatrixFile(dbSetup);
		DatabaseQueryIssuer dbex = new DatabaseQueryIssuer(this.db);
		Long countBooksTotal = dbex.count("SELECT DISTINCT isbn FROM "+dbSetup.linkedTable);
		if (!outFile.exists()) {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile));
			bufferedWriter.write(StringHandling.concat(keywords.keySet().toArray(new String[] {}), ",", ""));
			bufferedWriter.newLine();
			
			int i = 0, k = 0;
			ProgressBar progressBar = new ProgressBar("Assembling "+mds.distFunc.toString()+" matrix: "+keywords.size()+" rows to go.", keywords.size());
			for (Entry<String, Long> iE : keywords.entrySet()) {
				Double[] result_row = new Double[i+1];
				String i_isbns = "SELECT isbn FROM "+dbSetup.linkedTable+" WHERE "+dbSetup.columnName+" = \""+iE.getKey()+"\"";
				for (Entry<String, Long> kE : keywords.entrySet()) {
					if (i == k) {
						result_row[i] = 0d;
					} else if (k < i) {
						String k_isbns = "SELECT isbn FROM "+dbSetup.linkedTable+" WHERE "+dbSetup.columnName+" = \""+kE.getKey()+"\"";
						double intersection_h = Math.min(iE.getValue(), kE.getValue());
						double intersection_l = Math.max(0, iE.getValue() + kE.getValue() - countBooksTotal);
//						double intersection_expected = iE.getValue() * kE.getValue() / countBooksTotal;
//						double symDiff_h = Math.min(2*countBooksTotal - iE.getValue() - kE.getValue(), iE.getValue() + kE.getValue());
//						double symDiff_l = Math.abs(iE.getValue() - kE.getValue());
//						double union_h = Math.min(iE.getValue() + kE.getValue(), countBooksTotal);
//						double union_l = Math.max(iE.getValue(), kE.getValue());
						
						switch (mds.distFunc) {
						case intersection:
							String intersectionQ = "SELECT COUNT(*) FROM ("+i_isbns+") AS T1 INNER JOIN ("+k_isbns+") AS T2 USING (isbn)";
							result_row[k] = (double) dbex.getLong(intersectionQ);
							assert(intersection_l <= result_row[k] && result_row[k] <= intersection_h);
							break;
						case union:
							String unionQ = "SELECT COUNT(*) FROM (SELECT isbn FROM ("+i_isbns+") AS Tx UNION ("+k_isbns+")) AS Tor";
							result_row[k] = (double) dbex.getLong(unionQ);
							break;
						case myIndex:
							double percentageSimilar = intersection_h-intersection_l == 0 ? 0
								: (distMat_intersection[i][k] - intersection_l) / (intersection_h-intersection_l);
							assert(0 <= percentageSimilar && percentageSimilar <= 1);
							
//							computedDistanceMatrixRow[k] = 1 - percentageSimilar;
							result_row[k] = ((1 - percentageSimilar) + 0.1) / (percentageSimilar + 0.1);
							break;
						case jaccard:
							result_row[k] = distMat_union[i][k] == 0 ? 0
								: (double) distMat_intersection[i][k] / (double) distMat_union[i][k];
							break;
						default:
							throw new RuntimeException("Fell through switch case.");
						}
						
						switch (mds.adjMeth) {
						case sqrt2:
							result_row[k] = Math.sqrt(result_row[k]);
							break;
						case sqrt3:
							result_row[k] = Math.pow(result_row[k], 1f/3f);
							break;
						case reciprocal:
							result_row[k] = result_row[k] == 0 ? 1.1f : 1f/result_row[k];
							break;
						}
						
					}
					k++;
				}
				bufferedWriter.write(matt.util.StringHandling.concat(result_row, ","));
				bufferedWriter.newLine();
				// eliminates memory leaks
				bufferedWriter.flush();
				dbex.getDatabaseConnection().reopen();
				
				progressBar.progress();
				i++; k = 0;
			}
			progressBar.done();
			
			bufferedWriter.close();
			
			printSkipOrExist(outFile, false);
		} else {
			printSkipOrExist(outFile, true);
		}
		return outFile;
	}
	
	private File assembleAndRunScript(File dataFile, File scriptFile, File outFile, MDSfunction mdsFunc) throws IOException {
		if (!scriptFile.exists()) {
			String rscript = "";
			switch (mdsFunc) {
			case cmdscale:
				rscript = 
					"inp <- read.csv(\""+dataFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n"+
					"dim(inp);\n"+
					"i <- cmdscale(inp, eig=TRUE, k=2);\n"+ 
					"write.csv(i$points, \""+outFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n";
				break;
			case smacofSym:
				rscript = 
					"library(smacof);\n"+
					"inp <- read.csv(\""+dataFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n"+
					"dim(inp);\n"+
					"i <- smacofSym(inp, ndim=2, itmax=5000);\n"+
					"write.csv(i[4], \""+outFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n";
				break;
			case smacofRect:
				rscript = 
					"library(smacof);\n"+
					"inp <- read.csv(\""+dataFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n"+
					"dim(inp);\n"+
					"i <- smacofRect(inp, ndim=2, itmax=5000);\n"+
					"write.csv(i[4], \""+outFile.getAbsolutePath().replace("\\", "\\\\")+"\");\n";
				break;
			default:
				throw new RuntimeException("Fell through switch case.");
			}
			
			try {
				StringHandling.writeStringToFile(scriptFile, rscript, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			printSkipOrExist(scriptFile, false);
		} else {
			printSkipOrExist(scriptFile, true);
		}
		
		if (!outFile.exists()) {
			String command = Params.pathToR_executable+" CMD BATCH \""+scriptFile.getAbsolutePath()+"\"";
			Util.runProgram(command);
			
			if (!outFile.exists()) {
				throw new IOException("R script did not generate a new output file!");
			} else {
				printSkipOrExist(outFile, false);
			}
		} else {
			printSkipOrExist(outFile, true);
		}
		return outFile;
	}
	
	private void printSkipOrExist(File file, boolean existed) {
		String add = existed ? " . " : " ! ";
		System.out.println(add + file.getName());
	}
	
	private void putKeywordsInRoutputFile(File inFile, File outFile, HashMap<String, Long> keywords) throws IOException {
		String[][] arr = StringHandling.readCSV(inFile, true, true);

		String[] keywordArr = keywords.keySet().toArray(new String[0]);
		Long[] values = keywords.values().toArray(new Long[0]);
		
		for (int i=0; i < arr.length; i++) {
			arr[i] = new String[] {keywordArr[i], values[i].toString(), arr[i][0], arr[i][1]};
		}
		
		Util.writeCSV(outFile, arr);
	}
	
}
