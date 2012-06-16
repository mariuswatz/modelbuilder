package unlekker.test;

import processing.core.PApplet;
import unlekker.util.USimpleGUI;

public class GUITest extends PApplet {
	USimpleGUI gui;
	
	public void setup() {
		size(600,300);
		gui=new USimpleGUI(this);
//		gui.layoutVertical=false;
		
		gui.addButton("switchLayout");
		gui.addButton("test02");
		gui.addButton("test03");
		gui.newRow();
		gui.addRadioButton(
				"rb", new String[] {"abc","b","c"}, 50);
		gui.addToggle("toggle",true);
		gui.addSlider("slider", 0, 0, 100);
		gui.addSlider("slider2", 0, 0, 100);
		gui.setLayout(true);
		
	}
	
	public void switchLayout() {
		gui.setLayout(!gui.layoutVertical);
	}

	public void draw() {
		background(0);
		
		gui.draw();
	}

}
