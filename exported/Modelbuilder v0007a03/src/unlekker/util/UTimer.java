package unlekker.util;

import processing.core.PApplet;
import unlekker.util.*;

public class UTimer {
	public float start,dur;
	public float t,tMult,alph=0,fadeIn=-1,fadeOut,fadeFinal;

	/**
	 * Constructor setting start time and duration relative to an external global timer T. 
	 * Both time and duration assumes <code>t</code>=[0..1] time period. <code>t</code> is
	 * not clamped to a maximum value of <code>t==1</code>, as there are cases where it can
	 * be useful to let the timer keep running past <code>t==1</code>. 
	 * 
	 *  Cases: <ul>
	 *  <li>At global <code>T<start</code>, <code>update()</code> returns <code>t==-1</code></li>
	 *  <li>At global <code>T==start</code>, <code>update()</code> returns <code>t==0</code></li>
	 *  <li>At global <code>T==(start+dur)</code>, <code>update()</code> returns <code>t==1</code></li>
	 *  <li>Given <code>start==0.2</code> and <code>dur==0.25</code>, T=0.25 gives <code>t==0.2</code>,
	 *  T=0.4 gives <code>t==0.8</code> and T=0.7 gives <code>t==2</code>
	 * 
	 * @param _start Starting time (start=[0..1])
	 * @param _dur Duration of timer (dur=0..1])
	 */
	public UTimer(float _start,float _dur) {
		set(_start,_dur);
	}
	
	public UTimer(String str) {
		str=UUtil.chopBraces(str);
		String tok[]=PApplet.split(str,UDataText.BLOCKSPACER);

		int id=0;
		start=UUtil.parseFloat(tok[id++]);
		dur=UUtil.parseFloat(tok[id++]);
		t=UUtil.parseFloat(tok[id++]);
		fadeIn=UUtil.parseFloat(tok[id++]);
		fadeOut=UUtil.parseFloat(tok[id++]);
		if(fadeIn>-1) {
			if(id<tok.length) fadeFinal=UUtil.parseFloat(tok[id++]);
			else fadeFinal=1;
		}
		
		if(start+dur>0.95f) start=0.95f-dur;
		set(start,dur);
	}

	public void set(float _start,float _dur) {
		start=_start;
		dur=_dur;
		tMult=1f/dur;
	}
	
	public void setFade(float _in,float _out,float _final) {
		fadeIn=_in;
		fadeOut=_out;		
		fadeFinal=_final-fadeOut;
	}
	
	public float update(float T) {
		if(T<start) return -1;
		t=T-start;
		t*=tMult;
		
		alph=1;
		if(fadeIn>-1) {
			if(t<fadeIn) alph=t/fadeIn;
			if(fadeOut>-1) {
				if(t>(fadeOut+fadeFinal)) return -1;
				else if(t>(fadeOut+fadeFinal)) alph=0;
				else if(t>fadeOut) {
					alph=1-(t-fadeOut)/fadeFinal;
					if(alph<0) alph=0;
				}
			}
		}
				
		return t;
		
	}
	
	public String toDataString() {
		String s="["+start+UDataText.BLOCKSPACER+dur+UDataText.BLOCKSPACER+UUtil.nf(t)+UDataText.BLOCKSPACER+
				UUtil.nf(fadeIn)+UDataText.BLOCKSPACER+UUtil.nf(fadeOut)+UDataText.BLOCKSPACER+UUtil.nf(fadeFinal)+"]";
		return s;
	}
	
}
