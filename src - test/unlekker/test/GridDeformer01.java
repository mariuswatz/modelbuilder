package unlekker.test;

import processing.core.*; 
import processing.xml.*; 

import controlP5.*; 
import processing.opengl.*; 
import unlekker.test.*; 
import unlekker.util.*; 
import unlekker.modelbuilder.*; 
import ec.util.*; 
import processing.opengl.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class GridDeformer01 extends PApplet {


// Code written by Claus Rytter Bruun de Neergaard
// May, 2012, http://www.cargocollective.com/clausneergaard

	
/**
 * MW 2012.0518
 * NEW Features:
 * - Uses a UVertexList of points to deform mesh - currently a line
 * but could be any shape
 * - Use CTRL and mouse left/right to rotate line of deforming points
 * - Use CTRL and mouse up/down to change radius of deforming points
 * - Press ALT to use mouse to control camera
 * - "force" is now always positive - when left clicking points are
 * attracted, when right clicking poinst are repulsed
 * 
 * - buildLineDeformer builds line of deformer points every frame
 * - drawLineDeformer draws the deformer points overlaid on the mesh
 * - findZ finds the mesh Z for any position [0..width-1,0..height-1]
 */
	
int step = 10;
float radius=50, force=0.1f;
float lineRot=0,lineZ=-5;
boolean drawMesh=true,isCTRLon,isALTon;

PFont fnt;
UNav3D nav;

public void setup() {
  size(800, 500, OPENGL);
  smooth();
  noStroke();
  fill(100, 100);
  smooth();

  build();
  initGUI();

  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2, 0);
  nav.setRotation(radians(30), radians(30), 0);
  nav.unregisterMouseEvents();

  fnt=createFont("Arial", 18);
  textFont(fnt);
}

int type=0;
public void draw() {
  background(255);

  fill(100);
  pushMatrix();
  lights();
  nav.doTransforms();
  mesh();
  popMatrix();

  gui.draw();
  //  fill(0);
  //  text("fps "+(float)frameRate, 20, 20);
}

public void mesh() {

	buildLineDeformer();
	
  if (mousePressed && !(isCTRLon || isALTon || gui.cp.window(this).isMouseOver())) {
    deform();
  }

  if (!drawMesh) {
    for (int i=0; i<mesh.vert.n; i++) {
      pushMatrix();
      translate(mesh.vert.v[i].x, mesh.vert.v[i].y, mesh.vert.v[i].z);
      ellipse(0, 0, 4.5f, 4.5f);
      popMatrix();
    }
  }
  else {
    noFill();
    fill(150,200);
    stroke(150,50);
    mesh.draw(this);
  }

  
  drawLineDeformer();
}

private void drawLineDeformer() {
	stroke(255,100,0, 150);
  noFill();
  hint(DISABLE_DEPTH_TEST);
  for(int i=0; i<defline.n; i++) {
    pushMatrix();
    translate(defline.v[i].x, defline.v[i].y, defline.v[i].z+5);
    ellipse(0, 0, radius, radius);
    popMatrix();  	
  }
  hint(ENABLE_DEPTH_TEST);
}

private void deform() {
  int nx, ny;
  float mx,my;
  UVec3 vv, mouse=new UVec3();

  nx=(int)map(mouseX, 0, width-1, 0, numx-1);
  ny=(int)map(mouseY, 0, height-1, 0, numy-1);
  mx=map(mouseX, 0, width-1, -0.5f, 0.5f)*(float)((numx-1)*step);
  my=map(mouseY, 0, height-1, -0.5f, 0.5f)*(float)((numy-1)*step);

	if (mouseButton == LEFT) force=-abs(force);

	for(int j=0; j<defline.n; j++) {
		for (int i=0; i<mesh.vert.n; i++) {
		  vv=mesh.vert.v[i];
		  mouse.set(defline.v[j]).sub(vv);
	
		  float d=mouse.length();
		  if (d<radius) {
		    d=(radius-d)/radius;
		    //          d=sq(sq(1-(radius-d)/radius))*force;
		    d=d*d*d*d*force;
		    //          d*=force;
		    vv.add(mouse.mult(d, d, d*5));
		  }
		}
	}

	force=abs(force);
}

UGeometry mesh;
int numx,numy; 
UVec3 meshMin,meshMax;

public void build() {
  UVertexList horizontal=new UVertexList();
  // create master list for horizontal
  for (int x = step; x < width-step; x += step) 
    horizontal.add(x,0,0);
  numx=horizontal.n;
    
  // get an array of empty but initialized UVertexList
  UVertexList [] hl=UVertexList.getVertexLists((height-2*step)/step+1);
  numy=hl.length;

  // only rebuild if different grid size
  if(mesh!=null && mesh.vert.n==numx*numy) {
    println("same grid size "+step);
    return;
  }


  mesh=new UGeometry();
  mesh.vert.doNoDuplicates=true;
  
  for(int i=0; i<hl.length; i++) {
    hl[i]=new UVertexList(horizontal);
    hl[i].translate(0,(i+1)*step,0);
    mesh.vert.add(hl[i]);    
  }

  
  // create mesh by making a quadstrip between every UVertexList
  // in hl array
  mesh.quadStrip(hl); 
  mesh.center();
  mesh.calcBounds();
  meshMin=new UVec3(mesh.bb.min);
  meshMax=new UVec3(mesh.bb.max);
  
  println(numx+" "+numy+" "+mesh.vert.n);
  for(int i=0; i<numx*2; i++) println(i+" "+mesh.vert.v[i]);
}
USimpleGUI gui;

public void initGUI() {
  gui=new USimpleGUI(this);
  
  gui.addSlider("step", step, 5,50);
  gui.addSlider("radius", radius, 10,200);
  gui.addSlider("force", force, 0,0.4f);
  
  gui.newRow();
  gui.addSlider("lineRot", lineRot, 0,360);
  gui.addSlider("lineZ", lineZ, -200,200);

  gui.addToggle("drawMesh", drawMesh);
  gui.addButton("build");
  gui.addButton("saveSTL");
  
  gui.setLayout(false);
}

public void saveSTL() {
  String nameFormat=this.getClass().getSimpleName();
  nameFormat=nameFormat+" ####.png";

  String filename=UIO.getIncrementalFilename(nameFormat, sketchPath);
  saveFrame(filename);

  println("Saved '"+filename+"'");
  mesh.writeSTL(this, UIO.noExt(filename)+".stl");
}

UVertexList defline;

public void buildLineDeformer() {
	defline=new UVertexList();
  
  for(float i=0; i<11; i++) defline.add(-0.5f+i/10f,0,0);
  defline.scale(radius*5).rotateZ(radians(lineRot));
  
  lineZ=findZ(defline.v[5])-radius*0.25f;
  gui.cp.controller("lineZ").setValue(lineZ);

  defline.translate(mouseX,mouseY,0);
  for(int i=0; i<defline.n; i++) defline.v[i].z=findZ(defline.v[i])-radius*0.5f;

  defline.translate(-mouseX,-mouseY,0);
  defline.translate(
  			map(mouseX,0, width-1, meshMin.x,meshMax.x),
  			map(mouseY,0, height-1, meshMin.y,meshMax.y),
  			0);
  
}

public void keyPressed() {
	if(keyCode==CONTROL) isCTRLon=true;
	if(keyCode==ALT) isALTon=true;
}

public void keyReleased() {
	if(keyCode==CONTROL) isCTRLon=false;
	if(keyCode==ALT) isALTon=false;
}

public void mouseDragged() {
	// return if over a gui element
	if(gui.cp.window(this).isMouseOver()) return;
	
	if(isCTRLon) {
		// update lineRot
		lineRot+=(float)(mouseX-pmouseX);
		
		// update radius
		radius+=constrain((float)(mouseY-pmouseY)/10, -2,2);
		
		// update GUI with new values
	  gui.cp.controller("lineRot").setValue(lineRot);
	  gui.cp.controller("radius").setValue(radius);

	}

	if(isALTon) {
		// allow camera manipulation by the mouse
		nav.mouseDragged();
	}

}

public float findZ(UVec3 pt) {
  int nx,ny;
    
  // constrain values, because they can be off the grid
  float mx=constrain(pt.x,0,width-1);
  float my=constrain(pt.y,0,height-1);
  
  nx=(int)map(mx, 0, width-1, 0, numx-1);
  ny=(int)map(my, 0, height-1, 0, numy-1);
  return mesh.vert.v[nx+ny*numx].z;
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#666666", "--hide-stop", "unlekker.test.GridDeformer01" });
  }
}
