void initShape() {
  float D;
  
  num=20;
  numSeg=20;
  numSeg2=numSeg*2;
  segH=200/(float)(numSeg-1);
  
  panel=new CylPanel[num];
  D=TWO_PI/(float)num;
  for(int i=0; i<num; i++) 
    panel[i]=new CylPanel(
      D*(float)i,
      D*(float)(i+1));

  for(int i=0; i<num; i++) panel[i].next=panel[(i+1)%num];
  
  buildModel();
}

void buildModel() {
  model=new UGeometry();
  
  // build all side edges
  for(int i=0; i<num; i++) panel[i].toModel(model);
  
  // top cap
  model.beginShape(TRIANGLE_FAN);
  model.vertex(0,panel[0].v[numSeg2-1].y,0);
  for(int i=0; i<num; i++) {
    model.vertex(panel[i].v[numSeg2-1]);
    model.vertex(panel[i].v[numSeg2-2]);
  }
  model.vertex(panel[0].v[numSeg2-1]);
  model.endShape();

  // bottom cap
  model.beginShape(TRIANGLE_FAN);
  model.vertex(0,panel[0].v[0].y,0);
  for(int i=num-1; i>-1; i--) {
    model.vertex(panel[i].v[0]);
    model.vertex(panel[i].v[1]);
  }
  model.vertex(panel[0].v[0]);
  model.endShape();
  
  // center model
  model.center();
}

