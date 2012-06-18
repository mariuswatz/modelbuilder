void render() {
  doRender=true;
  
  // disable interactive controls
  gui.disable();
  nav.disable();
}


void renderStart() {
  // init tiler if tiler==null
  if(tiler==null) {
    String filename=
      UIO.getIncrementalFilename(
        this.getClass().getSimpleName()+" ####.png", 
        savePath("render"));
  
    tiler=new UTileSaver(this, tiles, filename);
  }
  
  tiler.pre();
}

void renderEnd() {
  tiler.post();
  if(!tiler.isTiling) {
    doRender=false;
    tiler=null;
    
    // enable interactive controls
    gui.enable();
    nav.enable();
  }
}

