import org.processing.wiki.triangulate.*;

ArrayList triangles = new ArrayList();
ArrayList points = new ArrayList();

void setup() {
  size(400, 400);
  smooth();
  noLoop();
 
  // fill the points Vector with points from a spiral
  float r = 1.0;
  float rv = 1.01;
  float a = 0.0;
  float av = 0.3;
 
  while(r < min(width,height)/2.0) {
    points.add(new PVector(width/2 + r*cos(a), height/2 + r*sin(a), 0));
    a += av;
    r *= rv;
  }
 
  // get the triangulated mesh
  triangles = Triangulate.triangulate(points);
}
 
void draw() {
  
  background(200);
 
  // draw points as red dots     
  noStroke();
  fill(255, 0, 0);
  
  for (int i = 0; i < points.size(); i++) {
    PVector p = (PVector)points.get(i);
    ellipse(p.x, p.y, 2.5, 2.5);
  }
 
  // draw the mesh of triangles
  stroke(0, 40);
  fill(255, 40);
  beginShape(TRIANGLES);
 
  for (int i = 0; i < triangles.size(); i++) {
    Triangle t = (Triangle)triangles.get(i);
    vertex(t.p1.x, t.p1.y);
    vertex(t.p2.x, t.p2.y);
    vertex(t.p3.x, t.p3.y);
  }
  endShape();
}
