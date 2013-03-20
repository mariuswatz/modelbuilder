class Particle {
  float x, y, rad;
  int col;
  UTimer time;

  Particle() {
    x=random(-0.5f, 0.5f)*(float)width;
    y=random(-0.5f, 0.5f)*(float)height;
    rad=random(10, 40);
    if (random(100)>90) rad=random(80, 120);

    // tStart = [0 .. 0.5]
    float tStart=random(50)/100;
    float tDur=random(0.2f, 1-tStart);
    time=new UTimer(tStart, tDur);

    col=colors.getRandomColor();
  }

  void draw() {
    // update UTimer with global time == [0..1]
    time.update(globalT);

    if (time.t<0 || time.t>1) return; // not born or dead -> don't draw

    // calc radius and color based on time.t (local time)
    float theRad=sin(PI*time.t)*rad;
    
    // set transparent fill based on sin(T) so that object fades in and out
    fill(col, sin(PI*time.t)*200);

    pushMatrix();
    translate(x, y, 0);
    rotateY(PI*time.t);
    box(theRad, theRad, theRad*5);
    popMatrix();
  }
}

