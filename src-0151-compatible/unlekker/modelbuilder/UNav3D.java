package unlekker.modelbuilder;

import controlP5.ControlP5;
import controlP5.ControllerInterface;

// 1.5.1
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

//import processing.event.*; // 2.0


import processing.core.*;
import unlekker.util.*;

/**
 * Class to provide standard 3D navigation (pan, rotate, zoom) functionality. By default UNav3D registers to receive key events from 
 * <code>PApplet</code>, but this can be controlled with <code>registerEvents()</code> and <code>unregisterEvents()</code>.
 * 
 *  Typical usage:
 *  
 *  <code>UNav3D nav;

	void setup() {
		...
		nav=new UNav3D(this);
		nav.setTranslation(width/2,height/2);
		..
	}
	
	void draw() {
		background(0);
		lights();
		nav.doTransforms();
		..
	}</code>
	*
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 *
 */
public class UNav3D implements PConstants {
	public PApplet p;
	public UVec3 rot,trans,rotReset,transReset;
	public float rotSpeed=5,transSpeed=5;
	public boolean isParentPApplet=true;
	public boolean shiftIsDown=false,altIsDown,ctrlIsDown;
	public USimpleGUI gui;
	public boolean enabled=true;
//	public MouseEvent theMouseEvent;
//	public KeyEvent theKeyEvent;
	private boolean doDebug=true;
	
	public UNav3D(PApplet _p) {
		init(_p,true);
	}

	/**
	 * Constructor that allows specifying whether or not to register to automatically receive Processing keyboard and mouse events.
	 * Useful when you want to use a UNav3D instance as a way to save camera views etc.
	 * @param _p
	 * @param _doEvents Flag to specify whether or not to register for events.
	 */
	public UNav3D(PApplet _p,boolean _doEvents) { 
		init(_p,_doEvents);
	}
	
	public void setGUI(USimpleGUI gui) {
		this.gui=gui;
	}
	
	public void init(PApplet _p,boolean _doEvents) {
		p=_p;		
		if(p.getClass().getSimpleName().equals("unlekker.app.App")) isParentPApplet=false;
		rot=new UVec3(0,0,0);
		trans=new UVec3(0,0,0);		
		rotReset=new UVec3(0,0,0);
		transReset=new UVec3(0,0,0);		
		if(_doEvents) {
		  // code to allow us to use the mouse wheel
		  registerEvents();
		}
	}

	public void registerEvents() {
		MouseWheelInput mw=new MouseWheelInput();
	  p.addMouseWheelListener(mw);

	  /* 2.0 beta
	  p.registerMethod("mouseEvent",this);
	  p.registerMethod("keyEvent",this);	  
	   * 
	   */
	  
	  /*
	   * 1.5.1
	   */
		p.registerKeyEvent(this);
		p.registerMouseEvent(this);
	}

	public void unregisterMouseEvents() {
//	  p.unregisterMethod("mouseEvent",this); // 2.0
		p.unregisterMouseEvent(this); // 1.5.1
	}

	public void unregisterKeyEvents() {
//	  p.unregisterMethod("keyEvent",this);
		p.unregisterKeyEvent(this); // 1.5.1
	}

	public void unregisterEvents() {
//  p.unregisterMethod("mouseEvent",this); // 2.0
//  p.unregisterMethod("keyEvent",this);
		
		p.unregisterKeyEvent(this); // 1.5.1
		p.unregisterMouseEvent(this); 
		
	}

	public void transformPoint(UVec3 v) { 
  	if(rot.y!=0) v.rotateY(rot.y);
  	if(rot.x!=0) v.rotateX(rot.x);
  	if(rot.z!=0) v.rotateZ(rot.z);
  	v.add(trans.x,trans.y,trans.z);
  }

	public void untransformPoint(UVec3 v) { 
  	v.sub(trans.x,trans.y,trans.z);
  	if(rot.z!=0) v.rotateZ(-rot.z);
  	if(rot.x!=0) v.rotateX(-rot.x);
  	if(rot.y!=0) v.rotateY(-rot.y);
  }

	public void doTransforms() { 
  	p.translate(trans.x,trans.y,trans.z);
  	if(rot.y!=0) p.rotateY(rot.y);
  	if(rot.x!=0) p.rotateX(rot.x);
  	if(rot.z!=0) p.rotateZ(rot.z);
  }
  
	public void doTransformsReverse() {
  	if(rot.z!=0) p.rotateZ(-rot.z);
  	if(rot.x!=0) p.rotateX(-rot.x);
  	if(rot.y!=0) p.rotateY(-rot.y);
  	p.translate(-trans.x,-trans.y,-trans.z);
	}

	
/* 2.0
 * 	public void keyEvent(KeyEvent ev) {
		if(!enabled) return;
		theKeyEvent=ev;
		
		if(ev.getAction() == KeyEvent.PRESS) keyPressed(ev);
		else if(ev.getAction() == KeyEvent.RELEASE) keyReleased(ev);
	}
	
	public void mouseEvent(MouseEvent ev) {
		if(!enabled) return;
		theMouseEvent=ev;
		if (ev.getAction() == MouseEvent.DRAG) {
			mouseDragged();
		}
	}
	
	  public void keyReleased(KeyEvent ev) {
  	if(UUtil.DEBUGLEVEL>3) UUtil.log("keyReleased "+
  			p.keyEvent.getKeyCode()+" "+(int)p.keyEvent.getKey()+" "+
  			p.CONTROL+" "+PConstants.CODED+" "+(p.key==p.CODED));
  	
  	
  	if(ev.getKeyCode()==p.SHIFT) {
  		shiftIsDown=false;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("SHIFT released");
  	}
  	if(ev.getKeyCode()==p.CONTROL) {
  		ctrlIsDown=false;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("CTRL released");
  	}
  	if(ev.getKeyCode()==p.ALT) {
  		altIsDown=false;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("ALT released");
  	}
  }

  @SuppressWarnings("static-access")
  public void keyPressed(KeyEvent ev) {
    if(UUtil.DEBUGLEVEL>3) UUtil.log("keyPressed "+
        ev.getKeyCode()+" "+
        p.keyEvent.getKeyCode()+" "+(int)p.keyEvent.getKey()+"|"+
        p.CONTROL+" "+PConstants.CODED+" "+(p.key==p.CODED));
    
  	if(ev.getKeyCode()==p.SHIFT) {
  		shiftIsDown=true;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("SHIFT pressed");
  	}
  	if(ev.getKeyCode()==p.CONTROL) {
  		ctrlIsDown=true;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("CTRL pressed");
  	}
  	if(ev.getKeyCode()==p.ALT) {
  		altIsDown=true;
  		if(UUtil.DEBUGLEVEL>3) UUtil.log("ALT pressed");
  	}
  	
  	float mult=1;
  	if(ctrlIsDown) mult=10; 

//    if(p.key==p.CODED) {
      // check to see if CTRL is pressed
      if(p.keyEvent.isControlDown()) {
        // do zoom in the Z axis
        if(ev.getKeyCode()==p.UP) trans.z+=transSpeed*mult;
        if(ev.getKeyCode()==p.DOWN) trans.z-=transSpeed*mult;
        if(ev.getKeyCode()==p.LEFT) rot.z+=p.radians(rotSpeed*mult);
        if(ev.getKeyCode()==p.RIGHT) rot.z-=p.radians(rotSpeed*mult);
      }
      // check to see if CTRL is pressed
      else if(p.keyEvent.isShiftDown()) {
        // do translations in X and Y axis
        if(ev.getKeyCode()==p.UP) trans.y-=transSpeed*mult;;
        if(ev.getKeyCode()==p.DOWN) trans.y+=transSpeed*mult;;
        if(ev.getKeyCode()==p.RIGHT) trans.x+=transSpeed*mult;;
        if(ev.getKeyCode()==p.LEFT) trans.x-=transSpeed*mult;;
      }
      else {
        // do rotations around X and Y axis
        if(ev.getKeyCode()==p.UP) rot.x+=p.radians(rotSpeed*mult);
        if(ev.getKeyCode()==p.DOWN) rot.x-=p.radians(rotSpeed*mult);
        if(ev.getKeyCode()==p.RIGHT) rot.y+=p.radians(rotSpeed*mult);
        if(ev.getKeyCode()==p.LEFT) rot.y-=p.radians(rotSpeed*mult);
      }
      
//    }
    
    if(ctrlIsDown && ev.getKeyCode()==java.awt.event.KeyEvent.VK_HOME) reset();

  }



*
*/	
	
	///////////////////////// 1.5.1
	public void keyEvent(KeyEvent ev) {
		if(!enabled) return;
		
		if(ev.getID() == KeyEvent.KEY_PRESSED) keyPressed();
		else if(ev.getID() == KeyEvent.KEY_RELEASED) keyReleased();
	}
	
	public void mouseEvent(MouseEvent ev) {
		if(!enabled) return;
		if (ev.getID() == MouseEvent.MOUSE_DRAGGED) {
			mouseDragged();
		}
	}

	public void mouseDragged() {
		if(gui!=null && gui.isMouseOver()) return;
		
  	float mult=1;
  	if(p.keyEvent!=null && p.keyEvent.isControlDown()) mult=10; 
  
  	if(p.mouseButton==p.RIGHT && ctrlIsDown) {
  		rot.z+=p.radians(p.mouseX-p.pmouseX)*1*transSpeed;
  	}
  	else if(p.mouseButton==p.RIGHT && !(shiftIsDown || ctrlIsDown)) {
      trans.z+=p.radians(p.mouseY-p.pmouseY)*30*transSpeed*mult;
      return ;
  	}
//		if(isParentPApplet) {
//      rot.y+=p.radians(p.mouseX-p.pmouseX);
//      rot.x+=p.radians(p.mouseY-p.pmouseY);
//      return;
//    }
		
    // if shift is down do pan instead of rotate
    if(shiftIsDown) {
      trans.x+=p.radians(p.mouseX-p.pmouseX)*5*transSpeed*mult;
      trans.y+=p.radians(p.mouseY-p.pmouseY)*5*transSpeed*mult;
    }
    // calculate rot.x and rot.Y by the relative change
    // in mouse position
    else {
      rot.y+=p.radians(p.mouseX-p.pmouseX);
      rot.x+=p.radians(p.mouseY-p.pmouseY);
    }
  }

  public void keyReleased() {
  	if(p.key==p.CODED && p.keyCode==p.SHIFT) {
  		shiftIsDown=false;
//  		UUtil.log("Shift released");
  	}
  	if(p.key==p.CODED && p.keyCode==p.CONTROL) {
  		ctrlIsDown=false;
//  		UUtil.log("Shift released");
  	}
  	if(p.key==p.CODED && p.keyCode==p.ALT) {
  		altIsDown=false;
//  		UUtil.log("Shift released");
  	}
  }

  public void keyPressed() {
  	if(p.key==p.CODED && p.keyCode==p.SHIFT) {
  		shiftIsDown=true;
//  		UUtil.log("Shift down");
  	}
  	if(p.key==p.CODED && p.keyCode==p.CONTROL) {
  		ctrlIsDown=true;
//  		UUtil.log("Shift released");
  	}
  	if(p.key==p.CODED && p.keyCode==p.ALT) {
  		altIsDown=true;
//  		UUtil.log("Shift released");
  	}
  	
  	float mult=1;
  	if(p.keyEvent!=null && p.keyEvent.isControlDown()) mult=10; 

    if(p.key==p.CODED) {
      // check to see if CTRL is pressed
      if(p.keyEvent.isControlDown()) {
        // do zoom in the Z axis
        if(p.keyCode==p.UP) trans.z+=transSpeed*mult;
        if(p.keyCode==p.DOWN) trans.z-=transSpeed*mult;
        if(p.keyCode==p.LEFT) rot.z+=p.radians(rotSpeed*mult);
        if(p.keyCode==p.RIGHT) rot.z-=p.radians(rotSpeed*mult);
      }
      // check to see if CTRL is pressed
      else if(p.keyEvent.isShiftDown()) {
        // do translations in X and Y axis
        if(p.keyCode==p.UP) trans.y-=transSpeed*mult;;
        if(p.keyCode==p.DOWN) trans.y+=transSpeed*mult;;
        if(p.keyCode==p.RIGHT) trans.x+=transSpeed*mult;;
        if(p.keyCode==p.LEFT) trans.x-=transSpeed*mult;;
      }
      else {
        // do rotations around X and Y axis
        if(p.keyCode==p.UP) rot.x+=p.radians(rotSpeed*mult);
        if(p.keyCode==p.DOWN) rot.x-=p.radians(rotSpeed*mult);
        if(p.keyCode==p.RIGHT) rot.y+=p.radians(rotSpeed*mult);
        if(p.keyCode==p.LEFT) rot.y-=p.radians(rotSpeed*mult);
      }
      
      if(ctrlIsDown && p.keyCode==KeyEvent.VK_HOME) reset();
    }
    else {
      if(p.keyEvent.isControlDown()) {
        if(p.keyCode=='R') {
//        	reset();
        }
      }
    }
  }

	public UNav3D reset() {
//		if(UUtil.DEBUGLEVEL>3) 
			p.println("Reset transformations. "+transReset);
		trans.set(transReset);
		rot.set(rotReset);
	  return this;
	}
  
  public UNav3D setTranslation(UVec3 vv) {
	  return setTranslation(vv.x,vv.y,vv.z);
  }

  public UNav3D set(UNav3D nv) {
	  setTranslation(nv.trans);
	  setRotation(nv.rot);
	  return this;
  }

  public UNav3D setTranslation(float x,float y,float z) {
	  trans.set(x,y,z);
	  transReset.set(trans);
	  return this;
  }

  public UNav3D setRotation(UVec3 vv) {
  	setRotation(vv.x,vv.y,vv.z);
	  return this;
  }

  public UNav3D setRotation(float x,float y,float z) {
	  rot.set(x,y,z);
	  rotReset.set(rot);
	  return this;
  }

  public UNav3D addRotation(float x,float y,float z) {
	  rot.add(x,y,z);
	  rotReset.set(rot);
	  return this;
  }

  public void mouseWheel(float step) {
		if(!enabled) return;
		
//  	if(p.keyEvent!=null && p.keyEvent.isControlDown())
//  		trans.z=trans.z+step*50;
//  	else 
  		trans.z=trans.z+step*5;
  }
  
  public String toStringData() {
  	return "[UNav3D "+trans.toString()+" "+rot.toString()+"]";
}


	// utility class to handle mouse wheel events
	class MouseWheelInput implements MouseWheelListener{
		public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
	    mouseWheel(e.getWheelRotation());
	  }	
	}


	public void interpolate(float camT, UNav3D cam1, UNav3D cam2) {
		UVec3 tD,rD;
		tD=new UVec3(cam2.trans).sub(cam1.trans).mult(camT);
		rD=new UVec3(cam2.rot).sub(cam1.rot).mult(camT);
		
		trans.set(cam1.trans).add(tD);
		rot.set(cam1.rot).add(rD);		
	}

	public void set(String s) {
		s=s.substring(5,s.length()-1);
		UUtil.log("UNav3D.set '"+s+"'");
		String tok[]=p.split(s, ' ');
		trans=UVec3.parse(tok[1]);
		rot=UVec3.parse(tok[2]);
		
//		trans=UVec3.parse("<"+tok[0].substring(0,tok[0].indexOf('>')));
//		rot=UVec3.parse("<"+tok[0].substring(0,tok[0].indexOf('>')));
		// TODO Auto-generated method stub
		
	}

	public void enable() {
		enabled=true;		
	}

	public void disable() {
		enabled=false;		
	}

}
