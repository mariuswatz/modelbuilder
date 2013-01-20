package unlekker.util;

import java.io.File;
import java.util.ArrayList;

public class UFileNode implements Runnable {
	public static final char DIRCHAR='/';
	public static final String HTML="html";
	public String name,path,filesizeStr;
	public ArrayList<UFileNode> files,folders;
	public File f;
	public boolean isDir,done;
	private boolean listed;
	public String statusMsg[];
	
	public long filesize;
	public String status;
	public int statusCnt;
	
	public UFileNode(String path) {
		path=path.replace('\\','/');
		if(path.endsWith("/")) path.substring(0,path.length()-1);
		this.path=path;
		
		init(false);
	}
	
	private void init(boolean recurse) {
		f=new File(path);
//		UUtil.log(path);
		name=UIO.noPath(path);
		
		isDir=f.isDirectory();
		
//		UUtil.log("name "+name+" "+isDir);
		if(!isDir) {
			filesize=f.length();
			filesizeStr=UIO.getFileSizeString(filesize);
		}		
//		else if(recurse) {
//			list(true);
//		}
	}
	
	public void print() {
		print(false);
	}
	
	public void print(boolean recurse) {
//		if(!isDir || !listed) return;
		if(!isDir || files==null) return;
		
		UUtil.logDivider(toString());
		
		if(folders.size()>0) {
			UUtil.log("");
			for(UFileNode ff : folders) {
				UUtil.log(ff.toString()); 
			}
		}
		
		if(files.size()>0) {
			UUtil.log("");
			for(UFileNode ff : files) UUtil.log(ff.name+" | "+ff.filesizeStr);			
		}
		
		if(recurse ) {
			for(UFileNode ff : folders) ff.print(true);
		}
		
	}
	
	public void list() {
		list(null,false);		
	}
	
	public void listRecursive() {
		Thread t=new Thread(this);
		t.start();
	}
	
	private void list(UFileNode parent,boolean recurse) {
//		UUtil.log("list() Path: '"+path+"' "+f.isDirectory()+" "+f.getAbsolutePath());

		files=new ArrayList<UFileNode>();
		folders=new ArrayList<UFileNode>();

		
		try {
			String [] list=f.list();
			for(int i=0; i<list.length; i++) {
				File file=new File(path+DIRCHAR+list[i]);
				UFileNode node=new UFileNode(file.getAbsolutePath());
				if(node.isDir) folders.add(node);
				else files.add(node);
			}
			
			if(recurse) synchronized(parent){
				for(UFileNode node : folders) node.list(parent,true);
				parent.statusLog(UUtil.nf(parent.statusCnt)+" "+toString());
				parent.status=path;
				parent.statusCnt++;
			}
			
		} catch (Exception e) {
			UUtil.logErr("list() failed. path="+path);
			UUtil.logErrStackTrace(e);
		}
		
		
	}
	
	private void statusLog(String msg) {
		System.arraycopy(statusMsg, 0, statusMsg, 1, statusMsg.length-1);
		statusMsg[0]=msg;
		
	}

	public String toString() {
		String s;
		if(isDir) {
			s="d="+path;
			if(folders!=null && folders.size()>0)
				s+=" | subdir="+folders.size();
			if(files!=null && files.size()>0)
				s+=" | file cnt="+files.size();
		}
		else s="f="+name;
//		String s=UUtil.shorten(name,40);
//		if(isDir) s="["+s+"]";
//		s=UUtil.strPad(s,40);
//		if(!isDir) s+='\t'+filesizeStr;
		return s;
	}

	public void run() {
		statusMsg=new String[100];
		list(this,true);
		
		done=true;
	}

}
