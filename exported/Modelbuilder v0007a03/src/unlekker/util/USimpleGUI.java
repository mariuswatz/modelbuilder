package unlekker.util;

import processing.core.*;
import unlekker.modelbuilder.UVec3;
import unlekker.modelbuilder.UVertexList;
import controlP5.*;
import java.util.*;

public class USimpleGUI {
	public PApplet p;
  public ControlP5 cp;
  public int bgCol, sliderW=100,charWidth=6;
  public float cpx, cpy, cpw, cph,lastW,lastH;
  public float padding=5;
  
  public boolean layoutVertical=true;
  public boolean enabled=true;
  private boolean layoutCalculated=false;
  
  ArrayList<GUINode> nodes;

  public USimpleGUI(PApplet _p) {
    p=_p;
    cp=new ControlP5(p);
    cp.setAutoDraw(false);
    cp.setMoveable(false);
    cp.setColorActive(p.color(255,100,0));
    cp.setColorBackground(p.color(50));
//    cp.setColorForeground(p.color(255));
    cp.setColorValue(p.color(255));
    cp.setColorLabel(p.color(255));
    
    bgCol=UColorTool.toColor("666666");
    
    nodes=new ArrayList<USimpleGUI.GUINode>();
  }

  public void draw() {
  	if(!enabled) return;
  	
  	if(!layoutCalculated) {
  		UUtil.log("USimpleGUI.setLayout() not called - setting vertical layout.");
  		setLayout(true);
  	}
  	
    p.hint(p.DISABLE_DEPTH_TEST);
    p.noLights();
    p.fill(bgCol, 200);
    p.noStroke();
    p.rect(0, 0, cpw, cph);
//    UUtil.log("cpw "+cpw+" "+cph);
    cp.draw();
    p.hint(p.ENABLE_DEPTH_TEST);
    
//    p.noFill();
//    p.stroke(255,0,0);
//    p.rectMode(p.CORNER);
//    for(GUINode node:nodes) if(!node.isNewRow){
//    	p.rect(node.posx,node.posy, node.width,node.height);
//    }
  }

  public void enable() {
  	cp.setUpdate(true);
  	enabled=true;
  }

  public void disable() {
  	cp.setUpdate(false);
  	enabled=false;
  }

  public USimpleGUI addButton(String name) {
    Button tmp=cp.addButton(name);// 1, 0,0, name.length()*charWidth, 15);
    tmp.setWidth(tmp.captionLabel().width()+3);
//    addNode(tmp, name,name.length()*charWidth,tmp.getHeight()-1);
    addNode(tmp, name,tmp.getWidth(),tmp.getHeight());
		return this;
  }

	public USimpleGUI addToggle(String name,boolean value) {
    Toggle tmp=cp.addToggle(name,value,(int)cpx, (int)cpy, 15, 15);
		int tw=tmp.captionLabel().width()-5;
    addNode(tmp, name,tw,tmp.getHeight()+tmp.captionLabel().getLineHeight());
    tmp.setValue(value);
    
		return this;
  }
	
	public USimpleGUI addTextField(String name,float w,float h) {
		Textfield txt;

		txt=cp.addTextfield(name,(int)cpx, (int)cpy,(int)w,(int)h);
		addNode(txt,name,txt.getWidth(),txt.getHeight()+txt.captionLabel().getLineHeight());
		return this;
	}
	
	public String getText(String name) {
		return cp.controller(name).stringValue();
	}

	public void setText(String name,String text) {
		Controller c=cp.controller(name);
		if(c.getClass().getSimpleName().indexOf("Textfield")==-1) {
			UUtil.logErr("setText() attempted on non-Textfield object.");
			return;
		}
			
		((Textfield)c).setValue(text);
	}

	// allows you to the set the caption label on a GUI element
	public void setCaption(String name,String caption) {
		Controller c=cp.controller(name);
		c.setCaptionLabel(caption);
	}

  public USimpleGUI addSlider(
    String name, float val, float min, float max) {

    Controller tmp=cp.addSlider(name, min, max, val, 0,0, sliderW, 15);
//    addNode(tmp, name,tmp.getWidth()+ name.length()*charWidth-9,tmp.getHeight()-1);
    addNode(tmp, name,tmp.getWidth()+tmp.captionLabel().width(),tmp.getHeight());
    
		return this;
  }

  public USimpleGUI addSlider(
      String name, int val, int min, int max) {

      Controller tmp=cp.addSlider(name, min, max, val, 0,0, 100, 15);
//      addNode(tmp, name,tmp.getWidth()+name.length()*charWidth-9,tmp.getHeight()-1);
      addNode(tmp, name,tmp.getWidth()+tmp.captionLabel().width(),tmp.getHeight());
      
  		return this;
    }

  /**
   * Adds a group of radio buttons <code>name</code>, built using the strings from the <code>labels</code> array. 
   * @param name Name of RadioButton group 
   * @param labels Array of button labels, each one of which will become a radio button
   * @param w Width of RadioButton group 
   * @return Reference to this <code>USimpleGUI</code> instance
   */
  public USimpleGUI addRadioButton(String name,String labels[],float w) {
  	RadioButton r=cp.addRadioButton(name,(int)cpx,(int)cpy);
  	
    r.setItemsPerRow(1);
    int txtlen=0;
    for(int i=0; i<labels.length; i++) {
    	Toggle t=r.addItem(labels[i], 1);
    	txtlen=PApplet.max(txtlen,labels[i].length());
    }
    
    addNode(r,name,r.getWidth()+charWidth*txtlen,(r.getHeight())*(labels.length+1));
    r.activate(0);
    
		return this;
  }
  
  public USimpleGUI addDropDown(String name,String labels[],int w) {
  	DropdownList dl=cp.addDropdownList(name, 0,0,w, 30);
  	for(int i=0; i<labels.length; i++)
  		dl.addItem(labels[i], 1);

  	addNode(dl,name,dl.getWidth(),dl.getHeight()-2);//(dl.getHeight()+2)*(labels.length+1));
  	return this;
  }

  public void setVerticalLayout() {
  	setLayout(true);
  }

  public void setHorizontalLayout() {
  	setLayout(false);
  }
  
  /**
   * Calculates actual positions of components based on
   * whether a vertical or horizontal layout is requested. 
   * <code>setLayout()</code> should always be called to complete
   * the GUI setup. 
   * 
   * Calls to <code>newRow()</code> will be interpreted as
   * blank spaces in a vertical layout, in a horizontal one it
   * will cause following components to appear on a new row.
   * 
   * @param vertical
   */
  public void setLayout(boolean vertical) {
		GUINode last=null,row[]=new GUINode[100];
		float rowh=0;
		
		layoutCalculated=true;
  	layoutVertical=vertical;

  	cpx=0; 
		cpy=0;		
		cpw=0;
		cph=0;
		UVec3 cpos=new UVec3();

		int id=0;
		if(layoutVertical) {  		
			UVertexList pos=new UVertexList();

  		for(GUINode n : nodes) {
  			pos.add(0,cpy);
  			cpw=PApplet.max(cpw,n.width);
  			cpy+=n.height;
  			id++;
  		}

  		id=0;
  		cph=cpy;
  		for(GUINode n : nodes) n.setPos(pos.v[id].x,pos.v[id++].y);
  	}
		else {
  		for(GUINode n : nodes) {
  			 if(n.isNewRow) {
    			cpw=PApplet.max(cpw,cpx+last.width);
  				cpx=0;
  				cpy+=rowh;
  				rowh=0;
  			}
  			else {
    			if(last!=null) cpx+=last.width;
      		n.setPos(cpx,cpy);
    			cpw=PApplet.max(cpw,cpx+n.width);
      		rowh=PApplet.max(rowh,n.height);
  				if(id==nodes.size()-1) cph=cpy+rowh;
  			}
  			
  			
//  			UUtil.log((int)cpx+","+(int)cpy+" "+
//  					(int)n.width+","+(int)n.height+
//  					" rh="+(int)rowh+" "+n.name+" "+n.className);
  			last=n;
  			id++;
  		}
  		
//  		for(GUINode n : nodes) if(!n.isNewRow && !n.isGroup)
//  			UUtil.log(n.className+" "+
//  				((Controller)n.o).position().x+","+
//  				((Controller)n.o).position().y);
		}
  }
  
  public void randomizeValue(String name) {
    Controller c=cp.controller(name);
    c.setValue(UUtil.rnd.random(c.min(),c.max()));
  }

  private void addNode(Object o,String name, int width, int height) {
		nodes.add(new GUINode(o,name,width,height));
		GUINode n=nodes.get(nodes.size()-1);
		//UUtil.log(nodes.size()+" "+name+" "+(int)n.width+"x"+(int)n.height);		
	}
  
  

	private void setLast(float lw, float lh) {
		lastW=lw;
		lastH=lh;
    cpw=cpw>cpx+lw ? cpw : cpx+lw;
    cph=cph>cpy+lh ? cph : cpy+lh;
    //UUtil.log("cpw "+cpw+" "+cph);
	}

  public USimpleGUI newRow() {
  	addNode(null, null, 0, 0);
  	return this;
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
  
  class GUINode {
  	public boolean isGroup;
		String name,className;
  	float width,height,posx,posy,paddingTop;
  	boolean isNewRow;
  	Object o;
  	Controller control=null;
  	ControllerGroup group=null;
  	
  	public GUINode(Object _o,String _name, int _width, int _height) {
  		o=_o;
  		if(o!=null) {
  			className=o.getClass().getName();
  			//UUtil.log(className);
  			
  			try {
  				control=(Controller)o;
  			} catch(Exception e) {
  				UUtil.logErr(e.getMessage());
  			}

  			try {
  				group=(ControllerGroup)o;
  				if(className.contains("Drop")) paddingTop=group.getHeight();
  				isGroup=true;
  			} catch(Exception e) {
  				UUtil.logErr(e.getMessage());
  			}
  		}
  		else {
  			isNewRow=true;
  			height=padding*2;
  			return;
  		}


  		name=_name;
  		width=_width;
  		height=_height;
			width+=padding*2;
			height+=padding*2;
		}

		public void setPos(float cpx, float cpy) {
			if(isNewRow) return;
			
			posx=cpx;
			posy=cpy;
			cpx+=padding;
			cpy+=padding+paddingTop;
				
			//UUtil.logDivider(className);
			if(control!=null) {
				CVector3f pos=control.position();
				//UUtil.log("Pos before: "+pos.x+" "+pos.y+" "+width+" "+height);
				control.setPosition(cpx, cpy);
				pos=control.position();
				//UUtil.log("Pos after: "+pos.x+" "+pos.y+" "+width+" "+height);
				return;
			}
			else if(group!=null) {
				CVector3f pos=group.position();
				//UUtil.log("Pos before: "+pos.x+" "+pos.y+" "+width+" "+height);
				group.setPosition(cpx, cpy);
				pos=group.position();
				//UUtil.log("Pos after: "+pos.x+" "+pos.y+" "+width+" "+height);
				return;
			}
//			
//			if(className.indexOf("Slider")!=-1) ((controlP5.Slider)o).setPosition(posx,posy);
//			else if(className.indexOf("Toggle")!=-1) ((controlP5.Toggle)o).setPosition(posx,posy);			
//			else if(className.indexOf("RadioButton")!=-1) ((controlP5.RadioButton)o).setPosition(posx,posy);
//			else if(className.indexOf("DropdownList")!=-1) ((controlP5.DropdownList)o).setPosition(posx,posy);
//			else if(className.indexOf("Button")!=-1) ((controlP5.Button)o).setPosition(posx,posy);
//			UUtil.log("Pos before: "+pos.x+" "+pos.y);
			
//			UUtil.log(className+" "+
//  				((Controller)o).position().x+","+
//  				((Controller)o).position().y);

		}  	
  }
  
  public boolean isMouseOver() {
  	if(cp==null) return false;
  	return cp.window(p).isMouseOver();
  }

}

