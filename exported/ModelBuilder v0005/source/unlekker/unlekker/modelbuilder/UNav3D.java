package unlekker.modelbuilder;

import java.awt.event.*;

import processing.core.*;

public class UNav3D {
	public PApplet p;
	public UVec3 rot,trans;

	public UNav3D(PApplet _p) {
		p=_p;
		rot=new UVec3(0,0,0);
		trans=new UVec3(0,0,0);
		
	  // code to allow us to use the mouse wheel
	  p.frame.addMouseWheelListener(new MouseWheelInput());
	}
	
	public void doTransforms() { 
  	p.translate(trans.x,trans.y,trans.z);
  	if(rot.y!=0) p.rotateY(rot.y);
  	if(rot.x!=0) p.rotateX(rot.x);
  	if(rot.z!=0) p.rotateZ(rot.z);
  }
  
	public void mouseDragged() {
    // calculate rot.x and rot.Y by the relative change
    // in mouse position
    if(p.keyEvent!=null && p.keyEvent.isShiftDown()) {
      trans.x+=p.radians(p.mouseX-p.pmouseX)*10;
      trans.y+=p.radians(p.mouseY-p.pmouseY)*10;
    }
    else {
      rot.y+=p.radians(p.mouseX-p.pmouseX);
      rot.x+=p.radians(p.mouseY-p.pmouseY);
    }
  }
  
  public void keyPressed() {
    if(p.key==p.CODED) {
      // check to see if CTRL is pressed
      if(p.keyEvent.isControlDown()) {
        // do zoom in the Z axis
        if(p.keyCode==p.UP) trans.z=trans.z+2;
        if(p.keyCode==p.DOWN) trans.z=trans.z-2;
      }
      // check to see if CTRL is pressed
      else if(p.keyEvent.isShiftDown()) {
        // do translations in X and Y axis
        if(p.keyCode==p.UP) trans.y=trans.y-2;
        if(p.keyCode==p.DOWN) trans.y=trans.y+2;
        if(p.keyCode==p.RIGHT) trans.x=trans.x+2;
        if(p.keyCode==p.LEFT) trans.x=trans.x-2;
      }
      else {
        // do rotations around X and Y axis
        if(p.keyCode==p.UP) rot.x+=p.radians(2);
        if(p.keyCode==p.DOWN) rot.x-=p.radians(2);
        if(p.keyCode==p.RIGHT) rot.y+=p.radians(2);
        if(p.keyCode==p.LEFT) rot.y-=p.radians(2);
      }
    }
    else {
      if(p.keyEvent.isControlDown()) {
        if(p.keyCode=='R') {
        	p.println("Reset transformations.");
          trans.x=0;
          trans.y=0;
          trans.y=0;
          rot.x=0;
          rot.y=0;
        }
      }
    }
  }

  public void mouseWheel(float step) {
    trans.z=trans.z+step*5;
  }

	// utility class to handle mouse wheel events
	class MouseWheelInput implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e) {
	    mouseWheel(e.getWheelRotation());
	  }
	
	}
}
