class CylPanel {
  float a,b,r1,r2,rD;
  float t,tD;
  UVec3 v1,v2;
  UVec3 v[];
  CylPanel next;
  
  CylPanel(float _a,float _b) {
    a=_a;
    b=_b;

    r1=(1/100f)*0.4+random(0.3)*(1/100f);
    if(random(100)>90) r1=(1/100f)*0.4+random(0.4,0.5)*(1/100f);    
    r2=r1*0.25f;

    if(r2>r1) {
      rD=r2;
      r2=r1;
      r1=rD;
    }
    rD=(r2-r1);
    
  }
    
  void init() {
    v1=new UVec3(1,0,0);
    v1.rotateY(b);
    v2=new UVec3(1,0,0);
    v2.rotateY(a);
    
    v=new UVec3[numSeg*2];
    
    for(int i=0; i<numSeg; i++) {
      float tt=(float)i/(float)(numSeg-1);
      
      v[i*2]=new UVec3(v1.x,(tt-0.5)*totalH,v1.z);
      v[i*2+1]=new UVec3(v2.x,(tt-0.5)*totalH,v2.z);
    }
  }
  
  void toModel(UGeometry g) {
    int rId=0;
    float rr=1;
    g.beginShape(QUAD_STRIP);
    
    for(int i=0; i<numSeg2; i+=2) {
      rr=(rD*shaper[rId++]+r1)*maxRad*100+minRad;
      g.vertex(v[i+1].x*rr, v[i+1].y, v[i+1].z*rr);
      g.vertex(v[i].x*rr, v[i].y, v[i].z*rr);
    }
    g.endShape();
    
    g.beginShape(QUAD_STRIP);
    rId=0;
    for(int i=0; i<numSeg2; i+=2) {
      rr=(rD*shaper[rId]+r1)*maxRad*100+minRad;
      g.vertex(v[i].x*rr,v[i].y,v[i].z*rr);
      rr=(next.rD*shaper[rId]+next.r1)*maxRad*100+minRad;
      g.vertex(
        next.v[i+1].x*rr,
        next.v[i+1].y,
        next.v[i+1].z*rr);
        
      rId++;
    }
    g.endShape();
  } 
}
