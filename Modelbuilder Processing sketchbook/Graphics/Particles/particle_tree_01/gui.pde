USimpleGUI gui;

void initGUI() {
  gui=new USimpleGUI(this);
  gui.addButton("reinit");
  gui.addSlider("maxLevel",maxLevel, 2,10);
  gui.addSlider("startSpeed",startSpeed,1,10);
  gui.addSlider("branchAngle",branchAngle,5,90);
  gui.addSlider("maxBranches",maxBranches,1,10);
  gui.addSlider("rotMod",rotMod,0.2,5);
  gui.addSlider("speedMod",speedMod,0.2,1.5);
}
