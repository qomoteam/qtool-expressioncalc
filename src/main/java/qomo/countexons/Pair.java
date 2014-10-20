package qomo.countexons;

public class Pair {
	private int first;
	private int second;
	public Pair(int first,int second){
		this.first=first;
		this.second=second;
	}
	
	public void setFirst(int fir){
		this.first=fir;
	}
	public int getFirst(){
		return this.first;
	}
	
	public void setSecond(int sec){
		this.second=sec;
	}
	public int getSecond(){
		return this.second;
	}
	
	public void firstAdd(){
		this.first++;
	}
	public void secondAdd(){
		this.second++;
	}
}
