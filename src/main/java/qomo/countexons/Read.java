package qomo.countexons;

public class Read {
	private String chr;
	private int start;
	private int end;
	private int strand;//0:+ 1:-
	//private int count;
	public Read(){
		this.chr="chr0";
		this.start=0;
		this.end =0;
		this.strand=0;
	//	this.count=0;
	}
	public Read(String chr,int start,int end,int strand){
		this.chr =chr;
		this.start =start;
		this.end =end;
		this.strand =strand;
	//	this.count =1;
	}
	public Read(String chr,int start,int end,int strand,int count){
		this.chr=chr;
		this.start=start;
		this.end=end;
		this.strand=strand;
	//	this.count=count;
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
	/*
	public int getCount(){
		return this.count;
	}*/
	
	public boolean lessThan(Read r){
		if(! this.chr.equals(r.getChr())){
			if(this.chr.compareTo(r.getChr())<0){
				return true;
			}else{
				return false;
			}
		}
		if(this.start!=r.getStart()){
			return this.start<r.getStart();
		}
		if(this.end!=r.getEnd()){
			return this.end<r.getEnd();
		}
		if(this.strand!=r.getStrand()){
			return this.strand<r.getStrand();
		}
		return false;
	}
	
	public boolean equalTo(Read r){
		if(! this.chr.equals(r.getChr())){
			return false;
		}
		if(this.start!=r.getStart()){
			return false;
		}
		if(this.end!=r.getEnd()){
			return false;
		}
		if(this.strand!=r.getStrand()){
			return false;
		}
		return false;
	}
	
	public boolean lessThan(Exon e){
		if(! this.chr.equals(e.getChr())){
			if(this.chr.compareTo(e.getChr())<0){
				return true;
			}else{
				return false;
			}
		}
		if(this.end <= e.getStart()){
			return true;
		}
		return false;
	}
	
	public boolean moreThan(Exon e){
		if(! this.chr.equals(e.getChr())){
			if(this.chr.compareTo(e.getChr())<0){
				return false;
			}else{
				return true;
			}
		}
		if(this.start>=e.getEnd()){
			return true;
		}
		return false;
	}
	
	public int overlap(Exon e){       // compute the length of overlap
		if(! this.chr.equals(e.getChr())){
			return 0;
		}
		if(this.end<=e.getStart()){    //r1 r2 e1 e2
			return 0;
		}
		if(this.start>=e.getEnd()){    //e1 e2 r1 r2
			return 0;
		}
		
		if((this.start>=e.getStart()) && (this.end<=e.getEnd())){ //e1 r1 r2 e2
			return this.end-this.start;
		}
		if((this.start>=e.getStart()) && (this.end>e.getEnd())){ //e1 r1 e2 r2
			return e.getEnd()-this.start;
		}
		if((this.start<=e.getStart()) && (this.end>=e.getEnd())){ // r1 e1 e2 r2
			return e.getEnd()-e.getStart();
		}
		if((this.start<=e.getStart()) && (this.end<e.getEnd())){  //r1 e1 r2 e2
			return this.end-e.getStart();
		}
		System.out.println("There is something wrong! \n");
		return -1;
	}
}
