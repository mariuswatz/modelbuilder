package unlekker.util;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;

import controlP5.ControlEvent;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import unlekker.modelbuilder.UVec3;
import unlekker.util.*;

public class UAppBase implements UConstants {
	public PApplet p;
  public String pathCurrent,pathSave;
	

	public UAppBase(PApplet p) {
		this.p=p;
		pathCurrent=UIO.getCurrentDir();
		pathSave=pathCurrent+UIO.DIRCHAR+"save";
	}

  public void vertex(UVec3 v[],int n) {
  	for(int i=0; i<n; i++) p.vertex(v[i].x,v[i].y,v[i].z);
	}
	
	public void vertex(UVec3 v) {
	  p.vertex(v.x,v.y,v.z);
	}
	
	public void depth() {
	  p.hint(p.ENABLE_DEPTH_TEST);
	}
	
	
	public void noDepth() {
	  p.hint(p.DISABLE_DEPTH_TEST);
	}
	
	
  public void glClearDepthBuffer() {
		GL gl=((PGraphicsOpenGL)p.g).beginGL();
		gl.glClear(gl.GL_DEPTH_BUFFER_BIT);
		((PGraphicsOpenGL)p.g).endGL();  	
  }

	public void keyEvent(KeyEvent ev) {
		if(ev.getID() == KeyEvent.KEY_PRESSED) keyPressed();
		else if(ev.getID() == KeyEvent.KEY_RELEASED) keyReleased();
	}
	

  public void keyPressed() {
  }

  public void keyReleased() {
  	
  }
  
	public void controlEvent(ControlEvent ev) {
		
	}


	public void mouseWheelEvent(float step) {
		// TODO Auto-generated method stub
		
	}


  public void mouseMoved() {
  }

  public void mouseDragged() {
  }

  public void mousePressed() {
  }


  public boolean rndProbGt(float prob) {
  	return UUtil.rnd.prob(prob);
  }
  
  public float rnd(float range) {
    return UUtil.rnd.random(range);
  }

  public float rnd(float min,float max) {
    return UUtil.rnd.random(min,max);
  }

  public int rndInt(int range) {
  	return UUtil.rnd.integer(range);
  }

  public int rndInt(int min,int max) {
  	return UUtil.rnd.integer(min,max);
  }

  public float rndSign() {
  	return UUtil.rnd.randomSign();
  }
  
  public boolean rndBool() {
  	return UUtil.rnd.bool();
  }
  


  public void logDivider() {
  	UUtil.logDivider();
  }

  public void logDivider(String s) {
  	UUtil.logDivider(s);
  }

  public void log(String s) {
  	UUtil.log(""+s);
  }

  public void log(String s[]) {
  	for(int i=0; i<s.length; i++) UUtil.log(s[i]);
  }

  public void logErr(String s) {
  	UUtil.logErr(s);
  }

  public void logErrDivider(String s) {
  	UUtil.logErrDivider(s);
  }
  
  public static void logErrStackTrace(Exception e) {
  	UUtil.logErrStackTrace(e);
  }
  
	/**
	 * Format floating point number for printing
	 * 
	 * @param num
	 *          Number to format
	 * @param lead
	 *          Minimum number of leading digits
	 * @param digits
	 *          Number of decimal digits to show
	 * @return Formatted number string
	 */
	static public String nf(float num, int lead, int decimal) {
		return UUtil.nf(num,lead,decimal);
	}

	static public String nf(double num, int lead, int decimal) {
		return UUtil.nf((float)num,lead,decimal);
	}

	/**
	 * Format floating point number for printing with maximum 3 decimal points.
	 * 
	 * @param num
	 *          Number to format
	 * @return Formatted number string
	 */
	static public String nf(float num) {
		return UUtil.nf(num);
	}

	static public String nf(double num) {
		return nf((float)num);
	}

	/**
	 * Format integer number for printing, padding with zeros if number has fewer
	 * digits than desired.
	 * 
	 * @param num
	 *          Number to format
	 * @param digits
	 *          Minimum number of digits to show
	 * @return Formatted number string
	 */
	static public String nf(int num, int digits) {
		return UUtil.nf(num,digits);
	}

  
	public boolean keyIsCTRLKey() {
		if((p.keyEvent.getModifiersEx() & p.keyEvent.CTRL_DOWN_MASK)>0) return true;
		return false;
	}

	public boolean keyIsALTKey() {
		if((p.keyEvent.getModifiersEx() & p.keyEvent.ALT_DOWN_MASK)>0) return true;
		return false;
	}

	public boolean keyIsSHIFTKey() {
		if((p.keyEvent.getModifiersEx() & p.keyEvent.SHIFT_DOWN_MASK)>0) return true;
		return false;
	}


}
