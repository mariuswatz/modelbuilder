package unlekker.util;

import java.io.File;

public class UFileNode {
	public String name,fullName;
	public File f;
	public boolean isDir;
	public long length;
	
	public UFileNode(String name,String path) {
		this.name=name;
		this.fullName=path+'/'+name;
		f=new File(fullName);
		
		isDir=f.isDirectory();
		if(!isDir) length=f.length();
	}
	
	public String toString() {
		String s=UUtil.shorten(name,40);
		if(isDir) s="["+s+"]";
		s=UUtil.strPad(s,40);
		if(!isDir) s+='\t'+UIO.getFileSizeString(length);
		return s;
	}
}
