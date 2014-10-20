package qomo.countexons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.hadoop.fs.Path;

import qomo.common.io.MultiFileInputStream;

//import java.util.Map;

public class Counts {
	private String refFlatFileStr;
	private Vector<String> mapResultFiles;
	private String outputFileStr;
	private String format;
	private boolean needSameStrand;
	private Vector<Exon> exons;
	private int rows;
	private boolean addtionCol;
	private double overlapPercent;

	public Counts(String refFlatFileStr, Vector<String> mapResultFiles,
			String outputFileStr, String format, boolean needSameStrand,
			Vector<Exon> exons, int rows, boolean addtionCol,
			double overlapPercent) {
		this.refFlatFileStr = refFlatFileStr;
		this.outputFileStr = outputFileStr;
		this.format = format;
		this.needSameStrand = needSameStrand;
		// this.mapResultFiles = new Vector<String>();
		this.mapResultFiles = mapResultFiles;
		this.exons = exons;
		this.overlapPercent = overlapPercent;
		this.rows = rows;
		this.addtionCol = addtionCol;
	}

	public int getCounts() {
		Vector<Exon> exons = this.exons;
		Map<String, Integer> maxExonLen = new HashMap<String, Integer>(); // chromoson
																			// -->int
		Vector<String> geneNames = new Vector<String>();
		Map<String, Integer> readsCount = new HashMap<String, Integer>();
		Map<String, Integer> gene2TotalLen = new HashMap<String, Integer>();
		GetExons getExonsObj = new GetExons();
		if (getExonsObj.getTheExons(this.refFlatFileStr, exons, geneNames,
				maxExonLen, gene2TotalLen) < 0) {
			System.out.println("There is something wrong!\n");
			System.out.println("Please check" + this.refFlatFileStr + "!\n");
			return -1;
		}

		Map<String, Map<String, Pair>> genesExp = new HashMap<String, Map<String, Pair>>();
		for (int it = 0; it < this.mapResultFiles.size(); it++) {
			Map<String, Pair> tmp = new HashMap<String, Pair>();
			genesExp.put(mapResultFiles.get(it), tmp);
			if (getCountsForOneSample(exons, maxExonLen, geneNames,
					mapResultFiles.get(it),
					genesExp.get(mapResultFiles.get(it)), readsCount) < 0) {
				this.overlapPercent = 1;
				this.addtionCol = false;
				return -1;
			}
		}
		PrintResults pr = new PrintResults(this.outputFileStr,
				this.mapResultFiles, genesExp, geneNames, readsCount,
				gene2TotalLen);
		pr.printResult3();
		this.overlapPercent = 1;
		this.addtionCol = false;
		return 0;
	}// end of getCounts

	public int getCountsForOneSample(Vector<Exon> exons,
			Map<String, Integer> maxExonLen, Vector<String> geneNames,
			String MapResultFileStr, Map<String, Pair> genesExp,
			Map<String, Integer> readsCount) {
		GetFileName fileName = new GetFileName(MapResultFileStr);
		String baseName = fileName.getFileNameStr();
		genesExp.clear();
		for (int i = 0; i < geneNames.size(); i++) {
			Pair tmp = new Pair(0, 0);
			genesExp.put(geneNames.get(i), tmp);
		}

		InputStreamReader mapReader = null;
		try {
			mapReader = new InputStreamReader(new MultiFileInputStream(
					GetGeneExp.fileSystem(), new Path(MapResultFileStr)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * try{ fileReader = new FileReader(MapResultFileStr);
		 * }catch(FileNotFoundException e){ e.printStackTrace(); }
		 */

		int length = 0;
		if (this.format.contains("eland")) {
			length = Integer.parseInt((format.substring(5))); // what is it
																// actually?
		} else {
			length = 0;
		}

		BufferedReader reader = new BufferedReader(mapReader);
		String tempString = null;
		try {
			while ((tempString = reader.readLine()) != null) {
				Vector<String> blocks = new Vector<String>();
				if (tempString.length() < 10) {
					continue;
				}
				ToTokens toToken = new ToTokens(tempString, blocks);
				toToken.String2Tokens();
				if (blocks.size() < 5) {
					continue;
				}
				if ((blocks.size() < 6) && (this.format.equals("bed"))) {
					continue;
				}
				if ((blocks.size() < 6) && (this.addtionCol == true)) {
					continue;
				}
				if ((blocks.size() < 7) && (this.format.equals("bed"))
						&& (this.addtionCol == true)) {
					continue;
				}
				// if (this.rows % 1000 == 0) {
				// System.out.println("\r processed" + this.rows + "reads"
				// + baseName);
				// }
				rows++;
				String chr;
				int begin;
				int end;
				int strand = 0;
				int count = 1;
				if (this.format.equals("bed")) {
					chr = blocks.get(0);
					begin = Integer.parseInt(blocks.get(1));
					end = Integer.parseInt(blocks.get(2));
					if (blocks.get(5).equals("+")) {
						strand = 0;
					} else if (blocks.get(5).equals("-")) {
						strand = 1;
					} else {
						System.out.println("Wrong Format! \n");
						this.rows = 0;
						this.overlapPercent = 1;
						this.addtionCol = false;
						return -1;
					}
					if (this.addtionCol == true) {
						count = Integer.parseInt(blocks.get(5));
					}
				} else if (this.format.contains("eland")) {
					chr = blocks.get(1);
					String chromoson = chr.substring(0,
							chr.lastIndexOf(".fa") - 3 + 1);
					chr = chromoson;
					begin = Integer.parseInt(blocks.get(2)) - 1;
					end = begin + length;
					if (blocks.get(4).equals("F")) {
						strand = 0;
					} else if (blocks.get(4).equals("R")) {
						strand = 1;
					} else {
						System.out.println("Wrong Format! \n");
						this.overlapPercent = 1;
						this.rows = 0;
						this.addtionCol = false;
						return -1;
					}
					if (this.addtionCol == true) {
						count = Integer.parseInt(blocks.get(4));
					}
				} else {
					System.out.println("Wrong Format! \n");
					this.rows = 0;
					this.overlapPercent = 1;
					this.addtionCol = false;
					return -1;
				}
				Read oneRead = new Read(chr, begin, end, strand, count);
				this.findOverLapGenes(exons, oneRead, genesExp, maxExonLen);
			}// end of while
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(" \r processed" + this.rows + "reads" + baseName);
		readsCount.put(MapResultFileStr, rows);
		this.rows = 0;
		return 0;
	}// end of getCountsForOneSample

	public int findOverLapGenes(Vector<Exon> exons, Read oneRead,
			Map<String, Pair> genesExp, Map<String, Integer> maxExonLen) {
		int head = 0;
		int tail = exons.size() - 1;
		int pos;
		while (true) {
			pos = (head + tail) / 2;
			if (pos == head) {
				pos = head + 1;
				break;
			}
			if ((!(oneRead.lessThan(exons.get(pos))))
					&& (oneRead.lessThan(exons.get(pos + 1)))) {
				// System.out.println(exons.get(pos).getStart()+"\t"+oneRead.getStart()+"\t"+oneRead.getEnd()+
				// "\t"+exons.get(pos).getEnd()+"\t"+exons.get(pos+1).getStart());//for
				// test
				break;
			} else if (oneRead.lessThan(exons.get(pos))) {
				tail = pos - 1;
				continue;
			} else if (!(oneRead.lessThan(exons.get(pos + 1)))) {
				head = pos;
				continue;
			} else {
				System.out.println("bug!\n");
				return -1;
			}
		}
		int readLen = oneRead.getEnd() - oneRead.getStart();

		Vector<String> geneNames = new Vector<String>();
		double overLen = this.overlapPercent * readLen;
		if (overLen < 1) {
			overLen = 1;
		} else if (overLen > readLen) {
			overLen = readLen;
		}

		while (pos >= 0) {
			if (exons.get(pos).getChr().compareTo(oneRead.getChr()) < 0) {
				break;
			}
			if (exons.get(pos).getChr().compareTo(oneRead.getChr()) > 0) {
				pos--;
				continue;
			}
			if (exons.get(pos).getStart() + maxExonLen.get(oneRead.getChr()) < oneRead
					.getEnd()) {

				break;
			}
			if ((this.needSameStrand == true)
					&& (exons.get(pos).getStrand() != oneRead.getStrand())) {
				pos--;
				continue;
			}
			if (oneRead.overlap(exons.get(pos)) + 0.001 >= overLen) {
				geneNames.add(exons.get(pos).getGeneName());
			}
			pos--;
		}

		int out;
		for (int in = 0; in < geneNames.size(); in++) {
			String str = geneNames.get(in);
			out = in;
			while (out > 0 && geneNames.get(out - 1).compareTo(str) > 0) {
				geneNames.set(out, geneNames.get(out - 1));
				--out;
			}
			geneNames.set(out, str);
		}
		Vector<String> geneNamesTemp = new Vector<String>();
		for (int j = 0; j < geneNames.size(); j++) {
			if (!geneNamesTemp.contains(geneNames.get(j))) {
				geneNamesTemp.add(geneNames.get(j));
			}
		}
		geneNames.clear();
		for (int k = 0; k < geneNamesTemp.size(); k++) {
			geneNames.add(geneNamesTemp.get(k));
		}

		if (geneNames.size() == 1) {
			genesExp.get(geneNames.get(0)).firstAdd();
			genesExp.get(geneNames.get(0)).secondAdd();
		} else {
			for (int m = 0; m < geneNames.size(); m++) {
				genesExp.get(geneNames.get(m)).secondAdd();
			}
		}

		return 0;
	}// end of findOverLapGenes

}
