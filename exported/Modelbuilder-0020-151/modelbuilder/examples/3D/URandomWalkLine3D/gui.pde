public void initGUI() {
  gui=new USimpleGUI(this);
  gui.addSlider("maxRot", maxRot, 10, 120);
  gui.addSlider("length", length, 30, 150);
  gui.addSlider("buildSpeed", buildSpeed, 1, 50);

  gui.addButton("build");
  gui.addButton("stopBuild");
  gui.addButton("clear");
  gui.addButton("saveSTL");
  gui.addButton("saveImage");
  
  gui.cpw=180;
}

public void build() {
  doBuild=true;
}

public void stopBuild() {
  doBuild=false;
}

public void saveSTL() {
  model.writeSTL(this, 
  UIO.getIncrementalFilename(
  this.getClass().getSimpleName()+" ###.stl", 
  sketchPath));
}

public void saveImage() {
  saveFrame(
  UIO.getIncrementalFilename(
  this.getClass().getSimpleName()+" ###.png", sketchPath));
}

