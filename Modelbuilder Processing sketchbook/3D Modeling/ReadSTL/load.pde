void loadModel() {
  String name=UIO.getFilenameChooserDialog(this,sketchPath);
  println(name);
  if(name!=null && name.endsWith("stl")) {
    model=UGeometry.readSTL(this, name);
    stepId=0;
  }
  
}


