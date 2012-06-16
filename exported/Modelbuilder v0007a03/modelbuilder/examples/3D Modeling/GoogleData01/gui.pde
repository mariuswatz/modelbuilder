USimpleGUI gui;
float h=400;
int res=12,rows=10;
boolean doRebuild;

void initGUI() {
  gui=new USimpleGUI(this);
  gui.addButton("saveSTL");
  gui.setLayout(false);
}

void rebuild() {
  doRebuild=true;
}

void saveSTL() {
  String filename=UIO.
    getIncrementalFilename(
      this.getClass().getSimpleName()+" ###.stl", sketchPath);
  geo.writeSTL(this, filename);
  saveFrame(UIO.noExt(filename)+".png");
}

void controlEvent(ControlEvent ev) {
  if(ev.controller().name().compareTo("h")==0) {
    geo.scale(1,h/buildH,1);
    buildH=h;
  }
}
