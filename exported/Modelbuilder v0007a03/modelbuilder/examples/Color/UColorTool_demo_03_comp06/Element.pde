class Element {
  int id;
  float x,y,rad;
  float rot,R;
  float strokeW;
  int col;
  
  boolean filled;
  int type;
  
  Element(int id) {    
    
    x=id%nx;
    y=id/nx;

    rot=map(id%nx, 0,nx-1, 0,TWO_PI);
    R=map(id/nx, 0 ,ny, (float)width*0.1, (float)width*0.5);
    x=width/2+cos(rot)*R;
    y=height/2+sin(rot)*R;
    
    // give x and y a random offset
    x=x+random(-0.4,0.4)*elRad;
    y=y+random(-0.4,0.4)*elRad;
    
    rad=elRad*random(0.2,0.8);
    
    col=colors.getRandomColor();

    // random stroke weight
    strokeW=random(1,5);
    
    // random type 
    type=(int)random(100)%2;
    
    // 80% chance element is filled
    if(random(100)>20) filled=true;
  }
  
  void draw() {
    if(filled) {
      fill(col);
      noStroke();
    }
    else {
      stroke(col);
      strokeWeight(strokeW);
      noFill();
    }
    
    // use push/popMatrix and transformations to draw
    pushMatrix();
    translate(x,y);
    rotate(rot);
    
    if(type==0) {
      ellipse(0,0,rad,rad);
    }
    else {
      rect(-rad*0.5,-rad*0.1, rad,rad*0.2);
      rect(-rad*0.1,-rad*0.5, rad*0.2,rad);
    }
    popMatrix();
  }
}
