package unlekker.examples;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.UTransformDeform;
import unlekker.util.*;

public class FileStructure extends PApplet {
	UFileStructure struct;
	USimpleGUI gui;
	
	public void setup() {
		size(500,500, OPENGL);
		smooth();
		
		gui=new USimpleGUI(this);
		gui.addButton("scan");
		gui.addTextField("input", 150, 20);
		
		
		
		UUtil.logToFile("file.log");
		
		String path="C:\\Users\\marius\\Dropbox\\03 Code\\20 Code\\Processing Sketchbook\\2012 Workshops";
		path="C:\\Users\\marius\\Dropbox";
		struct=new UFileStructure(path);
		struct.recurse();
		
		int id=0;
		for(UFileNode dir: struct.dir) UUtil.log((id++)+" "+dir.fullName);
		
		
		struct.saveCSV("file.csv");
		
//		int id=0;
//		for(UFileStructure s:struct.processQueue.processQueue) {
//			UUtil.logDivider(UUtil.nf(id++,3)+" "+s.path);
//			s.printStructure();
//		}
	}
	
	public void scan() {
		try {
			String path=UIO.getFilenameChooserDialog(this, UIO.getCurrentDir());
//			path=path.replaceAll("\\\\", "/");
			println("path "+path+" "+UIO.getPath(path));
			struct=new UFileStructure(UIO.getPath(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void draw() {
		background(0);
		
		gui.draw();
	}

}
