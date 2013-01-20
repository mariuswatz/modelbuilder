package unlekker.util;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class UThumbnailer {
	PGraphics canvas;
	boolean fixedWidth=false,fullSize=false;
	int w,h,cnt,px,py,numColumns,numRows;
	private PApplet papplet;
	private float fixedW,fixedH;
	
	public UThumbnailer(PApplet papplet) {
		this.papplet=papplet;
	}
	
	public UThumbnailer size(int w,int h) {
		this.w=w;
		this.h=h;
		
		canvas=papplet.createGraphics(w,h, papplet.JAVA2D);
		canvas.beginDraw();
		canvas.smooth();
		canvas.imageMode(papplet.CENTER);
		
		return this;
	}
	
	public UThumbnailer setFullSize(int w,int h,int numColumns,int numRows) {
		fullSize=true;
		return size(w*numColumns,h*numRows);
	}
	
	public UThumbnailer setFixed(int fixedW,int fixedH,int numColumns,int numRows) {
		this.fixedW=fixedW;
		this.fixedH=fixedH;
		this.numColumns=numColumns;
		this.numRows=numRows;
		fixedWidth=true;
		size(fixedW*numColumns,fixedH*numRows);
		return this;
	}
	
	public UThumbnailer add(PImage image) {
		int px,py,ix,iy;
		float sz=-1;
		
		if(fixedWidth) {
//			if(px+fixedW>w) newRow();
			
//			ix
//			ix=160*((imgCnt-1)%numColumns);
//			int py=120*((imgCnt-1)/numColumns);
//			float sz=160/(float)currImage.width;
//			if(currImage.width<currImage.height) sz=120/(float)currImage.height;
//
//			thumbs.pushMatrix();
//			thumbs.translate(px+80,py+60);
//			thumbs.scale(sz);
//			thumbs.image(currImage,0,0);
//			thumbs.popMatrix();

		}
//		if(px+image.width>w) {
//			
//		}
		return this;
	}

	private void newRow() {
		// TODO Auto-generated method stub
		
	}

}
