void buildModel() {
  float x,y,z;
  
  // Create cylinder geometry
  // Parameters: w,h,detail, capped
  model=UPrimitive.cylinder(50,150,18, true);
  
  // Create sphere geometry
  // Parameters: rad,detail
  UGeometry sph=UPrimitive.sphere(50, 18);
  
  // translate and add sphere to cylinder.
  // note that Geometry.add() copies the faces rather than 
  // keep a reference to the original Geometry object
  sph.translate(0,150,0);
  model.add(sph);
  
  // a second translate and add() results in a second
  // sphere being added
  sph.translate(0,-300,0);
  model.add(sph);
  
  model.add(UPrimitive.box(150,25,25));
  
}

