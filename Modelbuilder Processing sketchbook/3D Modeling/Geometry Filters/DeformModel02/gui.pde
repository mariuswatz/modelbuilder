USimpleGUI gui;
UNav3D nav;

public void initGUI() {
  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2+200, 0);
  nav.setRotation(PI, 0, 0);

  gui=new USimpleGUI(this);
  gui.addSlider("amount", amount, -1, 1);
  gui.addSlider("u", u, 2, 30);
  gui.addSlider("v", v, 2, 30);
  gui.newRow();
  gui.addSlider("num", num, 1, 18);
  gui.addSlider("offs", offs, -600, 600);
  gui.addSlider("sz", sz, 0.1f, 10);
  gui.newRow();
  gui.addButton("rotX").addButton("rotY").addButton("rotZ");
  gui.addButton("bend").addButton("taper").addButton("twist");
  gui.newRow();
  gui.addButton("resetForm");
  gui.addButton("loadSTL");
  gui.addButton("saveSTL");
  gui.setLayout(false);
}

// demonstrates how to load a STL file using a file chooser dialog
public void loadSTL() {
  String filename=UIO.getFilenameChooserDialog(this, sketchPath);
  if (filename.endsWith("stl")) {
    loadedModel=UGeometry.readSTL(this, filename);
    loadedModel.setDimensions(height);
    loadedModel.center().calcBounds();
    loadedModel.translate(0, -geo.bb.min.y, 0);			
    resetForm();
  }
}


// demonstrates adding different copies of the same model to
// produce a composite UGeometry for saving to file.
public void saveSTL() {
  String nameFormat=this.getClass().getSimpleName();
  nameFormat=nameFormat+" ####.png";

  String filename=UIO.getIncrementalFilename(nameFormat, sketchPath);
  saveFrame(filename);

  println("Saved '"+filename+"'");
  UGeometry tmp=new UGeometry();
  for (int i=0; i<num; i++) {
    tmp.add(
    new UGeometry(geo).
      scale(sz).
      translate(offs, 0, 0).
      rotateY(TWO_PI*(float)i/(float)(num)));
  }

  tmp.writeSTL(this, UIO.noExt(filename)+".stl");
}

