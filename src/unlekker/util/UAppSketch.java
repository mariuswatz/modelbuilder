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
	 * Intended for use with rendering animations and the like where it might be necessary to tell a sketch 
	 * to reset its current state to the start of a sequence. Where <code>reinit()</code> implies that the sketch
	 * should generate a new set of parameters <code>reinit()</code> should merely prepare the sketch for a new
	 * render. 
	 */
	public void rewind() {
	}

	/**
	 * Empty skeleton method to be overridden with custom code.
	 */
	public void draw() {
		
	}
	
	/**
	 * Map function taken from PApplet.java, maps input from one min..max range to another.
	 * @param value Current value
	 * @param istart Minimum value of input 
	 * @param istop Maximum value bound of input
	 * @param ostart Minimum value of output
	 * @param ostop Maximum value bound of output
	 * @return
	 */
	static public final float map(float value,
      float istart, float istop,
      float ostart, float ostop) {
				return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
  static public final float degrees(float radians) {
    return radians * RAD_TO_DEG;
  }

  static public final float radians(float degrees) {
    return degrees * DEG_TO_RAD;
  }

  public float random(float min,float max) {return app.rnd(min,max);}
  public float random(float range) {return app.rnd(range);}
  public float rnd(float min,float max) {return app.rnd(min,max);}
  public float rnd(float range) {return app.rnd(range);}
  
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
