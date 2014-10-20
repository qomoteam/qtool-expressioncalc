package qomo.countexons;

import java.util.Vector;

public class ToTokens {
	private String str;
	private Vector<String> tokens;
	private char delimiters;
	private boolean skip_empty;
	public ToTokens(String str,Vector<String> tokens){
		this.str=str;
		this.tokens=tokens;
		this.delimiters='\t';
		this.skip_empty=true;
	}
	public ToTokens(String str,Vector<String> tokens,char del){
		this.str=str;
		this.tokens=tokens;
		this.delimiters=del;
		this.skip_empty=true;
	}
	public ToTokens(String str,Vector<String> tokens,char del,boolean skip){
		this.str=str;
		this.tokens=tokens;
		this.delimiters=del;
		this.skip_empty=skip;
	}
	public void String2Tokens(){
		int temp=0;
		for(int i=0;i<str.length();i++){
			if(str.charAt(i)!=this.delimiters){
				temp=i;
				break;
			}
		}
		int lastPos=skip_empty? temp:0;
		lastPos--;
		int pos=str.indexOf(this.delimiters, lastPos+1);
		tokens.clear();
		while(true){
			if(pos==-1){
				if(lastPos+1>=str.length()){
					return;
				}
				tokens.add(str.substring(lastPos+1));
				return;
			}
			if(pos>lastPos+1){
				tokens.add(str.substring(lastPos+1, pos));
			}
			lastPos=pos;
			pos=str.indexOf(this.delimiters, lastPos+1);
		}
		//return;
	}
}
