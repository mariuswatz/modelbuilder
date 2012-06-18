package unlekker.util;

import java.io.File;
import java.util.ArrayList;

public class UFileStructure implements Runnable {
	public String path;
	public UFileStructure parent;
	public ArrayList<UFileStructure> children;
	public ArrayList<UFileNode> dir,file;
	public int dirN,fileN;

	public UFileProcessor processQueue;
	public boolean isProcessed;
	public long processTime,processStart;
	
	public ArrayList<String> ignoreExt;

	public UFileStructure() {		
	}

	public UFileStructure(String path,UFileStructure parent) {
		this.parent=parent;
		parent.addChild(this);
		ignoreExt=parent.ignoreExt;
		
	}
	
	public UFileStructure(String path) {
		read(path);
	}
	
	public void read(String path) {		
		this.path=path.replace('\\','/');
		if(path.endsWith("/")) path.substring(0,path.length()-1);
		
		File f=new File(path);
		UUtil.log("Path: '"+path+"' "+f.isDirectory());

		String [] list=f.list();
		UFileNode n;
		dir=new ArrayList<UFileNode>();
		file=new ArrayList<UFileNode>();
		
		for(int i=0; i<list.length; i++) {
			n=new UFileNode(list[i],path);
			if(n.isDir) dir.add(n);
			else file.add(n);
		}
		
		children=new ArrayList<UFileStructure>();
	}
	
	public void ignoreExtensions(String ext) {
		ignoreExt.add(ext);
	}

	public void ignoreExtensions(String ext[]) {
		for(int i=0; i<ext.length; i++) ignoreExt.add(ext[i]);
	}

	
	public void addChild(UFileStructure f) {
		children.add(f);		
	}

	public class UFileProcessor {
		public Thread thread[];
		public ArrayList<UFileStructure> processQueue;
		
		public UFileProcessor() {
//			thread=new Thread[10];
//			for(int i=0; i<thread.length; i++) thread[i]=new Thread();
			processQueue=new ArrayList<UFileStructure>();
		}
		
		public void add(UFileStructure parent,UFileNode f) {
			UUtil.log("Add "+UUtil.nf(processQueue.size(),3)+" "+f.fullName);
			UFileStructure struct=new UFileStructure(f.fullName,parent);
			processQueue.add(struct);
			struct.recurse();
			
		}
		
		public void update() {
			
		}
	}
	
	public void recurse() {
		if(processQueue==null) processQueue=new UFileProcessor();
		if(dir!=null && dir.size()>0)
			for(UFileNode n : dir) {
				UUtil.log("node "+n);
//				processQueue.add(this,n);
			}
	}
	
	public void saveCSV(String filename) {
		UDataText dat=new UDataText();
		UUtil.logDivider("Save CSV - "+processQueue.processQueue.size());
		try {
			saveCSV(dat);
			dat.save(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UUtil.logErrStackTrace(e);
		}
		
	}
	
	public void saveCSV(UDataText dat) {
		String D="D",F="F",NA="NA";
		
		try {
			UUtil.log("Saving "+path+" "+children.size());
			
			for(UFileNode n : dir) dat.add(n.name).
				add(NA).
				add(NA).
				add(D).
				add(UIO.getPath(n.fullName)).
				endLn();
		for(UFileNode n : file) dat.add(n.name).
				add(n.fileSizeStr).
				add(n.length).
				add(F).
				add(UIO.getPath(n.fullName)).
				endLn();
			
			for(UFileStructure child:children) {
				UUtil.log("Child: "+child.path);
				child.saveCSV(dat);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UUtil.logErrStackTrace(e);
		}
	}

	public void run() {
		processStart=System.currentTimeMillis();
		
//		for()
	}

	public void printStructure() {
		int id=0;
		for(UFileNode n:dir) UUtil.log(UUtil.nf(id,3)+"\t"+dir.get(id++).toString());
		id=0;
		for(UFileNode n:file) UUtil.log(UUtil.nf(id,3)+"\t"+file.get(id++).toString());
	}

}
