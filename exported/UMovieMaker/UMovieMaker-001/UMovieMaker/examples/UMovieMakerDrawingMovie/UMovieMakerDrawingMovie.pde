/*

Marius Watz, March 2013
http://workshop.evolutionzone.com / http://github.com/mariuswatz

UMovieMaker is a replacement for Dan Shiffman's MovieMaker class,
which was discontinued for Processing 2.0 beta. It uses the Monte 
Media Library (http://www.randelshofer.ch/monte/) by Werner 
Randelshofer and can write Quicktime without being dependent on 
QTJava.zip. 

Monte Media Library is free for all uses (non-commercial, commercial 
and educational) under the terms of Creative Commons Attribution 3.0 
(CC BY 3.0).

UMovieMaker currently only supports Quicktime PNG output, although the
Monte library is capable of writing AVI and other formats as well.
The library syntax is intentionally similar to MovieMaker, although it does  
not re-create all the functions of that library.

The following is a recreation of the DrawingMovie.pde demo from MovieMaker, 
recreated in UMovieMaker. Original code by Dan Shiffman.

*/

import unlekker.moviemaker.*;

UMovieMaker mm;

void setup() {
  size(320, 240);

  // Save uncompressed, at 15 frames per second
  mm = new UMovieMaker(this, 
    sketchPath("drawing.mov"), width, height, 15);

  println("Creating movie file for drawing.");
  
  background(255);
}


void draw() {
  stroke(255,100,0);
  strokeWeight(4);

  // Draw if mouse is pressed
  if (mousePressed && pmouseX != 0 && mouseY != 0) {
    line(pmouseX, pmouseY, mouseX, mouseY);
  }

  // Add window's pixels to movie
  mm.addFrame();
}


void keyPressed() {
  if (key == ' ' || key==ESC) {
    // Finish the movie if space bar is pressed
    // MW - Let's catch ESC as well so that movie is closed if 
    // user presses Escape, otherwise the file will be damaged
    mm.finish();
    
    println("Closing movie file.");
    
    // Quit running the sketch once the file is written
    exit();
  }
}


