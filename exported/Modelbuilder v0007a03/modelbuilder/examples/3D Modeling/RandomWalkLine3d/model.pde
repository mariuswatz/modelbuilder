public void randomWalk() {
  // if last.length()<10 then this is a new line. 		
  if (last.length()<10) //start in a random direction 
    dir=new UVec3(length, 0, 0).rotateY(random(TWO_PI)).rotateZ(random(TWO_PI));
  else 
    dir=new UVec3(last).norm(length).
    rotateY(radians(random(-maxRot, maxRot))).
    rotateZ(radians(random(-maxRot, maxRot)));

  dir.add(last);

  float rad=(10-cnt%10)*2+5;
  model.add(cyl2Points(last, dir, rad));

  cnt++;
  if (cnt%10==0) last=new UVec3();
  else last=dir;
}

public UGeometry cyl2Points(UVec3 p1, UVec3 p2, float rad) {
  UVec3 dir=new UVec3(p2).sub(p1);
  float l=dir.length();
  //		UGeometry ln=UPrimitive.cylinder(rad,l/2,18,true).rotateZ(HALF_PI).translate(l/2,0,0);
  UGeometry ln=UPrimitive.box(l/2, rad, rad).translate(l/2, 0, 0);

  UVec3 head=UVec3.getHeadingAngles(dir);
  // to orient along heading angles we need to rotate first around Z (head.x) 
  // and then Y (head.y)
  ln.rotateZ(head.x).rotateY(head.y).translate(p1);

  // add a chunky box that's also been aligned
  ln.add(UPrimitive.box(rad*0.25f, rad*1.25f, rad*1.25f).
    rotateZ(head.x).rotateY(head.y).translate(p2));

  return ln;
}

public void clear() {
  model=UPrimitive.box(40, 40, 40);
  last=new UVec3();
}


