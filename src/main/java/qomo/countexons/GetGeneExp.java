package qomo.countexons;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GetGeneExp extends Configured implements Tool {

	private static FileSystem fs;

	public String refFlatFileStr;

	public String resultBatch;
	private List<String> listResultBatch;
	// private int fileCount;

	public String outputFileStr;
	private String formatStr;
	private int readlenthInt;
	private boolean needSameStrand;
	private int rows;
	private double overlapPercent;
	private boolean addtionCol;

	public GetGeneExp() {
		// this.mapResultFile_str=mapResultFile_str;
		// super("Count Exons tool");
		this.listResultBatch = new ArrayList<String>();
		// this.fileCount=this.listResultBatch.size();
		this.formatStr = "bed";
		this.readlenthInt = 32;
		this.overlapPercent = 1;
		this.rows = 0;
		this.addtionCol = false;
		this.needSameStrand = false;
	} // end of GetGeneExp

	public int geneExpFun() {
		int fileCount = this.listResultBatch.size();
		String refFlatFile = this.refFlatFileStr;
		Vector<String> mapResultFiles = new Vector<String>();
		Vector<Exon> exons = new Vector<Exon>();
		for (int i = 0; i < fileCount; i++) {
			mapResultFiles.add(listResultBatch.get(i));
		}
		Vector<String> allFiles = new Vector<String>();
		// getAllFiles(mapResultFiles,allFiles);
		AllFiles all = new AllFiles(mapResultFiles, allFiles);
		all.getAllFiles();
		String outputFile = this.outputFileStr;
		String Format = this.formatStr;
		int readlength = this.readlenthInt;
		boolean needSameStrand = this.needSameStrand;
		if (Format == "") {
			Format = "bed";
		}
		// System.out.println(refFlatFile+"refFlatFile");//for test
		if ((refFlatFile.charAt(0) == '"') && (refFlatFile.charAt(0) == '"')) {
			refFlatFile = refFlatFile.substring(1, refFlatFile.length() - 2);
		}
		if ((outputFile.charAt(0) == '"') && (outputFile.charAt(0) == '"')) {
			outputFile = outputFile.substring(1, outputFile.length() - 2);
		}
		if (Format == "eland") {
			if (readlength == 0) {
				readlength = 32;
			} else {
			}
			// char tmp[10];
			Format = Format + Integer.toString(readlength);
		}
		long start = System.currentTimeMillis();
		System.out.println("Count the number of reads mapped to each gene.\n");
		System.out
				.println("This will take several minutes.\nPlease wait ...\n");

		Counts countTemp = new Counts(this.refFlatFileStr, allFiles,
				outputFileStr, this.formatStr, needSameStrand, exons,
				this.rows, this.addtionCol, this.overlapPercent);
		if (countTemp.getCounts() < 0) {
			System.out.println("There is something wrong!Please check...\n");
			return -1;
		}
		long end = System.currentTimeMillis();
		System.out.println("total used" + ((double) end - start) / 1000
				+ "seconds!\n");
		this.rows = 0;
		this.overlapPercent = 1;
		this.addtionCol = false;
		return 0;
	}

	/*
	 * public static void main(String[] args) throws IOException { String
	 * refFlat = "/home/xc/refFlatChr21.txt"; String ResultBatch =
	 * "/home/xc/kidneyChr21.bed.txt"; String output = "/tmp/counts"; String
	 * fileFormat = "bed"; int readLength = 32; boolean strandInfo = false;
	 * List<String> listResultBatch = new ArrayList<String> ();
	 * listResultBatch.add(ResultBatch); GetGeneExp getGeneExpObj = new
	 * GetGeneExp (refFlat, listResultBatch, listResultBatch.size(), output,
	 * fileFormat, readLength, strandInfo); getGeneExpObj.geneExpFun(); }
	 */
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int res = ToolRunner.run(conf, new GetGeneExp(), args);
		System.exit(res);
	}

	public static FileSystem fileSystem() {
		return fs;
	}

	public int run(String[] args) throws Exception {
		refFlatFileStr = args[0];
		resultBatch = args[1];
		outputFileStr = args[2];
		fs = FileSystem.get(this.getConf());
		this.listResultBatch.add(resultBatch);
		return geneExpFun();
	}
}
