void buildModel() {
  float x,y,z;
  
  model=new UGeometry();
  
  model.beginShape(QUAD_STRIP);
  for(float i=0; i<41; i++) {
    x=sin(TWO_PI*(i/40.0))*100;
    y=-150+300*(i/40.0);
    z=cos(TWO_PI*2*(i/40.0))*100;
    
    model.vertex(x-50,y,z);
    model.vertex(x+50,y,z);
  }
  model.endShape();
}

