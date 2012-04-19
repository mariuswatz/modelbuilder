// this class calculates a 3D terrain using the noise(x,y)
// function. see http://processing.org/reference/noise_.html
// for more information about noise().
//
// because the user might change grid resolution or the X and Y
// modifiers for the noise function, the Terrain.draw() function
// needs to be able to regenerate the mesh and calculate the
// Z heights every frame.

class Terrain {
  PApplet parent;
  
  Pt pt[][];
  int gridRes; // grid resolution
  int lastGridRes; // last known grid resolution
  UGeometry model;
  
  Terrain(PApplet _parent) {    
    parent=_parent;
    buildModel();  
  }
  
  
  void draw() {
    // check which drawing style to use
    if(toggleSolid) {
      fill(0,100,200);
      noStroke();
    }
    else {
      noFill();
      stroke(0);
    }
    model.draw(parent);

  }
  
  // draw mesh as horizontal lines
  void drawLines() {
    stroke(0);
  
    for(int i=0; i<gridRes; i++) {
      noFill();
      beginShape();
      for(int j=0; j<gridRes; j++) {
        vertex(pt[i][j].x,pt[i][j].y,pt[i][j].z);
      }
      endShape();
    }
  }

  // draw mesh surface as strips of quads
  void buildModel() {
    float bottomZ;
    float colFract;
    
    gridRes=slGridResolution;
//    pt=generateNoisePoints(gridRes);
    pt=generateImagePoints(gridRes);
    
    bottomZ=-Z*0.5;
    if(model==null) model=new UGeometry();
    else model.reset();
    
    noStroke();
    for(int i=0; i<gridRes-1; i++) {
      model.beginShape(QUAD_STRIP);
      for(int j=0; j<gridRes; j++) {
        setColorZ(pt[i+1][j].z);
        model.vertex(pt[i+1][j].x,pt[i+1][j].y,pt[i+1][j].z);
        
        setColorZ(pt[i][j].z);
        model.vertex(pt[i][j].x,pt[i][j].y,pt[i][j].z);
        
      }
      model.endShape();
    }
    
    // draw edges of the mesh
    
    fill(#e56000);
    stroke(255);
    
    // left edge
    model.beginShape(QUAD_STRIP);
    for(int i=0; i<gridRes; i++) {
      model.vertex(pt[0][i].x,pt[0][i].y,pt[0][i].z);
      model.vertex(pt[0][i].x,pt[0][i].y,bottomZ);
    }
    model.endShape();

    // right side
    model.beginShape(QUAD_STRIP);
    for(int i=0; i<gridRes; i++) {
      model.vertex(pt[gridRes-1][i].x,pt[gridRes-1][i].y,bottomZ);
      model.vertex(pt[gridRes-1][i].x,pt[gridRes-1][i].y,pt[gridRes-1][i].z);
    }
    model.endShape();
//
    // lower edge
    model.beginShape(QUAD_STRIP);
    for(int i=0; i<gridRes; i++) {
      model.vertex(pt[i][gridRes-1].x,pt[i][gridRes-1].y,pt[i][gridRes-1].z);
      model.vertex(pt[i][gridRes-1].x,pt[i][gridRes-1].y,bottomZ);
    }
    model.endShape();

    // top edge
    model.beginShape(QUAD_STRIP);
    for(int i=0; i<gridRes; i++) {
      model.vertex(pt[i][0].x,pt[i][0].y,bottomZ);
      model.vertex(pt[i][0].x,pt[i][0].y,pt[i][0].z);
    }
    model.endShape();
    
    // bottom plane
    model.beginShape(QUADS);
    model.vertex(pt[0][0].x,pt[0][0].y,bottomZ);
    model.vertex(pt[gridRes-1][0].x,pt[gridRes-1][0].y,bottomZ);
    model.vertex(pt[gridRes-1][gridRes-1].x,pt[gridRes-1][gridRes-1].y,bottomZ);
    model.vertex(pt[0][gridRes-1].x,pt[0][gridRes-1].y,bottomZ);
    model.endShape();    
  
    model.center();  
    
  }
  
  void setColorZ(float z) {
    // set color as a function of Z position
    float colFract=(z+Z*0.5)/Z;
    fill(25,
      50+75*colFract,
      80+175*colFract);
  } 
}
