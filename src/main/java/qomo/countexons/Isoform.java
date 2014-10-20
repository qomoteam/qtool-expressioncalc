package qomo.countexons;

import java.util.Vector;

public class Isoform {
	private String geneName;
	private String id;
	private String chr;
	private int start;
	private int end ;
	private int strand;
	private Vector<Exon> exons;
	public Isoform(String geneName,String id,String chr,int start,int end, int strand,Vector<Exon> exons){
		this.geneName=geneName;
		this.id=id;
		this.chr=chr;
		this.start=start;
		this.end=end;
		this.strand=strand;
		this.exons=exons;
	}
	public String getGeneName(){
		return this.geneName;
	}
	public String getId(){
		return this.id;
	}
	public String getChr(){
		return this.chr;
	}
	public int getStart(){
		return this.start;
	}
	public int getEnd(){
		return this.end;
	}
	public int getStrand(){
		return this.strand;
	}
	public Vector<Exon> getVectorExon(){
		return this.exons;
	}
	public boolean lessThan(Isoform i){
		if(this.id!=i.getId()){
			if(this.id.compareTo(i.getId())<0){
				return true;
			}else{
				return false;
			}
		}
		if(!(this.chr.equals(i.getChr()))){
			if(this.chr.compareTo(i.getChr())<0){
				return true;
			}else{
				return false;
			}
		}
		if(this.start!=i.getStart()){
			return this.start<i.getStart();
		}
		if(this.end!=i.getEnd()){
			return this.end<i.getEnd();
		}
		if(this.strand!=i.getStrand()){
			return this.strand<i.getStrand();
		}
		if(this.geneName!=i.getGeneName()){
			if(this.geneName.compareTo(i.getGeneName())<0){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	public boolean eaqualTo(Isoform i){
		if(!(this.id.equals(i.getId()))){
			return false;
		}
		return true;
	}
}
