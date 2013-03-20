USimpleGUI gui;
UNav3D nav;
boolean doRandomAttractor=false;

public void initGUI() {
  gui=new USimpleGUI(this);
  gui.addSlider("amount", amount, -1, 1);
  gui.addSlider("u", u, 4, 60);
  gui.addSlider("v", v, 4, 60);
  gui.addButton("subdivide");
  gui.addToggle("subdivConditional", subdivConditional);
  gui.newRow();
  gui.addSlider("radius", radius, 10, 200);
  gui.addSlider("force", force, 0.01f, 1);
  gui.addButton("attractor").addButton("repulsor");
  gui.newRow();
  gui.addButton("resetForm");
  gui.addButton("loadSTL");
  gui.addButton("saveSTL");
  gui.setLayout(false);
  
  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2, 0);
  nav.setRotation(PI, 0, 0);

  nav.setGUI(gui);
}


// demonstrates how to load a STL file using a file chooser dialog
public void loadSTL() {
  String filename=UIO.getFilenameChooserDialog(this, sketchPath);
  if (filename.toLowerCase().endsWith("stl")) {
    loadedModel=UGeometry.readSTL(this, filename);
    loadedModel.setDimensions(height);
    loadedModel.center().calcBounds();
    loadedModel.translate(0, -geo.bb.min.y, 0);			
    resetForm();
  }
}


public void saveSTL() {
  String nameFormat=this.getClass().getSimpleName();
  nameFormat=nameFormat+" ####.png";

  String filename=UIO.getIncrementalFilename(nameFormat, sketchPath);
  saveFrame(filename);

  println("Saved '"+filename+"'");
  geo.writeSTL(this, UIO.noExt(filename)+".stl");
}


public void keyPressed() {
  //  if (key==CODED) {
  if (keyCode==java.awt.event.KeyEvent.VK_F1) posIndex=(posIndex+30)%geo.vert.n;
  else if(keyCode==java.awt.event.KeyEvent.VK_F2) {
    posIndex-=30;
    if (posIndex<0) posIndex+=geo.vert.n-1;
  }
  
  
  if (keyCode==ENTER) doRandomAttractor=true;
  if (key==' ') posIndex=(int)random(geo.vert.n);
  setAttractorPosition();
}

