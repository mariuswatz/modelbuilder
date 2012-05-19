USimpleGUI gui;

void initGUI() {
  gui=new USimpleGUI(this);
  gui.addButton("loadModel");
  gui.newRow();
  gui.addButton("stepNext");
  gui.addButton("stepPrev");
  gui.addButton("showAll");
  gui.setLayout(true);
}

void stepNext() {stepPoly(1);}
void stepPrev() {stepPoly(-1);}

void stepPoly(int incr) {
  if(model==null) return;
  doShowAll=false;
  stepId=(stepId+incr)%model.faceNum;
  if(stepId<0) stepId=model.faceNum-1;
}

void showAll() {
  doShowAll=true;
}
