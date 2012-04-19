// RANDOMIZE CYLINDER FORM
void rndForm() {
  println("rndForm()");
  float D;

  if(shapeStyle==-1) rndShaper();

  panel=new CylPanel[num];
  D=TWO_PI/(float)num;
  for(int i=0; i<num; i++) 
    panel[i]=new CylPanel(
      D*(float)i,
      D*(float)(i+1));

  for(int i=0; i<num; i++) panel[i].next=panel[(i+1)%num];
  
  buildModel();
}

// BUILD MODEL ACCORDING TO CYLINDER SHAPE AND SHAPER FUNCTION
void buildModel() {
  if(num!=panel.length) {
    println("Must generate new random form once number of ribs are changed.");
    return;
  }
  
  if(model!=null) model.reset();
  else model=new UGeometry();
  
  buildShapeProfile();
  for(int i=0; i<num; i++) panel[i].init();
  for(int i=0; i<num; i++) panel[i].toModel(model);
  
  // top cap
  float rr;
  
  model.beginShape(TRIANGLE_FAN);
  model.vertex(0,panel[0].v[numSeg2-1].y,0);
  for(int i=0; i<num; i++) {
    rr=(panel[i].rD*shaper[numSeg-1]+panel[i].r1)*maxRad*100+minRad;
    model.vertex(
      panel[i].v[numSeg2-1].x*rr,
      panel[i].v[numSeg2-1].y,
      panel[i].v[numSeg2-1].z*rr);
    model.vertex(
      panel[i].v[numSeg2-2].x*rr,
      panel[i].v[numSeg2-2].y,
      panel[i].v[numSeg2-2].z*rr);
  }
  model.endShape();

  // bottom cap
  model.beginShape(TRIANGLE_FAN);
  model.vertex(0,panel[0].v[0].y,0);
  for(int i=num-1; i>-1; i--) {
    rr=(panel[i].rD*shaper[0]+panel[i].r1)*maxRad*100+minRad;
    model.vertex(
      panel[i].v[0].x*rr,
      panel[i].v[0].y,
      panel[i].v[0].z*rr);
    model.vertex(
      panel[i].v[1].x*rr,
      panel[i].v[1].y,
      panel[i].v[1].z*rr);
  }
  model.endShape();
}

void vertex(UVec3 v) {
  vertex(v.x,v.y,v.z);
}
