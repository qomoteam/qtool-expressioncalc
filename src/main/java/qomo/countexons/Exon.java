package qomo.countexons;

public class Exon {
	private String geneName;
	private String chr;
	private int start;
	private int end;
	private int strand;
	public Exon(String geneName,String chr, int start,int end,int strand) {
		this.geneName=geneName;
		this.chr=chr;
		this.start=start;
		this.end=end;
		this.strand=strand;
	}
	public Exon(String geneName,String chr,int start,int end){
		this.geneName=geneName;
		this.chr=chr;
		this.start=start;
		this.end=end;
		this.strand=0;
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
	public void setEnd(int ends){
		this.end=ends;
	}
	public int getStrand(){
		return this.strand;
	}
	public String getGeneName(){
		return geneName;
	}
	
	public boolean lessThan (Exon e){
		if (!(this.chr.equals(e.getChr()))){
			if(this.chr.compareTo(e.getChr())<0){
				return true;
			}else{
				return false;
			}
		}
		if(this.start!=e.getStart()){
			return this.start<e.getStart();
		}
		if(this.end!=e.getEnd()){
			return this.end<e.getEnd();
		}
		if(this.strand!=e.getStrand()){
			return this.strand<e.getStrand();
		}
		if(!(this.geneName.equals(e.getGeneName()))){
			if(this.geneName.compareTo(e.getGeneName())<0){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	public boolean equalTo(Exon e){
		if(!(this.chr.equals(e.getChr()))){
			return false;
		}
		if(this.start!=e.getStart()){
			return false;
		}
		if(this.strand!=e.getStrand()){
			return false;
		}
		if(!(this.geneName.equals(e.getGeneName()))){
			return false;
		}
		return true;
	}
}
