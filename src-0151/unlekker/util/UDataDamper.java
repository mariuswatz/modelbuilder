package unlekker.util;

import unlekker.util.*;

public class UDataDamper implements UConstants {
	public int n,cnt;
	public float val[];
	private double now[],old[],v[];
	
	
	public double dampFactor,dampMin;
	public boolean doNorm=true;
	
	private boolean isFirstTime=true;
	private double maxD,maxIntern,currmax,newmax,avg;;
	public float max,low,high;
	int maxCnt;

	public UDataDamper(int _n) {
		n=_n;
    val=new float[n]; // Visible outside -  - unmodified values
	  old=new double[n]; // Private - temp values
	  now=new double[n]; // Private - temp values
	  v=new double[n]; // Private - temp values

    low=200;
    high=2000;
    max=0;
    maxCnt=0;
    maxIntern=low;
	}
	
	 public void setLimits(float _low,float _high) {
		 low=_low;
		 high=_high;
	 }

	 public void setDamping(double _d) {
		 dampFactor=_d;
		 dampMin=1-dampFactor;
	 }

	 public void update(float [] in) {
	   for(int i=0; i<n; i++) now[i]=in[i];

	   if(isFirstTime) {
	  	 for(int i=0; i<n; i++) {
	  		 v[i]=now[i];
	  		 val[i]=in[i];
	  	 }
	  	 isFirstTime=false;
	  	 return;
	   }
	   
	   for(int i=0; i<n; i++) {
	  	 v[i]=now[i]*dampFactor+v[i]*dampMin;
	  	 if(v[i]>high) v[i]=high;
	   }

	   if(doNorm) {
	     currmax=low;
	     avg=0;
	     
	     for (int i=0; i<n; i++) {
	       avg+=v[i];
	       if(v[i]>currmax) currmax=v[i];
	     }
	     
	     if(currmax>high) currmax=high;	     
	    	 
	     if (currmax>maxIntern) {
//         maxIntern=currmax;
         maxD=(currmax-maxIntern)/4f;
         maxCnt=4;
       }

	     if(maxCnt>0) {
	       maxIntern+=maxD;
	       maxCnt--;
	     }
	     else {
	    	 if(maxIntern>low) 
		    	 maxIntern=currmax*0.005+maxIntern*(1-0.005);
		     else if(maxIntern>high) maxIntern=high;
	     }

	     avg/=(double)n;
	     cnt++;
	     for (int i=0; i<n; i++) {
	       val[i]=(float)(v[i]/maxIntern);
	       if(val[i]>1) val[i]=1;
	     }
	     max=(float)maxIntern;
//	     if(cnt<100) System.out.println(cnt+": max "+Str.nf(max)+" maxIntern "+Str.nf(maxIntern)+
//	    		 " curr "+Str.nf(currmax)+" "+
//	    		 maxCnt+"|"+Str.nf(maxD)+" avg "+Str.nf(avg));
	   }
	   else 
	  	 for(int i=0; i<n; i++) val[i]=(float)v[i]; 
	 }
}
