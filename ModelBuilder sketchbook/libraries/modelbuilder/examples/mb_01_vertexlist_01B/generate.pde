void generate() {
  vl=new UVertexList();
  vl2=new UVertexList();

  float sinR=random(0.5,1);  
  float sinA=random(1,2);
  
  for(float i=0; i<1.01; i+=0.025f) {
    // add point on a sine curve
    vl.add(i,sin(sinA*TWO_PI*i)*sinR,0);
    
    // add point at random height
    vl2.add(i,random(-0.5,0.5));
  }
  
  // add two points to "ground" the random spiky shape
  vl2.add(vl2.v[vl2.n-1].x,0.5,0);
  vl2.add(vl2.v[0].x,0.5,0);
  
  // scale and translate vertex list so that it has
  // "height" height and is centered in the middle of the canvas
  //
  // translate() and scale() return a reference back to the original
  // UVertexList instance and can therefore be chained jQuery-style:
  vl.scale(width-40,height/2-20,1).translate(20,height*0.5,0);
  vl2.scale(width-40,height/4-20,1).translate(20,height*0.5,0);
}

