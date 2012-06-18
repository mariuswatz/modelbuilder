// execute a random attractor
void randomAttractor() {
  posIndex=(int)random(geo.vert.n);
  setAttractorPosition();
//  gui.randomizeValue("radius");
//  gui.randomizeValue("force");

  if (random(100)<20) attractor();
  else repulsor();
}


public void attractor() {
  UTransformAttractor def=new UTransformAttractor();

  def.addAttractor(attractorPos, radius, force);
  def.transform(geo.vert);
}

public void repulsor() {
  UTransformAttractor def=new UTransformAttractor();

  def.addRepulsor(attractorPos, radius, force);
  def.transform(geo.vert);
}

public void setAttractorPosition() {
  attractorPos=new UVec3(geo.vert.v[posIndex]);
  geo.calcBounds();
  attractorPos.add(
  new UVec3(geo.bb.c).
    sub(attractorPos).norm(radius/2));
}

public void resetForm() {
  if (loadedModel!=null) geo=new UGeometry(loadedModel);
  else {
    geo=new UGeometry();
    int nn=(int)random(5, 10);

    for (int i=0; i<nn; i++) {
      geo.add(UPrimitive.sphere(random(50, 100), u).
        translate(new UVec3(random(80, 140), 0, 0).
        rotateY(random(TWO_PI)).rotateX(random(TWO_PI))));
    }
  }

  geo.calcBounds();
  geo.setDimensions((float)height*0.8f);
  geo.translate(0, -geo.bb.min.y, 0);

  posIndex=(int)random(geo.vert.n);
  setAttractorPosition();
}

public void bend() {
  new UTransformDeform().bend(amount*radians(90)).transform(geo);
}

public void twist() {
  new UTransformDeform().twist(amount*radians(90)).transform(geo);
}

public void taper() {
  new UTransformDeform().taper(amount).transform(geo);
}

public void rotX() {
  geo.rotateX(amount*HALF_PI);
}

public void rotY() {
  geo.rotateY(amount*HALF_PI);
}

public void rotZ() {
  geo.rotateZ(amount*HALF_PI);
}

