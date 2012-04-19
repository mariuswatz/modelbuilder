class CylPanel {
  float a,b,r;
  UVec3 v1,v2;
  UVec3 v[];
  CylPanel next;
  
  CylPanel(float _a,float _b) {
    a=_a;
    b=_b;

    r=50+random(150);
    v1=new UVec3(r,0,0);
    v1.rotateY(b);
    v2=new UVec3(r,0,0);
    v2.rotateY(a);
    
    v=new UVec3[numSeg*2];
    for(int i=0; i<numSeg; i++) {
      v[i*2]=new UVec3(v1.x,(float)i*segH,v1.z);
      v[i*2+1]=new UVec3(v2.x,(float)i*segH,v2.z);
    }
  }
  
  void toModel(UGeometry g) {
    g.beginShape(QUAD_STRIP);
    for(int i=0; i<numSeg2; i+=2) {
      g.vertex(v[i+1]);
      g.vertex(v[i]);
    }
    g.endShape();
    
    g.beginShape(QUAD_STRIP);
    for(int i=0; i<numSeg2; i+=2) {
      g.vertex(v[i]);
      g.vertex(next.v[i+1]);
    }
    g.endShape();
  } 
}
