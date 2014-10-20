package qomo.countexons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.hadoop.fs.Path;

import qomo.common.io.MultiFileInputStream;

public class GetExons {

	public GetExons() {

	}

	public int getTheExons(String reFlatFileStr, Vector<Exon> exons,
			Vector<String> geneNames, Map<String, Integer> maxExonLen,
			Map<String, Integer> gene2TotalLen) {
		exons.clear();

		// FileReader filereader=null;
		InputStreamReader refReader = null;
		try {
			refReader = new InputStreamReader(new MultiFileInputStream(
					GetGeneExp.fileSystem(), new Path(reFlatFileStr)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(refReader);

		Map<String, Vector<Integer>> geneLen = new HashMap<String, Vector<Integer>>();
		Map<String, Vector<Isoform>> gene2Isoforms = new HashMap<String, Vector<Isoform>>();
		// int count=0;
		String tempString = null;
		try {
			while ((tempString = reader.readLine()) != null) {
				// System.out.println(tempString); //for test
				// count++;
				int isoformLen = 0;
				Vector<Exon> isoformExons = new Vector<Exon>();
				Vector<String> blocks = new Vector<String>();
				isoformExons.clear();
				blocks.clear();
				if (tempString.length() < 10) {
					continue;
				}
				ToTokens toTokens = new ToTokens(tempString, blocks);
				toTokens.String2Tokens();
				if (blocks.size() < 5) {
					continue;
				}
				// Vector<String>begins=new Vector<String>();
				// Vector<String>end=new Vector<String>();
				String geneName = blocks.get(0);
				String isoformId = blocks.get(1);
				int isoformStart = Integer.parseInt(blocks.get(4));
				int isoformEnd = Integer.parseInt(blocks.get(5));
				String chr = blocks.get(2);
				int strand = 0;
				if (blocks.get(3).equals("+")) {
					strand = 0;
				} else if (blocks.get(3).equals("-")) {
					strand = 1;
				} else {
					System.out.println("Wrong refFlat format!\n");
					return -1;
				}
				Vector<String> exonSrt = new Vector<String>();
				Vector<String> exonEnd = new Vector<String>();

				if (blocks.get(9).charAt(blocks.get(9).length() - 1) == '\"') {
					blocks.set(
							9,
							blocks.get(9).substring(0,
									blocks.get(9).length() - 1));
				}
				if (blocks.get(9).charAt(0) == '\"') {
					blocks.set(9, blocks.get(9).substring(0));
				}
				if (blocks.get(10).charAt(blocks.get(10).length() - 1) == '\"') {
					blocks.set(
							10,
							blocks.get(10).substring(0,
									blocks.get(10).length() - 1));
				}
				if (blocks.get(10).charAt(0) == '\"') {
					blocks.set(10, blocks.get(10).substring(0));
				}
				ToTokens blo9toTokens = new ToTokens(blocks.get(9), exonSrt,
						',');
				blo9toTokens.String2Tokens();
				ToTokens blo10toTokens = new ToTokens(blocks.get(10), exonEnd,
						',');
				blo10toTokens.String2Tokens();
				int iter;
				for (iter = 0; iter < exonSrt.size(); iter++) {
					int start = Integer.parseInt(exonSrt.get(iter));
					int end = Integer.parseInt(exonEnd.get(iter));
					Exon exon = new Exon(geneName, chr, start, end, strand);
					exons.add(exon);
					isoformExons.add(exon);
					if (!maxExonLen.containsKey(chr)) {
						maxExonLen.put(chr, end - start);
					} else {
						if (maxExonLen.get(chr) < (end - start)) {
							maxExonLen.put(chr, end - start);
						}
					}
					isoformLen += end - start;
				}
				geneNames.add(geneName);
				Isoform isoform = new Isoform(geneName, isoformId, chr,
						isoformStart, isoformEnd, strand, isoformExons);
				if (!gene2Isoforms.containsKey(geneName)) {
					Vector<Isoform> tmp = new Vector<Isoform>();
					tmp.add(isoform);
					gene2Isoforms.put(geneName, tmp);
				} else {
					Vector<Isoform> oldTmp = gene2Isoforms.get(geneName);
					oldTmp.add(isoform);
					gene2Isoforms.put(geneName, oldTmp);
				}
				if (!geneLen.containsKey(geneName)) {
					Vector<Integer> tmp = new Vector<Integer>();
					tmp.add(isoformLen);
					geneLen.put(geneName, tmp);
				} else {
					Vector<Integer> oldTmp = geneLen.get(geneName);
					oldTmp.add(isoformLen);
					geneLen.put(geneName, oldTmp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int out;
		for (int in = 0; in < exons.size(); in++) {
			Exon temp = exons.elementAt(in);
			out = in;
			while (out > 0 && !(exons.elementAt(out - 1).lessThan(temp))) {
				exons.set(out, exons.elementAt(out - 1));
				--out;
			}
			exons.set(out, temp);
		}
		Vector<Exon> exonsTemp = new Vector<Exon>();
		for (int i = 0; i < exons.size(); i++) {
			if (!exonsTemp.contains(exons.get(i))) {
				exonsTemp.add(exons.get(i));
			}
		}
		exons.clear();
		for (int j = 0; j < exonsTemp.size(); j++) {
			exons.add(exonsTemp.get(j));
		}
		int outTemp;
		for (int in = 0; in < geneNames.size(); in++) {
			String str = geneNames.elementAt(in);
			outTemp = in;
			while (outTemp > 0
					&& geneNames.elementAt(outTemp - 1).compareTo(str) >= 0) {
				geneNames.set(outTemp, geneNames.elementAt(outTemp - 1));
				--outTemp;
			}
			geneNames.set(outTemp, str);
		}
		Vector<String> geneNamesTemp = new Vector<String>();
		for (int i = 0; i < geneNames.size(); i++) {
			if (!geneNamesTemp.contains(geneNames.get(i))) {
				geneNamesTemp.add(geneNames.get(i));
			}
		}
		geneNames.clear();
		for (int j = 0; j < geneNamesTemp.size(); j++) {
			geneNames.add(geneNamesTemp.get(j));
		}
		System.out.println("total " + geneNames.size() + " unique genes \n");
		this.getGeneLength(gene2Isoforms, geneNames, gene2TotalLen);
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}// end of getExons

	public int getGeneLength(Map<String, Vector<Isoform>> gene2Isoforms,
			Vector<String> geneNames, Map<String, Integer> gene2TotalLen) {
		// int isoforn_count=0;
		for (int i = 0; i < geneNames.size(); i++) {
			int temp1;
			Vector<Isoform> tempIsoform = gene2Isoforms.get(geneNames.get(i));

			for (int in = 0; in < tempIsoform.size(); in++) {
				Isoform temp = tempIsoform.get(in);
				temp1 = in;
				while (temp1 > 0
						&& !(tempIsoform.get(temp1 - 1).lessThan(temp))) {
					tempIsoform.set(temp1, tempIsoform.elementAt(temp1 - 1));
					--temp1;
				}
				tempIsoform.set(temp1, temp);
			}
			Vector<Isoform> tempIsoform_temp = new Vector<Isoform>();
			for (int j = 0; j < tempIsoform.size(); j++) {
				if (!tempIsoform_temp.contains(tempIsoform.get(j))) {
					tempIsoform_temp.add(tempIsoform.get(j));
				}
			}
			tempIsoform.clear();
			for (int k = 0; k < tempIsoform_temp.size(); k++) {
				tempIsoform.add(tempIsoform_temp.get(k));
			}
			gene2Isoforms.put(geneNames.get(i), tempIsoform);

			Vector<Exon> allExons = new Vector<Exon>();
			allExons.clear();
			for (int m = 0; m < tempIsoform.size(); m++) {
				Vector<Exon> tempVectorExon = tempIsoform.get(m)
						.getVectorExon();
				for (int n = 0; n < tempVectorExon.size(); n++) {
					allExons.add(tempVectorExon.get(n));
				}
			}
			int out = 0;
			for (int in = 0; in < allExons.size(); in++) {
				Exon temp = allExons.get(in);
				out = in;
				while (out > 0 && !(allExons.elementAt(out - 1).lessThan(temp))) {
					allExons.set(out, allExons.get(out - 1));
					--out;
				}
				allExons.set(out, temp);
			}
			Vector<Exon> tempAllExons = new Vector<Exon>();
			tempAllExons.clear();
			for (int j = 0; j < allExons.size(); j++) {
				if (!tempAllExons.contains(allExons.get(j))) {
					tempAllExons.add(allExons.get(j));
				}
			}
			allExons.clear();
			for (int k = 0; k < tempAllExons.size(); k++) {
				allExons.add(tempAllExons.get(k));
			}
			int point = 0;
			// isoforn_count++;
			while (point < allExons.size() - 1) {
				if (!(allExons.get(point).getChr().equals(allExons.get(
						point + 1).getChr()))) {
					point++;
					continue;
				}
				if (allExons.get(point).getEnd() <= allExons.get(point + 1)
						.getStart()) {
					point++;
				} else {
					if (allExons.get(point).getEnd() < allExons.get(point + 1)
							.getEnd()) {
						allExons.get(point).setEnd(
								allExons.get(point + 1).getEnd());
					}
					allExons.removeElementAt(point + 1);
				}
			}
			int length = 0;
			for (int it6 = 0; it6 < allExons.size(); it6++) {
				length += allExons.get(it6).getEnd()
						- allExons.get(it6).getStart();
			}
			gene2TotalLen.put(geneNames.get(i), length);
		}
		return 0;
	} // end of getGeneLength

}
