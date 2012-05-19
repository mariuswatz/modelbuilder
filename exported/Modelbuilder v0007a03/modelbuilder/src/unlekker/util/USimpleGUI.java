package unlekker.util;

import processing.core.*;
import controlP5.*;
import java.util.*;

public class USimpleGUI {
	public PApplet p;
  public ControlP5 cp;
  public int bgCol;
  public float cpx, cpy, cpw, cph, lastW,lastH;
  
  public boolean layoutVertical=true;
  private boolean layoutCalculated=false;
  
  ArrayList<GUINode> nodes;

  public USimpleGUI(PApplet _p) {
    p=_p;
    cp=new ControlP5(p);
    cp.setAutoDraw(false);
    cp.setColorActive(p.color(255,100,0));
    cp.setColorBackground(p.color(50));
//    cp.setColorForeground(p.color(255));
    cp.setColorValue(p.color(255));
    cp.setColorLabel(p.color(255));
    
    bgCol=UColorTool.toColor("666666");
    
    nodes=new ArrayList<USimpleGUI.GUINode>();
    
    addPos(5, 5);
  }

  public void draw() {
  	if(!layoutCalculated) {
  		UUtil.log("USimpleGUI.setLayout() not called - setting vertical layout.");
  		setLayout(true);
  	}
  	
    p.hint(p.DISABLE_DEPTH_TEST);
    p.noLights();
    p.fill(bgCol, 200);
    p.noStroke();
    p.rect(0, 0, cpw, cph);
    cp.draw();
    p.hint(p.ENABLE_DEPTH_TEST);
  }

  public USimpleGUI addButton(String name) {
    Button tmp=cp.addButton(name, 1, (int)cpx, (int)cpy, name.length()*6, 15);
    addNode(tmp, name,tmp.getWidth(),tmp.getHeight()+2);
		return this;
  }

	public USimpleGUI addToggle(String name,boolean value) {
		int tw=name.length()*7;
		
    Toggle tmp=cp.addToggle(name,value,(int)cpx, (int)cpy, 15, 15);
    addNode(tmp, name,tw,tmp.getHeight()+17);
    tmp.setValue(value);
    
		return this;
  }

  public USimpleGUI addSlider(
    String name, float val, float min, float max) {

    Controller tmp=cp.addSlider(name, min, max, val, (int)cpx, (int)cpy, 100, 15);
    addNode(tmp, name,tmp.getWidth()+5+name.length()*7,tmp.getHeight());
    
		return this;
  }

  public USimpleGUI addSlider(
      String name, int val, int min, int max) {

      Controller tmp=cp.addSlider(name, min, max, val, (int)cpx, (int)cpy, 100, 15);
      addNode(tmp, name,tmp.getWidth()+5+name.length()*7,tmp.getHeight());
      
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
    
    addNode(r,name,r.getWidth()+7*txtlen+5,(r.getHeight()+2)*(labels.length+1));
    r.activate(0);
    
		return this;
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
		GUINode last=null;
		float rowh;
		
		layoutCalculated=true;
  	layoutVertical=vertical;
		cpx=5; 
		cpy=5;
		cpw=0;
		cph=0;
		

		if(layoutVertical) {
  		
  		for(GUINode n : nodes) {
  			if((last!=null && last.o!=null)) cpy+=last.height;
  			else cpy=n.height;
//  			cp.controller(arg0)
  			n.setPos(5,cpy);

  			cpw=PApplet.max(cpw,cpx+n.width);
  			if(n==nodes.get(nodes.size()-1)) cpy+=n.height+5;
  			else if(n.o==null) cpy+=10;
  			else cpy+=2;
  			
  			cph=PApplet.max(cpy,cph);
  			last=n;
  			UUtil.log(cpx+" "+cpy);
  		}
  	}
		else {
			cpy=5;
			rowh=0;
  		for(GUINode n : nodes) {
  			rowh=PApplet.max(rowh,n.height);
  			cpx+=5;
  			cpw=PApplet.max(cpw,cpx);

  			if(last!=null) {
  				cpx+=last.width;
    			n.setPos(cpx,cpy);
  			}
  			
  			if(n.o==null) {
    			cpw=PApplet.max(cpw,cpx+last.width);
  				cpx=5;
  				cpy+=rowh+5;
  				cph=cpy;
  				rowh=0;
  			}
  			if(n==nodes.get(nodes.size()-1)) {
  				if(last==null) cpw=n.width;
  				else {
  					cpw=PApplet.max(cpw,cpx+last.width);
  				}
  				cph=cpy+rowh+5;
  			}
  			
  			UUtil.log(cpx+" "+cpy+" "+rowh);
  			last=n;

  		}
		}
  }
  
  public void randomizeValue(String name) {
    Controller c=cp.controller(name);
    c.setValue(UUtil.rnd.random(c.min(),c.max()));
  }

  private void addNode(Object o,String name, int width, int height) {
		nodes.add(new GUINode(o,name,width,height));
		UUtil.log(nodes.size()+" "+name+" "+width+" "+height);		
	}
  
  

	private void setLast(float lw, float lh) {
		lastW=lw;
		lastH=lh;
    cpw=cpw>cpx+lw ? cpw : cpx+lw;
    cph=cph>cpy+lh ? cph : cpy+lh;
    UUtil.log("cpw "+cpw+" "+cph);
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
  	String name,className;
  	float width,height;
  	Object o;
  	
  	public GUINode(Object _o,String _name, int _width, int _height) {
  		o=_o;
  		if(o!=null) className=o.getClass().getName();
  		if(o!=null) UUtil.log(o.getClass().getName());
  		
  		name=_name;
  		width=_width;
  		height=_height;
		}

		public void setPos(float cpx, float cpy) {
			if(className==null) return;
			
			if(className.indexOf("Slider")!=-1) ((controlP5.Slider)o).setPosition(cpx,cpy);
			else if(className.indexOf("Toggle")!=-1) ((controlP5.Toggle)o).setPosition(cpx,cpy);			
			else if(className.indexOf("RadioButton")!=-1) ((controlP5.RadioButton)o).setPosition(cpx,cpy);
			else if(className.indexOf("Button")!=-1) ((controlP5.Button)o).setPosition(cpx,cpy);
			
		}  	
  }

}

