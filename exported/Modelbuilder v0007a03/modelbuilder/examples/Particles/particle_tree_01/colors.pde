UColorTool colors;

void initColors() {
  colors=new UColorTool();

  if (random(100)<50) {
    colors.addGradient(5, 10, "002255", "005599"); // 1
    colors.addGradient(5, 10, "007800", "00A700"); // 2
    colors.addGradient(5, 10, "FFFF00", "FFB400"); // 3
    colors.addGradient(5, 10, "76BB32", "BAFF00"); // 4
    colors.addGradient(5, 10, "004770", "00FFFF"); // 5
    colors.addGradient(3, 4, "FF0094", "BE006E"); // 6
  }
  else {
    colors.addGradient(5, 10, "F4F1F1", "DAF0F0"); // 0
    colors.addGradient(5, 10, "002255", "00ABFF"); // 1
    colors.addGradient(5, 10, "003300", "008668"); // 2
    colors.addGradient(5, 10, "FFFF00", "FFDD00"); // 3
    colors.addGradient(5, 10, "970068", "4B0031"); // 4
  }
  colors.generateColors(5, 40);
}

