package unlekker.examples;

import processing.core.PApplet;

public class Main extends PApplet {

	public void setup() {
		
	}
	
	public void draw() {
		
	}
	
	public void keyPressed() {
	}
	
	public static void main(String[] args) {
		String sketch[] =new String [] {
				"unlekker.examples.TileSaving",
				"unlekker.examples.PShapeDrawing",
				"unlekker.examples.FileStructure",
				"unlekker.examples.GUITest"
				
				
				
		};
		
//		PApplet.main(new String[] {"unlekker.test.BezPatchTest"});
//		PApplet.main(new String[] {"unlekker.test.MainTest"});
//		PApplet.main(new String[] {"unlekker.test.UPShapeToolTest"});
//		PApplet.main(new String[] {"unlekker.test.UGeometryTransformerTest"});
		PApplet.main(new String[] {sketch[sketch.length-1]});
		
	}

}
