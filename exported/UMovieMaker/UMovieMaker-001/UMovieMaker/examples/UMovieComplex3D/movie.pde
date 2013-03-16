import unlekker.moviemaker.*;

UMovieMaker mm;

void keyPressed() {
  // if mm==null, start movie if key pressed is 'S' 
  if(mm==null) {
    if (key=='s' || key=='S') {
      // Save uncompressed, at 25 frames per second
      mm = new UMovieMaker(this, 
      sketchPath("Complex3D.mov"), width, height, 25);

      println("Creating movie file for drawing.");
    }
  }
  else {
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
}

