import processing.core.*; 
import processing.xml.*; 

import processing.pdf.*; 
import controlP5.*; 
import java.util.ArrayList; 
import unlekker.modelbuilder.*; 
import unlekker.util.*; 

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

public class UCracking02BP5 extends PApplet {



/**
 * UCracking2AB.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Cracking (aka subdivision) of triangular geometry. Uses 
 * ArrayList to efficiently handle storage of new triangles 
 * while removing old ones after they've been subdivided. 
 *
 * The starting shape is built using a UGeometry instance
 * built from an array of vertex lists. UFace instances are
 * extracted and used to initialize the custom Poly class,
 * allowing us to construct a complex starting form.
 */
 








public ArrayList<Poly> f;
Poly lastPoly=null;
public boolean doCrack,doSave;
public boolean randomCrack=false,doRecolor;
public float nMod=4;
public USimpleGUI gui;
String filename;
public int subdiv=0,rings=9;

public void setup() {
  size(1000, 1000,P2D);
  
  gui=new USimpleGUI(this);
  gui.addButton("reinit");
  gui.addSlider("nMod",nMod, 0.1f,10);
  gui.addSlider("rings",rings, 4,30);
  
  gui.addRadioButton("subdivType",
    new String[] {"type 1","type 2","type 3"},100);
  gui.addButton("initColors");
    gui.addButton("savePDF");

  gui.addToggle("doCrack",doCrack); // toggle continuous cracking
  gui.addToggle("randomCrack",randomCrack); // crack at random?
  
  reinit();
  smooth();
}

public void savePDF() {
  doSave=true;
}

public void draw() {
  background(0);
  
  if(!sketchPath.endsWith("save")) sketchPath=sketchPath+"/save";
  
  if(doSave) {
    filename=UIO.getIncrementalFilename(
      this.getClass().getSimpleName()+" ###.png",
      sketchPath);
    
    println(g.getClass().getSimpleName());
    if(g.getClass().getSimpleName().indexOf("2D")!=-1)
      beginRecord(PDF,UIO.noExt(filename)+".pdf");
    else beginRaw(PDF,UIO.noExt(filename)+".pdf");
    println("filename "+filename);
  }

  pushMatrix();
  translate(width/2,height/2);
  
  if (doCrack) 
    // crack 20 times if randomCrac==false, 40 times if true
    for(int i=0; i<20+(randomCrack ? 80 : 0); i++) crack();
  
  // handle recoloring
  if(doRecolor && f!=null) {
    for(Poly pp:f) pp.initColors();
    doRecolor=false;
  }
  
//  stroke(255);
  noStroke();
  for (Poly pp:f) pp.draw();

  if(doSave && filename!=null) {
      popMatrix();
    if(g.getClass().getSimpleName().indexOf("2D")!=-1)
      endRecord();
    else endRaw();
    
    saveFrame(UIO.noExt(filename)+".png");
    doSave=false;
    filename=null;
  }
  
  else {
    popMatrix();
    gui.draw();
  }
}


public void controlEvent(ControlEvent ev) {
  // handle ControlGroup event for the radio buttons
  if(ev.isGroup()) {
    for(int i=0;i<ev.group().arrayValue().length;i++) {
      if(ev.group().arrayValue()[i]>0) subdiv=i;
    }
  }
  else {
    if(ev.controller().name().equals("doCrack")) {
      if(f.size()>0) lastPoly=f.get(f.size()-1);
    }
  }
}

public void keyPressed() {
  // CTRL-A == initColors();
  if(keyEvent.isControlDown() && keyCode==KeyEvent.VK_A) initColors();
  if(keyEvent.isControlDown() && keyCode==KeyEvent.VK_N) reinit();
    if(!online && key=='s') doSave=true;

}

public void mousePressed() {
  if(!gui.isMouseOver()) crack();
}

class Poly {
  UVec3 v[];
  int col;

  Poly(UVec3 v1, UVec3 v2, UVec3 v3) {
    v=new UVec3[3];
    v[0]=v1;
    v[1]=v2;
    v[2]=v3;

    initColors();
  }

  public void initColors() {
    col=colors.getRandomColor();
    
    if(random(100)>75) 
      col=colors.adjustBrightness(col,
        (random(100)>66 ? random(0.5f,0.8f) : random(1.5f,2)));
        
//     col=color(random(200,255));
  }
  
  public void draw() {
    fill(col);

    beginShape(TRIANGLES);
    vertex(v[0].x, v[0].y);
    vertex(v[1].x, v[1].y);
    vertex(v[2].x, v[2].y);
    endShape();
  }

  // calculate random point in triangle, tending towards the
  // center of the triangle surface
  public UVec3 calcRandomPoint() {
    UVec3 vv=UVec3.interpolate(v[1], v[2], UUtil.rnd.random(0.33f, 0.66f));
    vv=UVec3.interpolate(v[0], vv, UUtil.rnd.random(0.33f, 0.66f));
    return vv;
  }

  // subdivide Poly by calculating a random point in the triangle
  // face and using it to produce three new Poly instances
  public Poly[] subdivide() {
    UVec3 vv,vv2,vv3;
    Poly p[];
    
    subdiv=(int)random(3);
    // random center point
    if(subdiv==0) {
      p=new Poly[3];
      vv=calcRandomPoint();
      p[0]=new Poly(v[0], vv, v[1]);
      p[1]=new Poly(v[1], v[2], vv);
      p[2]=new Poly(v[2], v[0], vv);
      return p;
    }

    // mid-edge subdivision rule
    if(subdiv==1) {
      p=new Poly[4];
      int id=0;
      float randOffs=0.1f;
      vv=UVec3.interpolate(v[id],v[((id++)+1)%3],
        random(-1,1)*randOffs+0.5f); // mid-point offset
      vv2=UVec3.interpolate(v[id],v[((id++)+1)%3],
        random(-1,1)*randOffs+0.5f); // mid-point offset
      vv3=UVec3.interpolate(v[id],v[(id+1)%3],
        random(-1,1)*randOffs+0.5f); // mid-point offset
      p[0]=new Poly(v[0], vv, vv3);
      p[1]=new Poly(vv,v[1], vv2);
      p[2]=new Poly(v[2], vv2, vv3);
      p[3]=new Poly(vv, vv2, vv3);
      return p;
    }
    
    // halving strategy
    int id=(int)random(3);
    vv=UVec3.interpolate(v[id],v[(id+1)%3],0.5f);
    p=new Poly[2];
    p[0]=new Poly(v[id], vv, v[(id+2)%3]);
    p[1]=new Poly(v[(id+1)%3], v[(id+2)%3], vv);
//    p[2]=new Poly(v[2], v[0], vv);
    return p;
  }
}

UColorTool colors;
float hueOffs;

public void initColors() {
  colors=new UColorTool();
  
  // start by creating lots of colors
  colors.addGradient(5,10,"FF0099","FF0033");
  colors.addGradient(5,10,"FFFF00","FF3300");
  colors.addGradient(5,10,"00FFFF","00496A");
  colors.addGradient(5,10,"293236","00496A");
  colors.addGradient(5,10,"166A00","44FC12");
  colors.addGradient(5,10,"FFFFFF","CCFFFF");
  colors.generateColors(10,40);
  
  // force a limited selection from that large palette
  int cc[]=new int[min(colors.n,(int)random(3,20))];
  for(int i=1; i<cc.length; i++) {
    cc[i]=colors.getRandomColor();
//    cc[i]=colors.adjustHue(cc[i],hueOffs);
  }
  cc[0]=color(255);
  for(int i=0; i<cc.length; i++) println(UColorTool.toHex(cc[i]));
  
  // set colors to that limited palette
  colors.colors=cc;
  colors.n=cc.length;
  
  doRecolor=true;  
}

// initializes triangle surface in one of two configurations
public void reinit() {
  initColors();
  
  f=new ArrayList<Poly>();
  ((Toggle)gui.cp.controller("doCrack")).setState(false);

  // n = # of points around circumference
  // vl.length = # of "rings"
  float n=(int)random(4,7)*6;
//  n=60;

//  UVertexList vl[]=UVertexList.getVertexLists((int)random(7,13)+6);
  UVertexList vl[]=UVertexList.getVertexLists(rings);

  // radial offset controlled by a sin() function
  float sineOffs=random(0.05f,0.1f)*0.05f*(float)height;
  sineOffs=random(0.25f,1);
  // # of phases the sin() offset goes through
  float sinePhases=(int)random(2,6)*2;
  sinePhases=(int)random(3,9);
//  n+=sinePhases*3;

  n=(int)((float)n*nMod);
  
  float hh=dist(width/2,height/2,0,0)/(float)(height);
  // calculate concentric vertex lists, with a sin() offset
    for(int j=0; j<vl.length; j++) {
      int nn;
      
      nn=(int)n;
//      if((float)j<(float)vl.length*0.5f) nn/=4;
//      else if((float)j<(float)vl.length*0.66f) nn/=2;
//      nn+=(nn%2);
      println(j+" "+nn+" "+(int)n);
      
    for(int i=0; i<nn; i++) {
      float a=map(i,0,nn-1,0,TWO_PI);
      float h=(1-sq(1-map(j,0,vl.length-1,0.05f,1)))*hh;
      h=map(j,0,vl.length-1,0.05f,1);
      h=1-bezierPoint(0.4f, 0.8f, 1, 1, h);
      
      float b=((a*sinePhases)%PI)/PI;
//      println("b "+b);
//      if(b>1) b=(1-(b-1))*sineOffs;
//      else b=b*sineOffs*20;
//      println("b "+b);
//      b=sin(b*PI);
      if(b<0.5f) {
        b*=2;
        b=bezierPoint(0,0.75f,1,1, b);
      }
      else {
        b=(b-0.5f)*2;
        b=bezierPoint(1,1,0.75f,0, b);
      }
      h*=b+sineOffs;
      h*=height;
//      h+=(sin(a*sinePhases)*b*sineOffs);
      
      vl[j].add(new UVec3(h,0,0).
        rotate(a).add(width/2,height/2));
//      if(i==n-1) vl[j].close();
  }
//    if(j%3==1) n*=2;
//    println(j+" "+n);
  }
  
  UGeometry geo=new UGeometry();
  geo.noDuplicates();
  
  // center triangle fan
  geo.triangleFan(vl[0],true,false);
  
  for(int i=0; i<vl.length-1; i++) 
    if(vl[i].n==vl[i+1].n) {
      geo.quadStrip(vl[i],vl[i+1]);
    }
    else {
      geo.beginShape(TRIANGLES);
    for(int j=0; j<vl[i].n; j++) {
//      println(j+" "+(j*2)+" "+vl[i+1].n+" "+vl[i);

      int nextJ=j*2;
      if(i<vl.length/2 && (i+1)<vl.length/2) nextJ*=2;
      
      geo.vertex(vl[i].v[j].x,vl[i].v[j].y,0);
      geo.vertex(vl[i+1].v[nextJ].x,vl[i+1].v[nextJ].y,0);
      geo.vertex(vl[i+1].v[nextJ+1].x,vl[i+1].v[nextJ+1].y,0);

      if(j<vl[i].n-1) {
      geo.vertex(vl[i].v[j].x,vl[i].v[j].y,0);
      geo.vertex(vl[i].v[j+1].x,vl[i].v[j+1].y,0);
      geo.vertex(vl[i+1].v[nextJ+1].x,vl[i+1].v[nextJ+1].y,0);
      }      
      if(j>0) {
      geo.vertex(vl[i].v[j].x,vl[i].v[j].y,0);
      geo.vertex(vl[i+1].v[nextJ].x,vl[i+1].v[nextJ].y,0);
      geo.vertex(vl[i+1].v[nextJ-1].x,vl[i+1].v[nextJ-1].y,0);
      }      

    }
  geo.endShape();
  }
//  // quadstrip all the rings
//  geo.quadStrip(vl);
    
      geo.center().setDimensions(width);

  // create Poly instances from all the resulting UFaces
  for(int i=0; i<geo.faceNum; i++) {
    UVec3 vv[]=geo.face[i].v;
    f.add(new Poly(vv[0],vv[1],vv[2]));
  }
}

public void crack() {
  ArrayList<Poly> ff=new ArrayList<Poly>();

  Poly pp;
  randomCrack=
    ((Toggle)gui.cp.controller("randomCrack")).getState();
  
  if(randomCrack) {
    int id=(int)(sq(random(1))*(float)f.size());
    if(random(100)>60) id=(int)random(f.size()/4);
    pp=f.get(id);
  }
  else  // crack in sequence, oldest first
    pp=f.get(0);
    if(lastPoly!=null && pp==lastPoly) {
      ((Toggle)gui.cp.controller("doCrack")).toggle();
      lastPoly=null;
      println("doCrack done - "+lastPoly);
      return;
    }
  
  // subdivide triangle to produce new triangles
  Poly p[]=pp.subdivide();
  
  // remove old triangle from arraylist
  f.remove(pp);
  
  // add the new triangles to arraylist
  for (int i=0; i<p.length; i++) {    
    UVec3 pv[]=p[i].v;
    int outCnt=0;
    
    // check if all three vertices are outside canvas
//    for(int j=0; j<3; j++)
//      if((pv[j].x<-50 || pv[j].x>width+50)
//        || (pv[j].y<-50 || pv[j].y>height+50)) outCnt++;
      
    if(outCnt<3) f.add(p[i]);
    else println("Poly rejected: "+UUtil.toString(pv));
  }
}

  
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "UCracking02BP5" });
  }
}
