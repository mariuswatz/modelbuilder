package unlekker.util;

import java.io.File;
import java.util.ArrayList;

public class UFileStructure implements Runnable {
	public String path;
	public UFileStructure parent,child[];
	public UFileNode dir[],file[];
	public int dirN,fileN;

	public static UFileProcessor processQueue;
	public boolean isProcessed;
	public long processTime,processStart;
	
	public UFileStructure(String path) {
		this.path=path.replace('\\','/');
		if(path.endsWith("/")) path.substring(0,path.length()-1);
		
		File f=new File(path);
		UUtil.log("'"+path+"' "+f.isDirectory());

		String [] list=f.list();
		UFileNode n[]=new UFileNode[list.length];
		for(int i=0; i<n.length; i++) {
			n[i]=new UFileNode(list[i],path);
			if(n[i].isDir) dirN++;
			else fileN++;
		}
		
		dir=new UFileNode[dirN];
		file=new UFileNode[fileN];
		dirN=0;
		fileN=0;
		
		for(int i=0; i<n.length; i++) {
			if(n[i].isDir) dir[dirN++]=n[i];
			else file[fileN++]=n[i];
		}
	}
	
	public void setParent(UFileStructure parent) {
		this.parent=parent;
	}
	
	public class UFileProcessor {
		public Thread thread[];
		public ArrayList<UFileStructure> processQueue;
		
		public UFileProcessor() {
//			thread=new Thread[10];
//			for(int i=0; i<thread.length; i++) thread[i]=new Thread();
			processQueue=new ArrayList<UFileStructure>();
		}
		
		public void add(UFileNode f) {
			UFileStructure struct=new UFileStructure(f.fullName);
			processQueue.add(struct);
			struct.recurse();
			
			UUtil.log("Add "+UUtil.nf(processQueue.size(),3)+" "+f.fullName);
		}
		
		public void update() {
			
		}
	}
	
	public void recurse() {
		if(processQueue==null) processQueue=new UFileProcessor();
		for(int i=0; i<dirN; i++) processQueue.add(dir[i]);
		
		
	}

	public void run() {
		processStart=System.currentTimeMillis();
		
//		for()
	}

	public void printStructure() {
		for(int i=0; i<dirN; i++) UUtil.log(UUtil.nf(i,3)+"\t"+dir[i].toString());
		for(int i=0; i<fileN; i++) UUtil.log(UUtil.nf(i,3)+"\t"+file[i].toString());
	}

}
