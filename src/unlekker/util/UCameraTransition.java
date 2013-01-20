package unlekker.util;

import controlP5.ControlEvent;
import processing.core.PApplet;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class UCameraTransition {
	public PApplet p;
	public UNav3D nav;
	public boolean isPaused;

	public boolean isRunning,isFirstFrame;
	public UNav3D cam[],theCam;
	public float camT,camCnt,camGoal;
	public int interpType=-1;
	static public int INTERSIGISMOID=0,INTEREXPEASE=1,
		INTERDBLSIGISMOID=2,INTEREASEIN=3,INTERQUAD=4,INTEREASEINOUT=5;
	long startRenderTime;
	public UProgressInfo progress=new UProgressInfo();
	private int viewNum;
	public UApp app;

	public UCameraTransition(PApplet _p,UNav3D _nav,String s) {
		p=_p;		
		nav=_nav;
		
		String tok[];
		
		s=s.substring(s.indexOf(" ")+1);
		tok=s.split(" ");

		progress=new UProgressInfo();
		stop();
		UUtil.log("Parsed transition: "+toDataString());
	}
	
	public UCameraTransition(PApplet _p) {
		p=_p;		

		theCam=new UNav3D(p,false);
		nav=new UNav3D(p);
		nav.setTranslation(p.width/2,p.height/2,0);
		
		setViewNum(3);
		camGoal=1200;
		
	}

	public void run() {
		if(isRunning) {
			stop();
			return;
		}
		
		if(app!=null) app.sketchCurrent.rewind();
		
		isRunning=true;
		isFirstFrame=true;
		UUtil.log("Transition.run() "+camGoal);
		
		startRenderTime=System.currentTimeMillis();
		progress.start();

//		p.startMovie();
		
		camCnt=0;
		camT=0;
//		p.keyEvent.setModifiers(p.keyEvent.CTRL_DOWN_MASK);
//		p.keyCode=p.keyEvent.VK_R;
//		p.keyPressed();
	}


	public void stopAndGoToEnd() {
			
		camCnt=camGoal;
		UUtil.log("stopAndGoToEnd() "+camGoal+" "+camT);
		stop();
	}
	
	public void stop() {
		isRunning=false;
		
		camCnt=0;
		doCam();
		UUtil.log("Transition.stop() "+camGoal+" "+camT+
				" | \n"+theCam.toStringData()+"\n"+
				nav.toStringData());
	}

  public void setCam(int id) {
  	cam[id].set(nav);
  	UUtil.log("Set camera "+(id)+" "+cam[id].toStringData());
  }  	

  public void setCam(int id,String in) {
  	in=UUtil.chopBraces(in);
//  	in=in.replace(',', ' ');
  	cam[id].set(in); 
  }  	
  
	public void setApp(UApp app) {
		this.app=app;
	}
	
	public void setGUI(USimpleGUI gui) {
		this.theCam.setGUI(gui);
	}



  public void setView(int id) {
  	nav.set(cam[id]); 
  	UUtil.log("Set view to camera "+(id)+" "+cam[id].toStringData());
  }  	
  
  public void setDuration(float t) {
  	camGoal=(int)(t*30);
  	UUtil.log("Duration: "+t+" == "+camGoal);
  }

	
	public void pause() {
		isPaused=!isPaused;
	}
	
	public void update() {
		
		if(isRunning) {
//			camT=p.sin(p.PI*0.5f*camT);
//			camT=camT*camT*camT;
//			p.control.status("T "+UUtil.nf(camT));
			doCam();
			nav.set(theCam);
			
			if(camT>=1) {
				camCnt=camGoal;
				camT=1;
				stop();
			}
			else camCnt++;
			if((int)camCnt%10==0) {
				float perc=((float)camCnt/(float)camGoal);
				String s=progress.update(p, perc*100f);
//				Util.log(s+" "+theCam.toStringData());
			}
					
			if(camCnt>1) isFirstFrame=false;
		}

	}

	public void doCam() {
		if(!isRunning) camT=1;
		else camT=camCnt/camGoal;	
		
		float T=camT*(float)(viewNum-1);
		int camID=p.min((int)T,viewNum-2);
		T=T-(float)(camID);
		
		/*
		 if (t < 1) return c/2*t*t + b;
	t--;
	return -c/2 * (t*(t-2) - 1) + b;
		 */

		T=UUtil.interExpEase(T, 0.2f);
//		T*=2;
//		if(T<1) T=0.5f*T*T;
//		else {
//			T-=1;
//			T=-0.5f*(T*(T-2)-1);
//		}
		
//		T*=2;
//		if(T<1) T=0.5f*T*T*T;
//		else {
//			T-=2;
//			T=0.5f*(T*T*T+2);
//		}
		
		theCam.interpolate(T, cam[camID], cam[camID+1]);
		nav.set(theCam);
//		UUtil.log("doCam "+UUtil.nf(camT)+" "+camID+" "+UUtil.nf(T));
//		cam.toStringData()+
//				" | "+camCnt+"/"+camGoal+" | "+
//				nav.toStringData());
	}
	
	public String camToString() {
		return ""+UUtil.nf(camT)+" "+cam.toString();
	}
	
	public String toString() {
		return ""+UUtil.nf(camT)+" "+cam.toString();
	}
	
	public String toDataString() {
		String s="Transition";
		for(int i=0; i<viewNum; i++) s+="\t"+cam[i].toStringData();
		s+="\t"+(int)camGoal;
		return s;
	}
	
	public UCameraTransition set(String in) {
		String tok[]=p.split(in, "\t");
		UUtil.log("TransitionMulti.set "+viewNum+" "+UUtil.toString(tok));
		
		int tokId=1;
		for(int i=0; i<viewNum; i++) setCam(i,tok[tokId++]);
		setDuration((float)UUtil.parseInt(tok[tokId++])/30f);
		return this;
	}

	public UCameraTransition setCamT(float t) {
		camT=t;
		camCnt=t*camGoal;
		doCam();
		return this;
	}

	public UCameraTransition setViewNum(int viewNum) {
		this.viewNum=viewNum;
		cam=new UNav3D[viewNum];
		
		for(int i=0; i<viewNum; i++) {
			cam[i]=new UNav3D(p,false);
			setCam(i);
		}

		return this;
	}

	public void addGUI(USimpleGUI gui) {
		String labels[]=new String[viewNum];
		for(int i=0; i<labels.length; i++) labels[i]="Set cam "+(i+1);
		gui.addDropDown("camSetCam", labels, 120);
		for(int i=0; i<labels.length; i++) labels[i]="Set view "+(i+1);
		gui.addDropDown("camSetView", labels, 120);
		gui.cp.group("camSetCam").setOpen(true);
		gui.addButton("run");
		gui.addButton("stop");
		
	}

	public void controlEvent(ControlEvent ev) {
	  if (ev.isGroup()) {
	  	UUtil.log("UCam controlEvent - "+ev.group().value()+" "+ev.group());
	  	String val=ev.group().stringValue();
	  	if(val.equalsIgnoreCase("set cam 1")) setCam(0);
	  	if(val.equalsIgnoreCase("set cam 2")) setCam(1);
	  	if(val.equalsIgnoreCase("set view 1")) setView(0);
	  	if(val.equalsIgnoreCase("set view 2")) setView(1);
	  	ev.group().setOpen(true);
	  }
	  else {
	  	if(ev.controller().name().equals("run")) run();
	  	if(ev.controller().name().equals("stop")) stop();
	  }

		
	}
}