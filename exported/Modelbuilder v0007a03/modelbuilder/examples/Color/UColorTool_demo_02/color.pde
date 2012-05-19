void initColors() {
  // get active palette 
  float [] palChoice=gui.cp.getGroup("palettes").arrayValue();
  for (int i=0;i<palChoice.length;i++) {
    println(i+" "+int(palChoice[i]));
  }
  
  colors=new UColorTool();
  
  // REDS
  if(palChoice[0]==1 || palChoice[2]==1) {
    colors.addGradient(3, 10, 
      colors.toColor(255, 0, 0), colors.toColor(255, 200, 0));
    colors.addGradient(3, 10, 
      colors.toColor(100, 50, 0), colors.toColor(200,50, 0));
  }
  
  // BLUES
  if(palChoice[1]==1 || palChoice[2]==1) {
    colors.addGradient(3, 10, colors.toColor(0, 100, 200), colors.toColor(0, 255, 255));
  }  
  
  // MIXED
  if(palChoice[2]==1) { 
    colors.add(0, 255, 255).add(0, 50, 100).add("FF0099");
    colors.addGradient(3, 10, colors.toColor(255, 255, 255), colors.toColor(255, 255, 0));
    colors.addGradient(3, 10, colors.toColor(100, 200, 0), colors.toColor(0, 50, 0));
  }

  if(disablePalettes) {
    // generate at least 5 colors, with a 30% chance of skipping a gradient
    colors.generateColors(5, 30);
  }
  else colors.generateColors();
  
  // store "colors" as a String for parsing
  String s=colors.toDataString();
  UUtil.log(s);

  // parse colors from String
  colors=UColorTool.parse(s);
  
}

