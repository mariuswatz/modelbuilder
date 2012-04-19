import org.processing.wiki.triangulate.*;

ArrayList triangles = new ArrayList();
ArrayList points = new ArrayList();

void setup() {
  size(400, 400);
  smooth();
  reset();
}

void reset() {
  
  // clear the list
  points.clear();
  
  // fill the points arraylist with random points
  for (int i = 0; i < 100; i++) {
    // PVector.z is used to store an angle (particle's direction)
    points.add(new PVector(random(width), random(height), random(TWO_PI)));
  }
 
}

void mouseClicked() {
  reset();
}

void draw() {
  model();
  view();
}

void model() {
  
  for (int i = 0; i < points.size(); i++) {
    PVector p = (PVector)points.get(i);
    // p.z is used to store an angle value (particle's direction)
    p.x += 2.0*cos(p.z);
    p.y += 2.0*sin(p.z);
    if (p.x < 0 || p.x > width) { p.z += PI; }
    if (p.y < 0 || p.y > height) { p.z += PI; }
  }
  
  // get the triangulated mesh
  triangles = Triangulate.triangulate(points);
  
}
  
void view() {
  
  background(255);    
  noStroke();
  fill(70, 70, 250);
  
  // draw the points
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
