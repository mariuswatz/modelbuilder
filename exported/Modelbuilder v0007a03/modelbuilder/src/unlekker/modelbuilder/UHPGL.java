package unlekker.modelbuilder;

import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;

import processing.core.PApplet;

import unlekker.modelbuilder.*;
import unlekker.util.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

/**
 * Simple exporter and parser class for HPGL vector format as used by old plotters.
 * The only recognized commands are IN, DF, PU, PD, IW and SP, anything else is ignored. 
 * This class not inteded to be a general-purpose parser, and should only be expected to read its own output.
 * Your mileage might vary.  
 * @author Marius Watz
 *
 */
public class UHPGL {
	public String filename;
	/**
	 * Constant for Letter format width on a HP 7550. 
	 */
  public static int HP7550_LETTER_WIDTH=10170;
  /**
   * Constant for Letter format height on a HP 7550.
   */
  public static int HP7550_LETTER_HEIGHT=7840;
  
	File file;
	PrintWriter outWriter;
	Writer outStream;

	public static int IN=0,DF=1,PU=2,PD=3,IW=4,SP=5;
	public static String cmdStr[]={"IN","DF","PU","PD","IW","SP"};
	public int penColor[];
	ArrayList<HPGLCommand> cmd;
	public UBBox bb;
	
	public void initialize() {
		add(new HPGLCommand(IN));
		add(new HPGLCommand(DF));
	}

	/**
	 * Adds IW command to set clipping window.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void inputWindow(float x1,float y1,float x2,float y2) {
		add(new HPGLCommand(IW,new float[]{x1,y1,x2,y2}));
	}

	public void setPen(int id) {
		add(new HPGLCommand(SP, new float[]{id}));
	}

	public void penUp() {
		add(new HPGLCommand(PU));
	}

	public void penUp(float x,float y) {
		add(new HPGLCommand(PU, new float[]{x,y}));
	}

	public void penUp(UVec3 v) {
		penUp(v.x,v.y);
	}

	public void penDown(float x,float y) {
		add(new HPGLCommand(PD, new float[]{x,y}));
	}

	public void penDown(UVec3 v) {
		penDown(v.x,v.y);
	}

	public void rect(float x,float y,float w,float h) {
		vertexList(new UVertexList().
				add(x,y).add(x+w,y).add(x+w,y+h).add(x,y+h).close());
	}
	
	/**
	 * Add a list of points from a UVertexList object. Executes a PU to the first point in the list, followed by
	 * PD commands for all the remaining points. 
	 * @param vl
	 */
	public void vertexList(UVertexList vl) {
		UUtil.log("vertexList "+vl.n);
		penUp(vl.v[0]);
		for(int i=0; i<vl.n; i++) penDown(vl.v[i]);
		penUp();
	}
	
	/**
	 * Add HPGL command to command list.
	 * @param c
	 */
	public void add(HPGLCommand c) {
		if(cmd==null) cmd=new ArrayList<UHPGL.HPGLCommand>();
		cmd.add(c);
	}
	
	/**
	 * Adds HPGL command by parsing a String. If the command is not recognized it is simply ignored.
	 * @param s
	 */
	public void add(String s) {
		HPGLCommand c=new HPGLCommand(s);
		if(c.type!=-1) add(c);
	}
	
	public void removeLast() {
		cmd.remove(cmd.size()-1);
	}
	
	/**
	 * Translates all points by adding <tx,ty> to <x,y>
	 * @param tx
	 * @param ty
	 */
	public void translate(float tx,float ty) {
		for(HPGLCommand c:cmd) c.translate(tx, ty);
	}

	/**
	 * Scales all points by multiplying x by mx and y by my.
	 * @param mx
	 * @param my
	 */
	public void scale(float mx,float my) {
		for(HPGLCommand c:cmd) c.scale(mx, my);
	}

	/**
	 * Scales all points by multiplying by factor "m".
	 * @param m
	 */
	public void scale(float m) {
		for(HPGLCommand c:cmd) c.scale(m,m);
	}

	/**
	 * Rotates all points around <0,0> by the specified radians.
	 * @param deg
	 */
	public void rotate(float deg) {
		for(HPGLCommand c:cmd) c.rotate(deg);
	}

	public void calcBounds() {
		if(bb==null) bb=new UBBox();
		else bb.reset();
		
		for(HPGLCommand c:cmd) {
			if(c.params!=null && (c.type==PU || c.type==PD)) bb.add(new UVec3(c.params[0],c.params[1]));
		}		
		bb.finishCalc();
	}
	
	/**
	 * Centers drawing around <0,0>
	 */
	public void center() {
		if(bb==null) calcBounds();
		translate(-bb.c.x,-bb.c.y);
	}

	/**
	 * Centers drawing around <cx,cy>
	 * @param cx
	 * @param cy
	 */
	public void center(float cx,float cy) {
		if(bb==null) calcBounds();
		translate(-bb.c.x+cx,-bb.c.y+cy);
	}

	
	/////////////////////////////////
	// DRAW
	
	/**
	 * Draws the HPGL commands to a PApplet canvas
	 * @param p
	 */
	public void draw(PApplet p) {
		HPGLCommand last=null;
		if(penColor==null) penColor=new int[]{
				p.color(0),p.color(255,0,0),p.color(0,0,255),p.color(0,255,0),
				p.color(255,100,0),p.color(255,0,50),p.color(0,255,255),p.color(255,200,0)};
		
		float lx=0,ly=0;
		boolean shapeOn=false;
		
		p.noFill();
		for(HPGLCommand c:cmd) {
			if(c.type==PU) {
				if(last!=null && last.type==PD) {
					p.endShape(); 
					shapeOn=false;
					lx=last.params[0]; 
					ly=last.params[1];
				}
				if(c.params!=null) {
					lx=c.params[0]; 
					ly=c.params[1];
				}
			}
			if(c.type==PD) {
				if(!shapeOn) {
					p.beginShape();
					shapeOn=true;
					p.vertex(lx,ly);
				}
				
				lx=c.params[0]; 
				ly=c.params[1];
				p.vertex(lx,ly);
			}
			if(c.type==SP) {
				if(c.params[0]<1) p.noStroke();
				else p.stroke(penColor[(int)c.params[0]-1]);
			}
			last=c;
		}
	}
	
	/////////////////////////////////
	// FILE IO

	public static UHPGL load(String _filename) {
		UHPGL hpgl=new UHPGL();
		
		hpgl.filename=_filename;
		UDataText data=UDataText.loadFile(hpgl.filename);
		
		for(int i=0; i<data.numStr; i++) {
			String tok[]=PApplet.split(data.str[0], ";");
			for(int j=0; j<tok.length; j++) hpgl.add(tok[j]);
		}
		
		return hpgl;
	}
	
	public void writeHPGL(String _filename) {
		try {
			filename=_filename;
			file=new File(filename);
			outStream=new OutputStreamWriter(
					new FileOutputStream(file.getAbsolutePath(),false));		
			outWriter=new PrintWriter(outStream);
			
			HPGLCommand cmda[]=(HPGLCommand [])cmd.toArray(new HPGLCommand[1]);
			for(int i=0; i<cmda.length; i++) cmda[i].writeHPGL();
			
			outWriter.flush();
			outStream.close();		

		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void writePDF(String filename) {
		
	}

	class HPGLCommand {
		int type;
		float params[];
		
		HPGLCommand(int _type,float _params[]) {
			type=_type;
			params=_params;
		}

		HPGLCommand(int _type) {
			type=_type;
		}

		public HPGLCommand(String s) {
			type=-1;
			if(s.length()<2) return;
			
			String dat=s.substring(0,2);
			s=s.substring(2);
//			Util.log(dat+" | '"+s+"'");
			for(int i=0; i<cmdStr.length; i++) if(cmdStr[i].equals(dat)) type=i;
			if(s.length()>0 && (type==PU || type==PD || type==SP)) params=UUtil.parseFloat(PApplet.split(s,","));			
//			Util.log(type+" | '"+cmdStr[type]+"' "+UUtil.toString(params));
		}

		public void translate(float tx,float ty) {
			if((type==PU || type==PD) && params!=null) {
				params[0]+=tx;
				params[1]+=ty;
			}
		}

		public void scale(float mx,float my) {
			if((type==PU || type==PD) && params!=null) {
				params[0]*=mx;
				params[1]*=my;
			}
		}

		public void rotate(float deg) {
			if((type==PU || type==PD) && params!=null) {
				UVec3 v=new UVec3(params[0],params[1]).rotate(deg);
				params[0]=v.x;
				params[1]=v.y;
			}
		}

		void writeHPGL() {
			if(type==IN) outWriter.print("IN;");
			else if(type==DF) outWriter.print("DF;");
			else if(type==PU && params==null) outWriter.print("PU;");
			else if(type==PU) outWriter.print("PU" + (int)params[0] + "," + (int)params[1] + ";");
			else if(type==PD) outWriter.print("PD" + (int)params[0] + "," + (int)params[1] + ";");
			else if(type==SP) outWriter.print("SP"+(int)params[0]+";");
			else if(type==IW) outWriter.print("IW" + (int)params[0] + "," + (int)params[1] + "," + 
					(int)params[2] + "," + (int)params[3] + ";");
		}
	}
}