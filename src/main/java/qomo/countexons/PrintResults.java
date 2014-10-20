package qomo.countexons;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Vector;

import org.apache.hadoop.fs.Path;

public class PrintResults {
	private String outputFile;
	private Vector<String> mapResultFiles;
	private Map<String, Map<String, Pair>> genesExp;
	private Vector<String> geneNames;
	private Map<String, Integer> readsCount;
	private Map<String, Integer> gene2TotalLen;

	public PrintResults(String outputFileStr, Vector<String> mapResultFilesVec,
			Map<String, Map<String, Pair>> genesExpMap,
			Vector<String> geneNamesVec, Map<String, Integer> readsCountsMap,
			Map<String, Integer> gene2TotalLenMap) {
		this.outputFile = outputFileStr;
		this.mapResultFiles = mapResultFilesVec;
		this.genesExp = genesExpMap;
		this.geneNames = geneNamesVec;
		this.readsCount = readsCountsMap;
		this.gene2TotalLen = gene2TotalLenMap;
	}

	public void printResult3() {
		GetFileName fileName;
		try {
			// FileWriter fw = new FileWriter(this.outputFile);
			OutputStreamWriter output = new OutputStreamWriter(GetGeneExp
					.fileSystem().create(new Path(this.outputFile)));
			BufferedWriter bw = new BufferedWriter(output);
			// bw.write("\"geneName\"");
			// // int totalReads = 0;
			// for (int it = 0; it < this.mapResultFiles.size(); it++) {
			// fileName = new GetFileName(mapResultFiles.get(it));
			// bw.write("\t" + "\"" + fileName.getFileNameStr()
			// + "(raw counts)" + "\"");
			// bw.write("\t" + "\"" + fileName.getFileNameStr() + "(RPKM)"
			// + "\"");
			// bw.write("\t" + "\"" + fileName.getFileNameStr()
			// + "(all reads)" + "\"");
			// // totalReads += this.readsCount.get(mapResultFiles.get(it));
			// }
			//
			// bw.write("\t"
			// + "\"gene length (union of all possible exon's length)\""
			// + "\n");
			// System.out.println("geneNames.size()"+geneNames.size()); // for
			// test
			for (int it1 = 0; it1 < geneNames.size(); it1++) {
				bw.write(geneNames.get(it1));
				for (int it2 = 0; it2 < mapResultFiles.size(); it2++) {
					double tmp = (((genesExp.get(mapResultFiles.get(it2)).get(
							geneNames.get(it1)).getSecond()) * 1000.0) / readsCount
							.get(mapResultFiles.get(it2))) * 1000000;
					bw.write("\t"
							+ genesExp.get(mapResultFiles.get(it2))
									.get(geneNames.get(it1)).getSecond());
					bw.write("\t" + tmp / gene2TotalLen.get(geneNames.get(it1)));
					bw.write("\t" + readsCount.get(mapResultFiles.get(it2)));
				}
				bw.write("\t" + gene2TotalLen.get(geneNames.get(it1)) + "\n");
			}

			bw.flush();
			bw.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
