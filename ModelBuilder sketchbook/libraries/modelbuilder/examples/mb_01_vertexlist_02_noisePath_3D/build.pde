// calculate a QUAD_STRIP using noise()
void build() {
  // clear any stored vertex data. this is faster than creating
  // a new UVertexList instance.
  vl.reset();
  
  // add pairs of vertices to make up a QUAD_STRIP
  for(float i=0; i<1.01; i+=0.02f) {
    
    vl.add((i-0.5)*(float)width,
      noise(i*1+t)*(float)(height/2-h)-h*0.5,
      (noise(i*1+t+1000)-0.5)*(float)(height));
      
    vl.add((i-0.5)*(float)width,
      noise(i*1+t)*(float)(height/2-h)+h*0.5,
      (noise(i*1+t+1010)-0.5)*(float)(height));
  } 
  t=t+0.01f;
}
