ControlP5 controlP5; // instance of the controlP5 library

void initGUI() {
  nav=new UNav3D(this);
  nav.setTranslation(width/2,height/2,0);
  
  controlP5 = new ControlP5(this);
  controlP5.addSlider("num", // name, must match variable name
    10,72, // min and max values
    num, // the default value
    20,20, // X,Y position of slider
    80,13) // width and height of slider
    .setId(0); // set controller ID
    
  controlP5.addSlider("numSeg", // name, must match variable name
    10,100, // min and max values
    numSeg, // the default value
    20,40, // X,Y position of slider
    80,13) // width and height of slider
    .setId(1); // set controller ID
  numSeg2=numSeg*2;

  totalH=200;
  controlP5.addSlider("totalH", // name, must match variable name
    25,500, // min and max values
    totalH, // the default value
    150,20, // X,Y position of slider
    80,13). // width and height of slider
    setId(4);

  maxRad=200;
  controlP5.addSlider("maxRad", // name, must match variable name
    10,500, // min and max values
    maxRad, // the default value
    150,40, // X,Y position of slider
    80,13). // width and height of slider
    setId(8);

  shapeSlider=controlP5.addSlider("shapeStyle", // name, must match variable name
    1,shapeNames.length, // min and max values
    1, // the default value
    150,60, // X,Y position of slider
    80,13); // width and height of slider
  shapeSlider.setId(5);
//  shapeSlider.setNumberOfTickMarks(shapeNames.length);

  controlP5.addSlider("minRad", // name, must match variable name
    10,100, // min and max values
    50, // the default value
    280,20, // X,Y position of slider
    80,13). // width and height of slider
    setId(6);

   // add "bang" button to randomize spheres
   controlP5.addBang("rndForm",
     20,60,13,13).
     setId(101);   

   // add "bang" button to randomize spheres
   controlP5.addBang("rndShaper",
     80,60,13,13).
     setId(102);   
     
   // add "bang" button to save STL
   controlP5.addBang("stlSave",
     20,100,13,13).
     setId(103);      
   
}

void controlEvent(ControlEvent theEvent) {
  boolean [] rebuildFlag={
    false,true,true,true,true,
    true,true,false,true,true};
    
  Controller c=theEvent.controller();
  int cid=c.id();
//  println("\n"+theEvent.isController()+" "+
//    c.id()+" "+c.label()+" "+c.value()+" type == "+theEvent.type());
  
  if(cid==0) num=(int)c.value();
  if(cid==1) {
    numSeg=(int)c.value();
    numSeg2=numSeg*2;
  }

  if(cid==4) totalH=(int)c.value();
  if(cid==5) { // shaper slider
    // don't call initShaper if it's the existing shaper style. this
    // prevents continuous calls to initShaper when the slider is
    // used rather than the rndShaper bang button.
    if((int)c.value()==oldShaper) return;
    shapeStyle=(int)c.value();
    initShaper();
  }
  if(cid==6) minRad=c.value();
  if(cid==8) maxRad=c.value();

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
