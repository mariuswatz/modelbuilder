public void place() {
  Sph newsph, checksph;
  boolean ok=true;
  int tries=0;

  for (int i=0; i<5; i++) {
    do {
      ok=true;
      tries=0;

      // trick to pick random position in ArrayList so that the first
      // positions are more likely to be picked than the last ones.
      int sphId=(int)(sq(sq(random(100)/100))*(float)spheres.size());

      newsph=spheres.get(sphId).placeOnSphere();
      for (int j=0; j<spheres.size(); j++) if (j!=sphId) {
        checksph=spheres.get(j);
        if (j!=sphId && newsph.pos.distanceTo(checksph.pos)<checksph.rad+newsph.rad) ok=false;
      }
      tries++;
    } 
    while (!ok && tries<100);

    if (ok) {
      // only build model if successful
      newsph.build();
      spheres.add(newsph);
    }
    else println("Unable to place after 100 tries. Current # of spheres: "+spheres.size());
  }
}

class Sph {
  UVec3 pos;
  float rad;
  Sph parent;
  UGeometry model;

  Sph(float x, float y, float z, float r) {
    pos=new UVec3(x, y, z);
    rad=r;
  }

  public void build() {
    if (type==SPHERE)
      model=UPrimitive.sphere(rad, (int)max(8, min(60, rad/10))).rotateX(HALF_PI);
    else {
      model=UPrimitive.box(rad*0.65f, rad*0.65f, rad*0.65f);
      model.add(UPrimitive.box(rad*0.2f, rad*0.2f, rad*0.4f).translate(0, 0, -rad*0.6f));
    }

    model.rotateY(HALF_PI);
    if (parent!=null) {
      // orient model to align with the vector between pos and parent.pos
      UVec3 heading=UVec3.getHeadingAngles(new UVec3(pos).sub(parent.pos));
      model.rotateZ(heading.x).rotateY(heading.y);
    }
    model.translate(pos);
  }

  Sph placeOnSphere() {
    Sph sph;

    float newrad=max(rad*random(minGrowthMod, maxGrowthMod), 20);
    float rotz, rotx;
    rotz=random(TWO_PI);
    rotx=random(TWO_PI);
    UVec3 sphpos=
      new UVec3(rad, 0, 0).rotateZ(rotz).rotateX(rotx).add(pos);

    sph=new Sph(sphpos.x, sphpos.y, sphpos.z, newrad);
    sph.parent=this;
    return sph;
  }
}

