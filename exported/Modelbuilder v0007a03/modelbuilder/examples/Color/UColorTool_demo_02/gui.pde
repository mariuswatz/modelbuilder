void initGUI() {
  gui=new USimpleGUI(this);
  gui.addButton("initColors");
  gui.addToggle("disablePalettes",disablePalettes);
  
  gui.addRadioButton("palettes", 
    new String[] {"reds", "blues", "mixed"}, 100);
  
  // hack to activate the "mixed" item of the "palettes" radio buttons
  ((RadioButton)gui.cp.getGroup("palettes")).activate(2);
}


