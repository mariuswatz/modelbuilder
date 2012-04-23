void build() {
  UGeometry b;
  float x,y,z;

  model=new UGeometry();

  float n=20;
  float hD=(float)height/(n-1);
  
  for(float i=0; i<n; i++) {
    float r=100+(float)((int)i%2)*noise(i/10)*100;
    
    // create cylinder mesh
    b=UPrimitive.cylinder(r, hD*0.5, 36, true);
    
    // offset mesh to random position
    UVec3 pos=new UVec3(random(70),i*hD,0);
    pos.rotateY(random(TWO_PI));
    b.translate(pos);
    
    // add cylinder to mesh
    model.add(b);
  }
  
  // set normalized model dimensions
  model.setDimensions(300);

  // center model
  model.center();  
}

