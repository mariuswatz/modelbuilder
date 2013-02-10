package unlekker.util;

import java.util.*;
import java.text.*;

import processing.core.PApplet;

/**
 * Simple utility class for calculating progress information, including time elapsed and estimated
 * time remaining. Useful for animation work. <code>start()</code> is called at
 * the start of whatever activity is to be tracked, then <code>update()</code> specifying the current
 * percentage completed which will return a String with the current status.
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 *
 */
public class UProgressInfo {
	static public final long MILLISPERDAY=1000*60*60*24;
	static public final long MILLISPERHOUR=1000*60*60;
	
	public long start,estimated,elapsed;
	public float perc;
	
	public String lastUpdate;
	
	public UProgressInfo() {
		start();
	}

	/**
	 * Called at the start of task that should be tracked.
	 */
	public void start() {
		start=System.currentTimeMillis();	
	}
	
	/**
	 * 
	 * @param _p Optional PApplet instance to include frame rate in output. Can be <code>null</code>. 
	 * @param _perc Percentage ([0..100]) of task completed.
	 * @return Status string with percentage, current frame rate (optional), time elapsed and estimated
	 * time remaining.
	 */
	public String update(PApplet _p,float _perc) {
		perc=_perc;
		
		float t=perc/100f;
		elapsed=System.currentTimeMillis()-start;
		
		if(perc<0.01f) estimated=0;
		else {
			float timeEst=(float)elapsed/t;
			timeEst=(timeEst*(1-t));
			estimated=(long)timeEst;
		}
		
		lastUpdate=UUtil.nf(perc,1,2)+"%";
		if(_p!=null) lastUpdate+=" FPS "+UUtil.nf(_p.frameRate,1,2);
		lastUpdate=lastUpdate+" Elapsed "+UUtil.timeStr(elapsed);
		lastUpdate=lastUpdate+" Remain "+UUtil.timeStr(estimated);

		return lastUpdate;
	}
	
}
