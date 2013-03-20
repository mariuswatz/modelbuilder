float surfCheck=1500;
boolean subdivConditional=false;
int randomCntSinceSubdivide;

public void subdivide() {
  int oldcnt=geo.faceNum;
  UGeometry tmp;
  if (subdivConditional)
    tmp=new USubdivider().//setType(UFilter.SUBDIVIDE_CENTROID).
    setMaxArea(surfCheck*0.25f).
      setMaxEdgeLength(sqrt(surfCheck*0.25f)).
      subdivideConditional(geo);
  else {
    println("uncoditional subdivide");
    tmp=new USubdivider().build(geo);
    if (tmp.faceNum>oldcnt) surfCheck*=1.2f;
  }

  //    surfCheck/=4;
  geo=tmp;
  UUtil.log("surfCheck="+surfCheck);
}

public void subdivideCheck() {
  randomCntSinceSubdivide=0;
  if (!subdivConditional && geo.faceNum>60000) return;
  float newCheck=0;
  try {
    boolean check=false;
    for (int i=0; i<geo.faceNum && !check; i++) {
      if (geo.face[i].surfaceArea()>surfCheck) check=true;
      newCheck=geo.face[i].surfaceArea();
    }
    if (!check) return;

    subdivide();
  } 
  catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
}

