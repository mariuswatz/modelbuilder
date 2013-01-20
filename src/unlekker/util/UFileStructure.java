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
	
	public ArrayList<String> filterExtIgnore,filterExtAccept;
	public String filterPrefix;

	public UFileStructure() {		
	}

	public UFileStructure(String path,UFileStructure parent) {
		this.parent=parent;
		parent.addChild(this);
		filterExtIgnore=parent.filterExtIgnore;
		filterExtAccept=parent.filterExtAccept;
		filterPrefix=parent.filterPrefix;
		
	}
	
	public UFileStructure(String path) {
		read(path);
	}

	public String [] listFiles() {
		return getFileList(true, false);
	}

	public String [] listDir() {
		return getFileList(false,true);
	}
	
	public String [] listAll() {
		return getFileList(true,true);
	}
	
	public static String [] listFiles(String path,String [] ext) {
		UFileStructure uf=new UFileStructure();
		if(ext!=null) uf.acceptExtensions(ext);
		uf.read(path);
		
		return uf.listFiles();
	}

	public String [] getFileList(boolean includeFiles,boolean includeDir) {
		ArrayList<String> flist=new ArrayList<String>();
		
		if(includeDir) 
			for(UFileNode d:dir) flist.add(d.fullName);
					
		if(includeFiles) 
			for(UFileNode d:file) flist.add(d.fullName);
					
		if(flist.size()<1) return null;
					
		String [] result=new String[flist.size()];
		for(int i=0; i<flist.size(); i++) result[i]=flist.get(i);
		return result;
	}

	public UFileStructure read(String path) {		
		this.path=path.replace('\\','/');
		if(path.endsWith("/")) path.substring(0,path.length()-1);
		
		File f=new File(path);
		UUtil.log("Path: '"+path+"' "+f.isDirectory());

		String [] list=f.list();
		UFileNode n;
		dir=new ArrayList<UFileNode>();
		file=new ArrayList<UFileNode>();
		
		boolean ok;
		for(int i=0; i<list.length; i++) {
			ok=true;
			ok=checkExt(list[i]);
			n=new UFileNode(list[i],path);
//			UUtil.log(n.fullName+" "+list[i]);
			if(ok) {
				if(n.isDir) dir.add(n);
				else file.add(n);
			}
		}
		
		children=new ArrayList<UFileStructure>();
		
		return this;
	}

	private boolean filterPrefix(String fname) {
		if(filterPrefix==null) return true;
		
		fname=fname.toLowerCase();
		for(String cmp:filterExtIgnore) if(fname.startsWith(filterPrefix)) return false;
		return true;
	}

	private boolean checkExt(String fname) {
		if(filterExtIgnore==null && filterExtAccept==null) return true;
		
		fname=fname.toLowerCase();
		if(filterExtIgnore!=null)
			for(String cmp:filterExtIgnore) if(fname.endsWith(cmp)) return false;
					
		boolean ok=false;
		if(filterExtAccept!=null) {
			for(String cmp:filterExtAccept) if(fname.endsWith(cmp)) ok=true;
			if(!ok) return false;
		}
		return true;
	}

	public UFileStructure acceptExtension(String ext) {
		if(filterExtAccept==null) filterExtAccept=new ArrayList<String>();
		filterExtAccept.add(ext);
		return this;
	}

	public UFileStructure acceptExtensions(String ext[]) {
		if(filterExtAccept==null) filterExtAccept=new ArrayList<String>();
		for(int i=0; i<ext.length; i++) filterExtAccept.add(ext[i]);
		return this;
	}

	public UFileStructure ignoreExtension(String ext) {
		if(filterExtIgnore==null) filterExtIgnore=new ArrayList<String>();
		filterExtIgnore.add(ext);
		return this;
	}

	public UFileStructure ignoreExtensions(String ext[]) {
		if(filterExtIgnore==null) filterExtIgnore=new ArrayList<String>();
		for(int i=0; i<ext.length; i++) filterExtIgnore.add(ext[i]);
		return this;

	}

	
	public UFileStructure addChild(UFileStructure f) {
		children.add(f);		
		return this;

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
	
	public void searchFiles(String pattern,ArrayList<UFileNode> results, boolean doLowercase) {
		if(file==null || file.size()<1) return; 
		for(UFileNode f : file) {
			if(doLowercase) 
				if(f.name.toLowerCase().matches(pattern.toLowerCase()))
						results.add(f);
			if(f.name.matches(pattern)) results.add(f);
			
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
