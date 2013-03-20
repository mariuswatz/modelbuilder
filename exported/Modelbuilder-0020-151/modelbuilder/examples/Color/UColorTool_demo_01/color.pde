void initColors() {
  colors=new UColorTool();
  
  colors.add(0, 255, 255).add(0, 50, 100).add("FF0099");

  // The first two parameters of addGradient() specifies the
  // minimum and maximum number of colors that may be generated
  // for that specific gradient when generateColors() is called.
  // This allows for a randomized distribution of known good colors.
  
  colors.addGradient(3, 10, 
    colors.toColor(255, 0, 0), colors.toColor(255, 200, 0));
  colors.addGradient(3, 10, 
    colors.toColor(100, 50, 0), colors.toColor(200,50, 0));
  
  colors.addGradient(3, 10, 
    colors.toColor(0, 100, 200), colors.toColor(0, 255, 255));
  

  colors.addGradient(3, 10, 
    colors.toColor(255, 255, 255), colors.toColor(255, 255, 0));
  colors.addGradient(3, 10, 
    colors.toColor(100, 200, 0), colors.toColor(0, 50, 0));

  colors.generateColors();
}

