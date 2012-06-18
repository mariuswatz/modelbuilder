void drawObjects() {
    translate(0,0,zoomZ);
  rotateX(rotX);
  rotateY(rotY);
      
  for(int i=0; i<obj.length; i++) {
    // calculate a fraction to determine color
    float colFract=map(i,0,obj.length-1,0,1);
          
    if(colFract<0.5f)
      fill(
          UColorTool.interpolate(colFract*2,
          "FFFF00", "00FFFF"));
    else fill(
        UColorTool.interpolate((colFract-0.5f)*2,
        "FF0000", "FF6600"));

    obj[i].draw(this);
  }
}

void build() {
  obj=new UGeometry[(int)random(10,100)];
  
  for(int i=0; i<obj.length; i++) {
    float h=random(100,250);  
    if(random(100)>85) h*=2;
    
    obj[i]=
        UPrimitive.cylinderGrid(
          random(20,50), h, meshRes, meshRes*2, true);
    
    // center
    obj[i].calcBounds();
    obj[i].translate(0,h*0.5f,0);
    
    // apply bend transform
    new UTransformDeform().
      taper(-0.75f).
      bend(radians(random(30,60))).
      twist(radians(random(-1,1)*80)).
      transform(obj[i]);
    
    obj[i].
      rotateZ(random(TWO_PI)).
      rotateX(random(TWO_PI));
  }
}



