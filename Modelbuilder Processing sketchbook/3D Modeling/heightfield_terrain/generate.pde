Pt[][] generateImagePoints(int res) {
  Pt[][] pt;
  float D;

  int imgw=img.width;
  int imgh=img.height;
  float xstep=(float)imgw/(float)res;
  float ystep=(float)imgh/(float)res;

  pt=new Pt[res][res];
  D=(float)width*0.8f;
  D=D/(float)(res-1);

  for(int i=0; i<res; i++) {
    for(int j=0; j<res; j++) {
      // generate new verex
      pt[i][j]=new Pt(
      (float)i*D,
      (float)j*D,
      0);
      
      int X=(int)((float)i*xstep);
      int Y=(int)((float)j*ystep);
      pt[i][j].z=
        (brightness(img.get(X,Y))/255.0)*Z;
    }
  }
  
  return pt;
}


Pt[][] generateNoisePoints(int res) {
  Pt[][] pt;

  float D,noiseStart,noiseX,noiseY;

  // set offset for noise function
  noiseStart=random(1000);    

  pt=new Pt[res][res];

  // D is the distance between each vertex, calculated
  // as 80% of width, divided by gridRes minus one
  D=(float)width*0.8f;
  D=D/(float)(res-1);

  for(int i=0; i<res; i++) {
    for(int j=0; j<res; j++) {
      // generate new verex
      pt[i][j]=new Pt(
      (float)i*D,
      (float)j*D,
      0);
    }
  }

  noiseX=noiseStart;
  for(int i=0; i<res; i++) {
    noiseY=0;

    for(int j=0; j<res; j++) {
      pt[i][j].z=noise(noiseX,noiseY)*Z-Z*0.5;          
      noiseY+=(noiseYD/(float)res)*0.1;
    }

    noiseX+=(noiseXD/(float)res)*0.1;
  }

  return pt;
}

