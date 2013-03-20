ControlP5 controlP5; // instance of the controlP5 library
USimpleGUI gui;

void initGUI() {
  nav=new UNav3D(this);
  nav.setTranslation(width/2,height/2,0);
  
  gui=new USimpleGUI(this);
  gui.addSlider("num", num, 10,72);
  gui.addSlider("numSeg", numSeg, 10,100);
  gui.addSlider("totalH", totalH, 25,500);
  gui.addSlider("maxRad", maxRad, 10,500);
  gui.addSlider("minRad", minRad, 10,500);
  gui.addSlider("shapeStyle",shapeStyle, 1,shapeNames.length-1);
  
   // add "bang" button to randomize spheres
   gui.addButton("rndForm");   
   // add "bang" button to randomize spheres
    gui.addButton("rndForm");     
   // add "bang" button to save STL
   gui.addButton("stlSave");
   
  nav.setGUI(gui);
}

void controlEvent(ControlEvent theEvent) {
  boolean [] rebuildFlag={
    false,true,true,true,true,
    true,true,false,true,true};
    
  Controller c=theEvent.controller();
  int cid=c.id();
  
  if (theEvent.isFrom(gui.cp.getController("numSeg"))) {
    numSeg=(int)c.value();
    numSeg2=numSeg*2;
  }

  if (theEvent.isFrom(gui.cp.getController("totalH"))) {
    totalH=(int)c.value();
  }
  if (theEvent.isFrom(gui.cp.getController("shapeStyle"))) {
    // don't call initShaper if it's the existing shaper style. this
    // prevents continuous calls to initShaper when the slider is
    // used rather than the rndShaper bang button.
    if((int)c.value()==oldShaper) return;
    shapeStyle=(int)c.value();
    initShaper();
  }

  // flag that a rebuild is needed?
  if(cid<10 && rebuildFlag[cid]) {
    doRebuild=true;
  }   
}

void stlSave() {
  model.writeSTL(this, 
    UIO.getIncrementalFilename(
      this.getClass().getSimpleName()+
      "-####.stl",sketchPath));
}
