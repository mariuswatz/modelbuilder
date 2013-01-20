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
  public int cpx, cpy, cpw, cph,lastW,lastH;
  public int padding=5;
  
  public boolean layoutVertical=true;
  public boolean enabled=true;
  private boolean layoutCalculated=false;
  
  ArrayList<GUINode> nodes;
	private int ID;

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
    ID=0;
  }

  public void draw() {
  	if(!enabled) return;
//  	p.rectMode(p.CORNER);
  	
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
  }
  
  public void drawGUIOutlines() {
	  p.noStroke();
	  p.rectMode(p.CORNER);
	  for(GUINode node:nodes) if(!node.isNewRow){
		  p.fill(255,0,0);
	  	p.rect(node.posx,node.posy, node.width,1);
	  	p.rect(node.posx,node.posy, 1,node.height);
	  	p.rect(node.posx,node.posy+node.height-1, node.width,1);
	  	p.rect(node.posx+node.width-1,node.posy, 1,node.height);
	  	
	  	p.textSize(9);
	  	String s=node.width+" "+node.height;
	  	float pw=p.textWidth(s)+4;
	  	float px=p.max(node.posx,node.posx+node.width-pw);
	  	p.rect(px,node.posy, pw,12);
	  	p.fill(0);
	  	p.text(s,
	  			px+2,node.posy+10);
	  }

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
    Toggle tmp=cp.addToggle(name,value,cpx, cpy, 15, 15);
		int tw=tmp.captionLabel().width()-5;
    addNode(tmp, name,tw,tmp.getHeight()+tmp.captionLabel().getLineHeight());
    tmp.setValue(value);
    
		return this;
  }
	
	public USimpleGUI addTextField(String name,int w,int h) {
		Textfield txt;

		txt=cp.addTextfield(name,cpx, cpy,w,h);
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

	public USimpleGUI addLabel(String name,String value,int w,int h) {
		Textlabel label=cp.addTextlabel(name, value, w, h);
		addNode(label,name,w,h);
		return this;
	}
	
	public USimpleGUI setLabel(String name,String value) {
		Textlabel label=(Textlabel)cp.controller(name);
		label.setValue(value);
		return this;
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

      Controller tmp=cp.addSlider(name, min, max, val, 0,0, sliderW, 15);
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
  	RadioButton r=cp.addRadioButton(name,cpx,cpy);
  	
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
		int rowh=0;
		
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
  			cpy+=n.height-padding;
  			if(n.equals(nodes.get(nodes.size()-1))) cpy+=padding;
  			id++;
  		}

  		id=0;
  		cph=cpy;
  		for(GUINode n : nodes) n.setPos((int)pos.v[id].x,(int)pos.v[id++].y);
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
  			
  			
//  			UUtil.log(cpx+","+cpy+" "+
//  					n.width+","+n.height+
//  					" rh="+rowh+" "+n.name+" "+n.className);
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
		if(n.isGroup) n.group.setId(ID++);
		else if(n.control!=null) n.control.setId(ID++);
		//UUtil.log(nodes.size()+" "+name+" "+n.width+"x"+n.height);		
	}
  
  

	private void setLast(int lw, int lh) {
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
  
  public static int TOGGLE=0,SLIDER=1,BUTTON=2,RADIOBUTTON=3,
  		TEXTFIELD=4,DROPDOWN=5,LABEL=6;
  		
  		public static String TOGGLESTR="Toggle",SLIDERSTR="Slider",
  		BUTTONSTR="Button",RADIOBUTTONSTR="RadioButton",TEXTFIELDSTR="Textfield",
  		LABELSTR="Textlabel",DROPDOWNSTR="DropdownList";
  		
  public static int getType(ControllerInterface cc) {
  	String name=cc.getClass().getSimpleName();
  	
  	if(name.equals(BUTTONSTR)) return BUTTON;
  	if(name.equals(TOGGLESTR)) return TOGGLE;
  	if(name.equals(SLIDERSTR)) return SLIDER;
  	if(name.equals(RADIOBUTTONSTR)) return RADIOBUTTON;
  	if(name.equals(TEXTFIELDSTR)) return TEXTFIELD;
  	if(name.equals(LABELSTR)) return LABEL;
  	if(name.equals(DROPDOWNSTR)) return DROPDOWN;
  	
  	return -1;
  }
  
  public String [] toDataString() {
//	  	ControllerInterface[] cc=cp.getControllerList();
//		UUtil.log("cp.getControllerList = "+cc.length);
	  	UDataText txt=new UDataText();
//	  	
//	  int currID=0,cnt=0;
//	  int taken[]=new int[cc.length];
//	  for(int i=cnt; i<cc.length; i++) taken[i]=-1;
//	  
//	  while(cnt<cc.length) {
//			for(int i=0; i<cc.length; i++) {
//				int id=cc[i].id();
//				
//				UUtil.log("currID "+currID+" "+id+" "+currID+" "+cnt);
//				if(taken[id]<0 && id<=currID) {
//					taken[id]=1;
//					currID++;
//					cnt++;
					
	  int i=0;
  	for(GUINode n: nodes) {
  		if(n.isNewRow) txt.add("NEWROW").endLn();
  		else {
  			int id=n.id;  			
				UUtil.log("Found: "+id+" "+n.name);
				if(n.type==SLIDER) {
					Slider sl=(Slider)n.control;
		  		UUtil.log(i+"| "+sl.name()+txt.DELIM+sl.value()+" "+sl.id()+" "+n.id);
		  		txt.add(sl.name()).add(sl.id()).add(n.type).
		  			add(sl.value()).add(sl.min()).add(sl.max()).endLn();
				}
				if(n.type==BUTTON) {
					Button b=(Button)n.control;
		  		txt.add(b.name()).add(b.id()).add(n.type).endLn();				
				}
				if(n.type==TOGGLE) {
					Toggle b=(Toggle)n.control;
		  		txt.add(b.name()).add(b.id()).add(n.type).add(b.getState()).endLn();				
				}
				
				i++;
			}

		}

		String [] res=txt.toArray();
		UUtil.logDivider("USimpleGUI");
		UUtil.log(res);
		UUtil.logDivider();
		
		return res;
		
  }
  public void loadFromString(String[] guistr,boolean recreate) {
  	UUtil.log("guistr.length "+guistr.length);
		UDataText txt=new UDataText(guistr);
		loadFromFile(txt,recreate);
  }
  
	public void loadFromFile(UDataText txt,boolean recreate) {
		int res=-1;
		String name;
		int type,id;
		
		do{
			res=txt.parseString();
			UUtil.log(txt.parseLine+" "+res+" '"+txt.parseStr+"'");
			if(res==txt.TOKENSTR) {
				if(txt.parseStr.equals("NEWROW")) {
					if(recreate) newRow();
				}
				else {
					name=txt.getString();
					id=txt.getInt();
					type=txt.getInt();
					UUtil.log(name+" "+id+" "+type);
					
					if(recreate) {
						if(type==BUTTON) addButton(name);
						if(type==TOGGLE) addToggle(name,txt.getBool());
						if(type==SLIDER) addSlider(name, txt.getFloat(), txt.getFloat(), txt.getFloat());
					}
					if(type==SLIDER) {
						Slider sl=(Slider)cp.controller(name);
						sl.setValue(txt.getFloat());
						sl.setMin(txt.getFloat());
						sl.setMax(txt.getFloat());
					}
				}
			}
		} while(res==txt.TOKENSTR);
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
  
  class GUINode {
  	public boolean isGroup;
		String name,className;
  	int width,height,type,id,posx,posy,paddingTop;
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
  				type=getType(control);
  				id=control.id();
  			} catch(Exception e) {
//  				UUtil.logErr(e.getMessage());
  			}

  			try {
  				group=(ControllerGroup)o;
  				if(className.contains("Drop")) paddingTop=group.getHeight();
  				isGroup=true;
  				type=getType(group);
  				id=control.id();
  			} catch(Exception e) {
//  				UUtil.logErr(e.getMessage());
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

		public void setPos(int cpx, int cpy) {
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

	public boolean hasController(String string) {
		if(cp.controller(string)!=null) return true;
		return false;
	}


}

