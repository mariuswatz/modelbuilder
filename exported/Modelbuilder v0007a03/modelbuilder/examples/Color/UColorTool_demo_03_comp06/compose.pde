void compose() {
  initColors();
  bgCol=colors.getRandomColor();
  
  // radius of element
  elRad=random(20,100);
  
  // number of elements in the X and Y directions
  nx=(int)((float)width/elRad);
  ny=(int)((float)height/elRad);
  
  // units to step in X and Y directions depending on nx and ny
  // can be different from elRad if nx and ny are not divisible
  // by elRad;
  xd=(float)width/(float)nx;
  yd=(float)height/(float)ny;
  
  // total number of elements is nx*ny
  el=new Element[nx*ny];
    
  // initialize elements
  for (int i=0; i<el.length; i++) el[i]=new Element(i);
}


