package unlekker.util;

import java.util.ArrayList;

import processing.core.*;

public class UImageArray {
	ArrayList<PImage> img;
	public int gridx,gridy,maxDimension;
	
	public UImageArray() {
		img=new ArrayList<PImage>();
		
	}
	
	public void add(PImage g) {
		if(maxDimension>0) g=resizeToDimensions(g);
		img.add(g);
	}
	
	public int[] getMaxDimensions(PImage g) {
		int dim[];
		float asp=(float)g.height/(float)g.width;
		if(isWider(g)) {
			dim=new int[] {maxDimension,(int)((float)maxDimension*asp)};
		}
		else {
			dim=new int[] {(int)((float)maxDimension*(1f/asp)),maxDimension};
		}
		
		return dim;
	}
	
	private PImage resizeToDimensions(PImage g) {
		PImage tmp=g.get();
		int dim[]=getMaxDimensions(g);
		tmp.resize(dim[0], dim[1]);
				
		return tmp;
	}
	
	public static boolean isWider(PImage g) {
		return (g.width>g.height) ? true: false;
	}
}
