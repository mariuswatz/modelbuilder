package unlekker.util;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class UAppSketch extends UAppBase implements UConstants {
	public UApp app;
	public PApplet p;
	public UColorTool colors;
	public String sketchName,saveFormat,savePath;
	
	public UAppSketch(UApp app) {
		super(app.p);
		this.app=app;
		p=app.p;
		sketchName=this.getClass().getSimpleName();
		saveFormat=sketchName+" ###";
		savePath=UIO.savePath(app.pathSave+UIO.DIRCHAR+sketchName);
	}
	
	/**
	 * Empty skeleton method intended to be overridden.
	 * <code>reinit()</code> should reinitialize the sketch completely,
	 * making no assumptions about pre-existing conditions.
	 */
	public void reinit() {
	}
	
	/**
	 * Empty skeleton method to be overridden with custom code.
	 */
	public void draw() {
		
	}
	
	/**
	 * Empty skeleton method to be overridden with custom code.
	 */
	public void initColors() {
		
	}

	public void load(String path) {
		
	}
	
	public String getSaveFilename(String ext) {
		return UIO.getIncrementalFilename(saveFormat+"."+ext, savePath);
		
	}
	
	public void save() {
		
	}
}
