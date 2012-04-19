void generate() {
  vl=new UVertexList[50];
  vl[0]=new UVertexList();
  for(float i=0; i<12; i++) vl[0].add(0,cos(radians(i*30))*100,sin(radians(i*30))*100);
  vl[0].add(vl[0].v[0]);
  
  for(int i=1; i<vl.length; i++) {
    vl[i]=new UVertexList(vl[0]);
    vl[i].scale(noise((float)i*0.05)*3);
    vl[i].translate(i*10,0,0);
  }
  
  model=new UGeometry();
  for(int i=0; i<vl.length-1; i++) model.quadStrip(vl[i],vl[i+1]);
  model.triangleFan(vl[0],false);
  model.triangleFan(vl[vl.length-1],true);
  model.center();
  model.writeSTL(this,"Test.stl");
}
