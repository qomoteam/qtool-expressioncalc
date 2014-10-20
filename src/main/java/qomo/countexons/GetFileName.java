package qomo.countexons;

public class GetFileName {
	private String fileName;
	public GetFileName(String fileNameStr){
		this.fileName = fileNameStr;
	}
	public String getFileNameStr(){
		int i =  this.fileName.lastIndexOf('\\');
		if (i == -1){
			i = this.fileName.lastIndexOf('/');
		}
		return this.fileName.substring(i+1);
	}

}
