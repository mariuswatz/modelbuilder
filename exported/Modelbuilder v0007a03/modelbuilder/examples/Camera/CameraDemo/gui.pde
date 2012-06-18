USimpleGUI gui;

public void initGUI() {
  gui=new USimpleGUI(this);
  gui.addButton("setCam1");
  gui.addButton("setCam2");
  gui.addButton("setCam3");
  gui.newRow();
  gui.addButton("setView1");
  gui.addButton("setView2");
  gui.addButton("setView3");
  gui.newRow();
  gui.addButton("run");
  gui.setLayout(false);
}


public void setCam1() {
  cam.setCam(0);
}

public void setCam2() {
  cam.setCam(1);
}

public void setCam3() {
  cam.setCam(2);
}

public void setView1() {
  cam.setView(0);
}

public void setView2() {
  cam.setView(1);
}

public void setView3() {
  cam.setView(2);
}


public void toggleRun() {
  if (cam.isRunning)cam.stop(); 
  else cam.run();
}

