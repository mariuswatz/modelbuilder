void initColors() {
  // get active palette 
  float [] palChoice=gui.cp.getGroup("palettes").arrayValue();
  for (int i=0;i<palChoice.length;i++) {
    println(i+" "+int(palChoice[i]));
  }
  
  colors=new UColorTool();

  // The first two parameters of addGradient() specifies the
  // minimum and maximum number of colors that may be generated
  // for that specific gradient when generateColors() is called.
  // This allows for a randomized distribution of known good colors.
  
  // REDS
  if(palChoice[0]==1 || palChoice[2]==1) {
    colors.addGradient(3, 10, "FF0000","FFCC00");
    colors.addGradient(3, 10, "663300","CC3300");
  }
  
  // BLUES
  if(palChoice[1]==1 || palChoice[2]==1) {
    colors.addGradient(3, 10, 
      colors.toColor(0, 100, 200), 
      colors.toColor(0, 255, 255));
  }  
  
  // MIXED
  if(palChoice[2]==1) { 
    colors.add(0, 255, 255).add(0, 50, 100).add("FF0099");
    colors.addGradient(3, 10, 
      colors.toColor(255, 255, 255), 
      colors.toColor(255, 255, 0));
    colors.addGradient(3, 10, 
      colors.toColor(100, 200, 0), 
      colors.toColor(0, 50, 0));
  }

  // generateColors() has the option to specify a chance of skipping
  // any given gradient. This is useful to maximise the chance of arriving
  // at unplanned combinations between color gradients.
  
  if(disablePalettes) {
    // generate at least 5 colors, with a 30% chance of skipping a gradient
    colors.generateColors(5, 30);
  }
  else colors.generateColors();
}

