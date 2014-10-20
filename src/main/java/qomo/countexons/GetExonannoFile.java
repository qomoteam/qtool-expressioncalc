package qomo.countexons;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GetExonannoFile {
	private String refFlatFileStr;
	private String exonAnnotationFile;
	
	public GetExonannoFile(String refFlatFileStr, String exonAnnotationFile){
		this.refFlatFileStr = refFlatFileStr;
		this.exonAnnotationFile = exonAnnotationFile;
	}
	
	public int getExonAnnotationFile(){
		String refFlatFile = this.refFlatFileStr;
		String outputFile = this.exonAnnotationFile;
		long start = System.currentTimeMillis();
		System.out.println("Generate annotation file for exons. \n");
		System.out.println("This will take several minutes.\nPlease wait ...\n");
		Vector<Exon> exons =new Vector<Exon>();
		Map<String, Integer> maxExonLen = new HashMap<String, Integer>();
		Vector<String> geneNames = new Vector<String>();
		Map<String, Integer> readsCount = new HashMap<String, Integer>();
		Map<String, Integer> geneAveLen = new HashMap<String, Integer>();
		GetExons getExonsObj1 = new GetExons();
		if (getExonsObj1.getTheExons(refFlatFile, exons, geneNames, maxExonLen, geneAveLen) < 0) {
			System.out.println("There is something wrong!\n");
			System.out.println("Please check" + refFlatFile + "!\n");
			return -1;
		}
		try {
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int it=0; it<exons.size(); it++){
				String strand;
				if (exons.get(it).getStrand() == 0){
					strand = "+";
				} else {
					strand = "-";
				}
				bw.write(exons.get(it).getGeneName() + "_" + exons.get(it).getChr() + "_" + exons.get(it).getStart() + "_" + exons.get(it).getEnd() + "_" + exons.get(it).getStrand() + "\t");
				bw.write(exons.get(it).getGeneName() + "-" + exons.get(it).getChr() + "_" + exons.get(it).getStart() + "_" + exons.get(it).getEnd() + "_" + exons.get(it).getStrand() + "\t");
				bw.write(exons.get(it).getChr() + "\t" + strand + "\t" + exons.get(it).getStart() + "\t" + exons.get(it).getEnd() + "\t" + exons.get(it).getStart() + "\t" + exons.get(it).getEnd());
				bw.write("1\t" + exons.get(it).getStart() + ",\t" + exons.get(it).getEnd() + ",\n");
			} 
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		int exon_num = exons.size();
		System.out.println("totla" + exon_num + "unique exons.\n");
		System.out.println("total used" + ((double)end - start)/1000 + "seconds");
		return 0;
	}
}
