package qomo.countexons;

import java.io.File;
//import org.apache.commons.math3.geometry.Vector;
import java.util.Vector;


public class AllFiles {
//private String sep;
private Vector<String> filesAndDirs;
private Vector<String> allFiles;
public AllFiles(Vector<String> fileAndDirs,Vector<String> allFiles){
	 this.filesAndDirs=fileAndDirs;
	 this.allFiles=allFiles;
	 //this.sep="/";
}
public int getAllFiles(){
	this.allFiles.clear();
	for(int i=0;i<this.filesAndDirs.size();i++){
		String name=filesAndDirs.get(i);
		if((name.charAt(0)=='"')&&(name.charAt(0)=='"')){
			name=name.substring(1, name.length()-2);
		}
		//char filename[200];
		int len=name.length();
		if((name.charAt(len-1)=='\\')||(name.charAt(len-1)=='/')){
			name=name.substring(0, len-2);
		}
		//String dir_str=name;
		File dirname=new File(name);
		if(dirname.isDirectory()){
			String filename;
			File[] files=dirname.listFiles();
			for(int j=0;j<files.length;j++){
				if(!files[j].isDirectory()){
					filename=files[j].toString();
					allFiles.add(filename);
				}
			}
		}else{
			allFiles.add(name);
		}
	}
		// System.out.println("SampleFiles:\n");
		// for(int i=0;i<allFiles.size();i++){
		// System.out.println(allFiles.get(i));
		// }
	return 0;
}
}
