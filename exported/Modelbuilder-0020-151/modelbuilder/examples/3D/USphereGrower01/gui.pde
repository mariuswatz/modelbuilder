public void initGUI() {
  gui=new USimpleGUI(this);
  gui.cpw=200;
  gui.addSlider("minGrowthMod", minGrowthMod, 0.1f, 0.6f);
  gui.addSlider("maxGrowthMod", maxGrowthMod, 0.6f, 0.95f);
  gui.newRow();
  gui.addButton("place");
  gui.addButton("useBox");
  gui.addButton("useSphere");
  gui.newRow();
  gui.addButton("reinit");
  gui.addButton("saveSTL");
  gui.setLayout(false);
}

public void useSphere() {
  // if type is already SPHERE then return
  if (type==SPHERE) return;

  // if not, rebuild
  type=SPHERE;
  for (Sph theSphere : spheres) theSphere.build();
}

public void useBox() {
  // if type is already BOX then return
  if (type==BOX) return;

  // if not, rebuild
  type=BOX;
  for (Sph theSphere : spheres) theSphere.build();
}

public void saveSTL() {
  String nameFormat=this.getClass().getSimpleName();
  nameFormat=nameFormat+" ####.png";

  filename=UIO.getIncrementalFilename(nameFormat, sketchPath);
  saveFrame(filename);

  println("Saved '"+filename+"'");
  UGeometry model=new UGeometry();
  for (Sph theSphere : spheres) model.add(theSphere.model);

  model.writeSTL(this, UIO.noExt(filename)+".stl");
}

