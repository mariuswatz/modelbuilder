/*

Marius Watz, March 2013
http://workshop.evolutionzone.com / http://github.com/mariuswatz

Complex3D.pde (standard Processing example, rewritten to output 
movies using UMovieMaker. PDF code has been removed and particle 
code in setup() and draw() has been moved to a separate tab. 

Press "S" to start movie export, and "S" or "Escape" to stop export.

*/


/*
 * PDF Complex
 * by Marius Watz (workshop.evolutionzone.com). 
 * 
*/


import processing.opengl.*;
 
void setup() {
  size(600, 600, OPENGL);
  frameRate(24);
  background(255);
  
  initParticle();
  textFont(createFont("Arial",12,false));
}
 
void draw() {
  background(0);
 
  drawParticle();
  
  // check to see if we should add movie frame
  if(mm!=null) {
    mm.addFrame();
    
    fill(255);
    text("Saving Quicktime - "+mm.frameCount+" frames saved.",10,20);
  }
}
 
 
// Get blend of two colors
public int colorBlended(float fract,
float r, float g, float b,
float r2, float g2, float b2, float a) {
 
  r2 = (r2 - r);
  g2 = (g2 - g);
  b2 = (b2 - b);
  return color(r + r2 * fract, g + g2 * fract, b + b2 * fract, a);
}
 
 
// Draw arc line
public void arcLine(float x,float y,float deg,float rad,float w) {
  int a=(int)(min (deg/SINCOS_PRECISION,SINCOS_LENGTH-1));
  int numlines=(int)(w/2);
 
  for (int j=0; j<numlines; j++) {
    beginShape();
    for (int i=0; i<a; i++) { 
      vertex(cosLUT[i]*rad+x,sinLUT[i]*rad+y);
    }
    endShape();
    rad += 2;
  }
}
 
// Draw arc line with bars
public void arcLineBars(float x,float y,float deg,float rad,float w) {
  int a = int((min (deg/SINCOS_PRECISION,SINCOS_LENGTH-1)));
  a /= 4;
 
  beginShape(QUADS);
  for (int i=0; i<a; i+=4) {
    vertex(cosLUT[i]*(rad)+x,sinLUT[i]*(rad)+y);
    vertex(cosLUT[i]*(rad+w)+x,sinLUT[i]*(rad+w)+y);
    vertex(cosLUT[i+2]*(rad+w)+x,sinLUT[i+2]*(rad+w)+y);
    vertex(cosLUT[i+2]*(rad)+x,sinLUT[i+2]*(rad)+y);
  }
  endShape();
}
 
// Draw solid arc
public void arc(float x,float y,float deg,float rad,float w) {
  int a = int(min (deg/SINCOS_PRECISION,SINCOS_LENGTH-1));
  beginShape(QUAD_STRIP);
  for (int i = 0; i < a; i++) {
    vertex(cosLUT[i]*(rad)+x,sinLUT[i]*(rad)+y);
    vertex(cosLUT[i]*(rad+w)+x,sinLUT[i]*(rad+w)+y);
  }
  endShape();
}
