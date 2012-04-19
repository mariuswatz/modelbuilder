void generate() {
  vl=new UVertexList();
  
  for(float i=0; i<1.01; i+=0.025f) {
    // add point on a sine curve
    vl.add(i,sin(TWO_PI*i),0);
  }
  
  // scale and translate vertex list so that it has
  // "height" height and is centered in the middle of the canvas
  //
  // translate() and scale() return a reference back to the original
  // UVertexList instance and can therefore be chained jQuery-style:
  vl.scale(width-40,height/2-20,1).translate(20,height*0.5,0);
}

