package unlekker.util;

import processing.core.*;
import controlP5.*;

public class USimpleGUI {
	public PApplet p;
  public ControlP5 cp;
  public float cpx, cpy, cpw, cph;
  public boolean layoutVertical=true;

  public USimpleGUI(PApplet _p) {
    p=_p;
    cp=new ControlP5(p);
    cp.setAutoDraw(false);
    cp.setColorActive(p.color(255,100,0));
    cp.setColorBackground(p.color(50));
//    cp.setColorForeground(p.color(255));
    cp.setColorValue(p.color(255));
    cp.setColorLabel(p.color(255));
    
    cpw=150;
    cph=5;
    addPos(5, 5);
  }

  public void draw() {
    p.hint(p.DISABLE_DEPTH_TEST);
    p.noLights();
    p.fill(30, 200);
    p.noStroke();
    p.rect(0, 0, cpw, cph);
    cp.draw();
    p.hint(p.ENABLE_DEPTH_TEST);
  }

  public void addButton(String name) {
    Button tmpbut=cp.addButton(name, 1, (int)cpx, (int)cpy, name.length()*6, 15);
    if(layoutVertical) addPos(0, 17);
    else addPos(name.length()*6+10,0);
  }

  public void addToggle(String name,boolean value) {
    Toggle tmptoggle=cp.addToggle(name,value,(int)cpx, (int)cpy, 15, 15);
    if(layoutVertical) addPos(0, 17);
    else addPos(name.length()*7,0);
  }

  public void addSlider(
    String name, float val, float min, float max) {

    Controller sl=cp.addSlider(name, min, max, val, (int)cpx, (int)cpy, 100, 15);
    if(layoutVertical) addPos(0, 17);
    else addPos(105+name.length()*7,0);
  }
  
  public void addRadioButton(String group,String labels[],float w) {
  	RadioButton r=cp.addRadioButton(group,(int)cpx,(int)cpy);
    r.setItemsPerRow(1);
    for(int i=0; i<labels.length; i++) {
    	Toggle t=r.addItem(labels[i], 1);
      if(layoutVertical) addPos(0, 17);
      else addPos(w+5,0);
    }
  }
  
  public void randomizeValue(String name) {
    Controller c=cp.controller(name);
    c.setValue(UUtil.rnd.random(c.min(),c.max()));
  }


  public void setPos(float _x, float _y) {
    cpx=(int)_x;
    cpy=(int)_y;
  }

  public void addPos(float _x, float _y) {
    cpx+=(int)_x;
    cpy+=(int)_y;
    cpw=cpw>cpx+_x ? cpw : cpx+_x;
    cph+=_y;
  }
  
  public void newRow() {
  	addPos(-cpx+5,25);
  }
  
  public void saveToFile(String fname) {
  	UDataText txt=new UDataText();
  	
  	ControllerInterface[] cc=cp.getControllerList();
//  	Util.log("cp.getControllerList = "+cc.length);
  	for(int i=0; i<cc.length; i++) {
  		String type=cc[i].getClass().getSimpleName();
  		if(type.compareTo("Slider")==0) {
  			Slider sl=(Slider)cc[i];
//    		Util.log(i+"| "+sl.name()+" "+sl.value());
    		txt.add(sl.name()).add(type).
    			add(sl.value()).add(sl.min()).add(sl.max()).endLn();
  		}
  	}
  	txt.silent=true;
  	txt.save(fname);
  }
  
  public void readFromFile(String fname) {
  	UDataText txt=UDataText.loadFile(fname);
  	txt.silent=true;
  	
//  	Util.log("txt = "+txt.numStr);
  	txt.beginParse();
  	for(int i=0; i<txt.numStr; i++) {
  		txt.parseTokenString();
  		Controller cc=cp.controller(txt.getString());
  		txt.getString();
  		cc.setValue(txt.getFloat());
  		cc.setMin(txt.getFloat());
  		cc.setMax(txt.getFloat());
  	}
//  	txt.save(fname);
  }

}

