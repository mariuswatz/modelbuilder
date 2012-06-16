public void resetForm() {
  if (loadedModel!=null) geo=new UGeometry(loadedModel);
  else geo=UPrimitive.cylinderGrid(60, 200, u, v, true);
  geo.calcBounds();
  geo.translate(0, -geo.bb.min.y, 0);
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

