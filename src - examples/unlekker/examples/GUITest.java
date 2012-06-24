package unlekker.examples;

import processing.core.PApplet;
import unlekker.util.UDataText;
import unlekker.util.USimpleGUI;

public class GUITest extends PApplet {
	USimpleGUI gui;
	boolean drawOutlines=true;
	
	public void setup() {
		size(600,300);
		gui=new USimpleGUI(this);
//		gui.layoutVertical=false;
		
		gui.addButton("switchLayout");
		gui.addButton("test02");
		gui.addButton("goOnCallSantaClaus");
		gui.newRow();
		
		gui.addRadioButton(
				"rb", new String[] {"abc","b","c"}, 50);
		gui.addDropDown("drop", new String[] {"abc","b","c"}, 50);
		gui.addTextField("Debug", 100, 20);
		gui.addToggle("toggle",true);
		gui.newRow();
		
		gui.addSlider("slider", 0, 0, 100);
		gui.addSlider("sliderlyHeaven", 0, 0, 100);
		gui.setLayout(false);
		gui.setText("Debug", gui.cpw+" "+gui.cph);
		
		loadText();
	}
	

void loadText() {
	UDataText textdata=UDataText.loadFile(
    sketchPath("c:/Users/marius/Dropbox/03 Code/20 Code/Processing Sketchbook/2012 Workshops/02 Advanced/Data/UDataSave01/data/LoremIpsum.txt"));
    
  String str;
  int status=0,id=0;
  
  do {
    // call UDataText.parseString() to parse next line. 
    // it returns a status code, status==-1 means we've 
    // read all the lines in the file.
    status=textdata.parseString();
    if(status!=UDataText.EOF) {
      println("====== "+(id++));
      println(status+" "+textdata.numToken+" "+textdata.parseStr);
      
      str=textdata.getString();
      String tok[]=split(str," ");
      println(tok);
    }
    
    println("EOF "+(status!=UDataText.EOF));
  } while(status!=UDataText.EOF);
}

	public void switchLayout() {
		gui.setLayout(!gui.layoutVertical);
	}

	public void draw() {
		background(0);
		
		gui.draw();
		if(drawOutlines) gui.drawGUIOutlines();
	}
	
	public void mousePressed() {
		drawOutlines=!drawOutlines;
	}

}
