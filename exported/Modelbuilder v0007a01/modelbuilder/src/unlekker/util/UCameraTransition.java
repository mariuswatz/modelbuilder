package unlekker.util;

import processing.core.PApplet;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class UCameraTransition {
	public PApplet p;
	public UNav3D nav;
	public boolean isPaused;

	public boolean isRunning;
	public UNav3D cam[],theCam;
	public float camT,camCnt,camGoal;
	public int interpType=-1;
	static public int INTERSIGISMOID=0,INTEREXPEASE=1,
		INTERDBLSIGISMOID=2,INTEREASEIN=3,INTERQUAD=4,INTEREASEINOUT=5;
	long startRenderTime;
	UProgressInfo progress=new UProgressInfo();

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
		cam=new UNav3D[3];
		nav=new UNav3D(p);
		for(int i=0; i<cam.length; i++) {
			cam[i]=new UNav3D(p,false);
			setCam(i);
		}
		camGoal=1200;
	}

	public void run() {
		isRunning=true;
		UUtil.log("Transition.run() "+camGoal);
		
		startRenderTime=System.currentTimeMillis();
		progress.start();

//		p.startMovie();
		
		camCnt=0;
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
					
		}

	}

	public void doCam() {
		if(!isRunning) camT=1;
		else camT=camCnt/camGoal;	
		
		float T=camT*(float)(cam.length-1);
		int camID=p.min((int)T,cam.length-2);
		T=T-(float)(camID);
		
		/*
		 if (t < 1) return c/2*t*t + b;
	t--;
	return -c/2 * (t*(t-2) - 1) + b;
		 */
		
		T*=2;
		if(T<1) T=0.5f*T*T;
		else {
			T-=1;
			T=-0.5f*(T*(T-2)-1);
		}
		
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
		for(int i=0; i<cam.length; i++) s+="\t"+cam[i].toStringData();
		s+="\t"+(int)camGoal;
		return s;
	}
	
	public void set(String in) {
		String tok[]=p.split(in, "\t");
		UUtil.log("TransitionMulti.set "+cam.length+" "+UUtil.toString(tok));
		
		int tokId=1;
		for(int i=0; i<cam.length; i++) setCam(i,tok[tokId++]);
		setDuration((float)UUtil.parseInt(tok[tokId++])/30f);
	}

	public void setCamT(float t) {
		camT=t;
		camCnt=t*camGoal;
		doCam();
	}
}