package unlekker.util;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.*;

import java.lang.*;
import java.lang.reflect.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Set of static utility functions. 
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */


public class UUtil implements UConstants {
	private static Runtime runtime;
	private final static String NULLSTR="NULL",EQUALSTR=" = ",COMMASTR=", ",
			DIVIDER="----------------------------------------------------------------------";
	private final static String STRSQBRACKETSTART="[",STRSQBRACKETEND="]",
			STRCURLYSTART="{",STRCURLYEND="}",STRGT=">",STRLT="<";
	private final static Class [] types={
		Float.class, Integer.class, Double.class, 
		Long.class, String.class, Boolean.class};
	
//	public static HashMap shapeTypes=null;

	/**
	 * Constants relating to trigonometry and Cos/Sin lookup tables.
	 */
  public static final float SINCOS_PRECISION=0.01f,SINCOS_INVPREC=1f/SINCOS_PRECISION;
  public static final int SINCOS_LENGTH= (int) (360f/SINCOS_PRECISION);
  public static final float sinLUT[]=new float[SINCOS_LENGTH];
  public static final float cosLUT[]=new float[SINCOS_LENGTH];
  public static boolean LUTINITIALIZED=false;

	/**
	 * Static initializer for the unlekker.util.Util class.  
	 *
	 */
  static {
    for (int i=0; i<SINCOS_LENGTH; i++) {
      sinLUT[i]= (float)Math.sin((float)i*DEG_TO_RAD*SINCOS_PRECISION);
      cosLUT[i]= (float)Math.cos((float)i*DEG_TO_RAD*SINCOS_PRECISION);
    }    
    rnd=new URnd();
    runtime=Runtime.getRuntime();
    logStart=System.currentTimeMillis();
    
//    shapeTypes=new HashMap(10,5);
//    shapeTypes.put(""+POLYGON, "POLYGON");
//    shapeTypes.put(""+TRIANGLES, "TRIANGLES");
//    shapeTypes.put(""+TRIANGLE_STRIP, "TRIANGLE_STRIP");
//    shapeTypes.put(""+QUADS, "QUADS");
//    shapeTypes.put(""+QUAD_STRIP, "QUAD_STRIP");
//    shapeTypes.put(""+POINTS, "POINTS");
//    shapeTypes.put(""+LINES, "LINES");
//    
//    Util.logOn();
//
//    Object[]     set=shapeTypes.keySet().toArray();
//    for(int i=0; i<set.length; i++) {
//    	String name=set[i].getClass().getSimpleName();
//    	if(name.equals("String")) Util.log("Data: "+(String)set[i]);
//    }
//    set=shapeTypes.values().toArray();
//    for(int i=0; i<set.length; i++) {
//    	String name=set[i].getClass().getSimpleName();
//    	Util.log(i+": "+name);
//    	if(name.equals("String")) Util.log("Data: "+(String)set[i]);
//    } 
//    Util.log("shapeTypes.get: "+(String)shapeTypes.get(""+POLYGON));
    
    for(int i=0; i<shapeTypes.length; i++) shapeTypes[i]="Constant "+i+" Undefined";
    shapeTypes[POINT]="POINT";
    shapeTypes[POINTS]="POINTS";
    shapeTypes[LINES]="LINES";
    shapeTypes[LINE]="LINE";
    shapeTypes[TRIANGLE]="TRIANGLE";
    shapeTypes[TRIANGLES]="TRIANGLES";
    shapeTypes[TRIANGLE_STRIP]="TRIANGLE_STRIP";
    shapeTypes[TRIANGLE_FAN]="TRIANGLE_FAN";
    shapeTypes[QUAD]="QUAD";
    shapeTypes[QUADS]="QUADS";
    shapeTypes[QUAD_STRIP]="QUAD_STRIP";
    shapeTypes[POLYGON]="POLYGON";
    shapeTypes[PATH]="PATH";
    shapeTypes[RECT]="RECT";
    shapeTypes[ELLIPSE]="ELLIPSE";
    shapeTypes[ARC]="ARC";
    shapeTypes[SPHERE]="SPHERE";
    shapeTypes[BOX]="BOX";
    shapeTypes[shapeTypes.length-1]="No such shape type ";
  }
  
  /**
   * Static copy of unlekker.util.Rnd for easy random number generation.
   */
  public static URnd rnd;
  
  private static long timerStart;
  


  /**
   * Provides an easy way to build an expiry date into an application. Useful when you send work to exhibitions
   * and you don't want people to keep "live" copies.
   *
   * @param year
   * @param month
   * @param day
   * @return
   */
  public static boolean checkExpiryDate(int year,int month,int day) {
  	Calendar expirydate=Calendar.getInstance();
		expirydate.set(year, month-1, day);
		cal=Calendar.getInstance();
		
		if(cal.getTime().compareTo(expirydate.getTime())>0) {
			logErrPrint("checkExpiryDate(): Has expired...");
			return true;
		}
		return false;
	} 

  static Calendar cal=Calendar.getInstance();
  static int logStyle=0;
  public static int LOGSINCESTART=1,LOGCURRTIME=0;
  public static boolean doLog=true,logNoPrint=false;
  public static long logStart;
  
	private static String logFilename;
	private static Logger logger;
	private static FileInputStream logStream;
	private static FileHandler logHandler;
	public static ULogUtil logUtil;
	public static PFont logFnt;
	public static int logMsgN=40;
	public static String logMsg[];
  
  public static void logStyle(int style) {
  	logUtil.logStyle=style;
  	logStyle=style;
  }
  
  /**
   * logPrint() prints the String s to System.out with a timestamp indicating the hour and minute
   * it was printed.
   * @param s String to be printed.
   */
  public static void logPrint(String s) {
  	if(!doLog) return;
  	
  	if(logMsg==null) logMsg=new String[logMsgN];
  	
  	String tstr=null;
  	if(logStyle==LOGSINCESTART) 
  		tstr=timeStr(System.currentTimeMillis()-logStart);
  	else {
    	cal.setTimeInMillis(System.currentTimeMillis());
    	tstr=nf(cal.get(Calendar.HOUR_OF_DAY),2) +
    		":"+nf(cal.get(Calendar.MINUTE),2);
  	}

  	if(!logNoPrint) System.out.println(tstr+" "+s);
  	System.arraycopy(logMsg,0,logMsg,1,logMsgN-1);
  	logMsg[0]=s;
  	if(logger!=null) logger.info(s);
  }

  public static void logOn() {
  	doLog=true;
  }

  public static void logOff() {
  	doLog=false;
  }
  
  
  public static void log(String s) {
  	int pos=s.indexOf('\n');
  	if(pos!=-1) {
  		if(pos==0) {
  			logPrint("");
  			log(s.substring(pos+1));
  		}
  		else {
  			logPrint(s.substring(0,pos));
  			if(pos==s.length()-1) log("");
  			else log(s.substring(pos+1));
  		}
  	}  	
  	else logPrint(s);
  }

  public static void log(String [] s) {
  	if(s==null) logPrint("log: String array == null.");
  	for(int i=0; i<s.length; i++) logPrint(i+"/"+s.length+": '"+s[i]+"'");
  }

  public static void log(Object [] o) {
  	if(o==null) logPrint("log: String array == null.");
  	for(int i=0; i<o.length; i++) logPrint(i+"/"+o.length+":"+o[i]);
  }

  public static void log(float [] f) {
  	if(f==null) logPrint("log: Float array == null.");
  	for(int i=0; i<f.length; i++) logPrint(i+":"+nf(f[i]));
  }
  
  public static void log(int [] val) {
  	if(val==null) logPrint("log: Int array == null.");
  	for(int i=0; i<val.length; i++) logPrint(i+": "+val[i]);
  }
  
	public static void log(long[] val) {
  	if(val==null) logPrint("log: Long array == null.");
  	for(int i=0; i<val.length; i++) logPrint(i+": "+val[i]);
	}

  
//  public static void log(String [] s,int min,int max) {
//  	if(s==null) logPrint("log: String array == null.");
//  	min=Data.max(0,min);
//  	max=Data.min(max,s.length);
//  	for(int i=min; i<max; i++) logPrint(i+":"+s[i]);
//  }

  public static void logDivider(String s) {
  	logPrint("");
  	logPrint(DIVIDER);
  	logPrint(s);
  }

  public static void logDivider() {
  	logPrint("");
  	logPrint(DIVIDER);
  }

  public static void logErr(String s) {
  	logPrint(s);
  }

  public static void logErrDivider(String s) {
  	logErr("");
  	logErr(DIVIDER);
  	logErr(s);
  }
  

	public static void logDoPrint(boolean yesorno) {
		logNoPrint=yesorno;	  
  }


  	
  /**
   * logPrintErr() prints the String s to System.err with a timestamp indicating the hour and minute
   * it was printed.
   * @param s String to be printed.
   */
  public static void logErrPrint(String s) {
  	cal.setTimeInMillis(System.currentTimeMillis());
  	System.err.println(
  			nf(cal.get(Calendar.HOUR_OF_DAY),2) +
  			":"+nf(cal.get(Calendar.MINUTE),2)+"|  "+
  			s);
  	
  	if(logger!=null) logger.warning(s);
  }

  public static void logErrStackTrace(Exception e) {
  	if(e==null) return;
		StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    logErr(sw.toString());
  }
  
  // LOG TO FILE

  public static void logToFile(String filename) {
  	logToFile(filename,false);
  }

  public static void logToFile(String filename,boolean append) {
  	if(logger==null) try {
  		Calendar cal=Calendar.getInstance();
  		
    	logFilename=filename;   	
//    	java.util.logging.ConsoleHandler.level=NONE;
    	logger=Logger.getLogger("Util");
    	logger.setUseParentHandlers(false);
    	
    	logUtil=new ULogUtil();
    	logger.setFilter(logUtil);

    	// Erase log once a week
    	File f=new File(logFilename);
    	if(append && f.exists()) {
    		Calendar mod=Calendar.getInstance();
    		mod.setTimeInMillis(f.lastModified());
    		log("Modified: "+timeStr(mod));
    		int diff=(int)(
    			(System.currentTimeMillis()-mod.getTimeInMillis())/
    			(24*60*60*1000));
    		log("Diff: "+(diff)+" days.");
    		if(diff>=7) {
    			log("Log file older than 1 week. Deleting.");
    			logHandler=new FileHandler(logFilename);
    		}
    	}
    	
    	if(logHandler==null) logHandler=new FileHandler(logFilename,append);
    	
    	logger.addHandler(logHandler);
    	logHandler.setFormatter(logUtil);
    	
			logDivider("Added log file output: '"+logFilename+"'");
//    	log("Log started: "+timeStr(cal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logErrStackTrace(e);
			logger=null;
			logHandler=null;
		}
  	
  }
  
  public static void beginTimer() {
  	timerStart=System.currentTimeMillis();
  }

  public static long endTimer() {  	
  	return System.currentTimeMillis()-timerStart;
  }

	public static String shapeType(int type) {
		if(type<0 || type>shapeTypes.length-2) 
			return shapeTypes[shapeTypes.length-1]+" ("+type+")";
		return shapeTypes[type];
	}
	
	// try a=0.220
	static public float interExpEase (float x, float a){
	  
	  float epsilon = 0.00001f;
	  float min_param_a = 0.0f + epsilon;
	  float max_param_a = 1.0f - epsilon;
	  a = PApplet.max(min_param_a, PApplet.min(max_param_a, a));
	  
	  if (a < 0.5){
	    // emphasis
	    a = 2.0f*(a);
	    float y = PApplet.pow(x, a);
	    return y;
	  } else {
	    // de-emphasis
	    a = 2.0f*(a-0.5f);
	    float y = PApplet.pow(x, 1.0f/(1-a));
	    return y;
	  }
	}

	// try a=0.7
	static public float interSigmoid (float x, float a){
	  // n.b.: this Logistic Sigmoid has been normalized.

	  float epsilon = 0.0001f;
	  float min_param_a = 0.0f + epsilon;
	  float max_param_a = 1.0f - epsilon;
	  a = PApplet.max(min_param_a, PApplet.min(max_param_a, a));
	  a = (1/(1-a) - 1);

	  float A = 1.0f / (1.0f + PApplet.exp(0 -((x-0.5f)*a*2.0f)));
	  float B = 1.0f / (1.0f + PApplet.exp(a));
	  float C = 1.0f / (1.0f + PApplet.exp(0-a)); 
	  float y = (A-B)/(C-B);
	  return y;
	}

	// a=0.7
	static public float interDblSigmoid (float x, float a){

	  float epsilon = 0.00001f;
	  float min_param_a = 0.0f + epsilon;
	  float max_param_a = 1.0f - epsilon;
	  a = PApplet.min(max_param_a, PApplet.max(min_param_a, a));
	  a = 1.0f-a; // for sensible results
	  
	  float y = 0;
	  if (x<=0.5){
	    y = (PApplet.pow(2.0f*x, 1.0f/a))/2.0f;
	  } else {
	    y = 1.0f - (PApplet.pow(2.0f*(1.0f-x), 1.0f/a))/2.0f;
	  }
	  return y;
	}

	public static float interEaseIn(float t, float b, float c, float d) {
		return -c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;
	}

	public static float interEaseOut(float t, float b, float c, float d) {
		return c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
	}
	
	public static float interGaussian(float x, float mean, float variance) {
	  return (float)((1 / Math.sqrt(TWO_PI * variance)) * Math.exp(-sq(x - mean) / (2 * variance)));
	}
	
	public static float sq(float a) {return a*a;}
	public static double sq(double a) {return a*a;}
	
	/*
	 * Math.easeInOutQuad = function (t, b, c, d) {
		t /= d/2;
		if (t < 1) return c/2*t*t + b;
		t--;
		return -c/2 * (t*(t-2) - 1) + b;
	};
	 */

	public static float interEaseInOut(float t, float b, float c, float d) {
		return -c / 2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;
	}
  /////////////////////////////////////////////////
  // RUNTIME UTILITIES
  
	public static boolean classExists (String className) {
    try {
      Class.forName (className);
      return true;
    }
    catch (ClassNotFoundException exception) {
      return false;
    }
  } 

  public static long memoryMax() {
  	long mem=0;
  	
  	for(int i=0; i<10; i++) mem+=runtime.maxMemory();
  	mem/=10;
  	return mem;
  }

  public static long memoryTotal() {
  	long mem=0;
  	
  	for(int i=0; i<10; i++) mem+=runtime.totalMemory();
  	mem/=10;
  	return mem;
  }

  public static long memoryFree() {
  	long mem=0;
  	
  	for(int i=0; i<10; i++) mem+=runtime.freeMemory();
  	mem/=10;
  	return mem;
  }
  
  public static long memoryUsed() {
  	long mem=0;
  	
  	for(int i=0; i<10; i++) mem+=memoryTotal()-memoryFree();
  	mem/=10;
  	return mem;
  }
  
  public static String memoryStats() {
  	float MB=1024f*1024f;
  	String s="Mem: "+nf((float)memoryFree()/MB,0,1)+" free "+
  		"| "+nf((float)memoryTotal()/MB,0,1)+" total | "+
  		nf((float)memoryMax()/MB,0,1)+" max.";
  	return s;
  }

  public static void runtimeGarbageCollector() {
  	long mem=memoryUsed();
  	runtime.runFinalization();
  	runtime.gc();
  	float diff=(float)(memoryUsed()-mem)/(1024f*1024f);
//  	Util.log("\nForcing Java garbage collection... "+
//  			nf(diff,0,1)+" MB difference.");
  }
  
  public static String[] runtimeExec(String cmd,boolean wait) {
  	String s[]=null;
  	int n;
  	
	  try  { 
	    Process p=Runtime.getRuntime().exec(cmd); 
	    if(wait) p.waitFor(); 
	    
	    BufferedReader reader=
	    	new BufferedReader(new InputStreamReader(p.getInputStream())); 
	    
	    
	    s=new String[200];
	    n=0;
	    
	    String line=reader.readLine(); 
	    while(line!=null) { 
	    	if(n==s.length) {
	    		String [] tmp=new String[n*2];
	    		System.arraycopy(s, 0, tmp, 0, n);
	    		s=tmp;
	    	}
	    	s[n++]=line;
	      line=reader.readLine(); 
	    } 
	    
	    s=stripNullStrings(s);
	  } 
	  catch(Exception e1) {
	    logErrPrint("execCommand() failed: "+e1.toString());
	    s=null;
	  } 
  	return s;
  }

  public static PImage imgResize(PImage img,float w,float h) {
  	float sz=1;
  	
  	if(w<0) {
  		sz=h/(float)img.height;
  	}
  	else if(h<0) {
  		sz=w/(float)img.width;
  	} 
  	
  	return imgResize(img, (int)((float)img.width*sz));
  }

  public static PImage imgResize(PImage img,float w) {
  	BufferedImage tmp=(BufferedImage )img.getImage();
  	float mod=w/(float)tmp.getWidth();
//  	log("mod "+mod+" dim "+tmp.getWidth()+" "+tmp.getHeight());
  	
  	PImage tmp2=new PImage(tmp.getScaledInstance(
  			(int)w, (int)Math.round((float)tmp.getHeight()*mod),   
  			java.awt.Image.SCALE_SMOOTH));
  	
//  	log("mod "+mod+" dim "+tmp2.width+" "+tmp2.height);

  	return tmp2;
  }
  
  /**
	 * Returns a String representation of object "o", containing the values 
	 * of all its fields. 
	 */

	public static String listObjectFields(Object o) {
		Object otmp;
		Class ctmp;
		
		try {
			Class cl=o.getClass();
			Field[] f=cl.getFields();

			final StringBuffer buf=new StringBuffer(500);
			Object value=null;
			buf.append(cl.getSimpleName());
			buf.append('@');
			buf.append(o.hashCode());
			buf.append(" = {");

			for (int idx=0; idx<f.length; idx++) {

				if (idx!=0)
					buf.append(COMMASTR);

				buf.append(f[idx].getName());
				buf.append(EQUALSTR);
				
				ctmp=null;
				otmp=f[idx].get(o);
				if(otmp!=null) {
					ctmp=otmp.getClass();
					if(ctmp.equals(types[0])) buf.append(nf(f[idx].getFloat(o)));
					else if(ctmp.equals(types[1])) buf.append(f[idx].getInt(o));
					else if(ctmp.equals(types[2])) buf.append(nf(f[idx].getDouble(o)));
					else if(ctmp.equals(types[3])) buf.append(f[idx].getLong(o));
					else if(ctmp.equals(types[4])) {
						buf.append('"');
						buf.append((String)otmp);
						buf.append('"');
					}
					else if(ctmp.equals(types[5])) buf.append(f[idx].getBoolean(o));
					else buf.append(ctmp.getName());
				}
				else buf.append(NULLSTR);
			}

			buf.append("}");

			return wrapText(buf.toString(),70);

		} catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			//throw new RuntimeException(ex);
		}
		
		return null;
	}
	
	public Object deepCopy(Object objectTree) throws IOException,
		ClassNotFoundException {
		ByteArrayOutputStream outStore = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(outStore);
		out.writeObject(objectTree);
		ByteArrayInputStream inStore = new
		ByteArrayInputStream(outStore.toByteArray());
		ObjectInputStream in = new ObjectInputStream(inStore);
		return in.readObject();
	}
	
	/**
	 * Expands an array of Objects to double of its current size. Remember to use cast to turn the result into an array of the appropriate class, i.e:
	 * 
	 *   <code>someArray=(SomeClass [])unlekker.util.Util.expandArray(someArray);
	 * @param list Array to be expanded.
	 * @return Array of Object [], must be cast to appropriate class.
	 */
	static public Object expandArray(Object[] list) {
		int newSize=list.length*2;
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, 0, temp, 0,	
                     Math.min(Array.getLength(list), newSize));
    return temp;
  }

	static public Object expandArray(Object[] list,int newSize) {
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, 0, temp, 0,	
                     Math.min(Array.getLength(list), newSize));
    return temp;
  }

	/**
	 * Expands an array of floats to double its current size.
	 *
	 * @param f Array to be expanded.
	 * @return Array of floats with f.length*2 length. 
	 */
	static public float[] expandArray(float[] f) {
		int newSize=f.length*2; 
		float [] newf=new float[newSize];
    System.arraycopy(f, 0, newf, 0,	
                     Math.min(f.length, newSize));
    return newf;
  }

	/**
	 * Expands the first column of a 2D array of floats to 
	 * double its current size.
	 *
	 * @param f Array to be expanded.
	 * @return Array of floats with f.length*2 length. 
	 */
	static public float[][] expandArray(float[][] f) {
		int newSize=f.length*2; 
		float [][] newf=new float[newSize][];
		for(int i=0; i<f.length; i++) newf[i]=f[i];
    return newf;
  }

	/**
	 * Expands an array of ints to double its current size.
	 *
	 * @param f Array to be expanded.
	 * @return Array of ints with f.length*2 length. 
	 */
	static public int[] expandArrayInt(int[] f) {
		return expandArray(f);
	}
	
	static public int[] expandArray(int[] f) {
		int newSize=f.length*2; 
		int [] newi=new int[newSize];
    System.arraycopy(f, 0, newi, 0,	
                     Math.min(f.length, newSize));
    return newi;
  }

	static public int[] expandArray(int[] f,int newSize) {
		int [] newi=new int[newSize];
    System.arraycopy(f, 0, newi, 0,	
                     Math.min(f.length, newSize));
    return newi;
  }

	static public Object extractArray(Object[] list,int pos,int n) {
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, n);
		System.arraycopy(list, pos, temp, 0,n);
		return temp;
	}
	
	/**
	 * Trims an array to a given size.
	 *
	 * @param f Array to be trimmed.
	 * @return Trimmed array 
	 */
	
	static public Object trimArray(Object[] list,int newSize) {
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, 0, temp, 0,	newSize);                     
    return temp;
	}

	static public Object trimArray(Object[] list,int start,int end) {
		int newSize=end-start;
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, start, temp, 0,	newSize);                     
    return temp;
	}

	/**
	 * Resizes an array of Objects to a specified size.
	 *
	 * @param list Array to be resized.
	 * @param newSize Array size after resizing
	 * @return Array of type Object with the specified size
	 */
	static public Object resizeArray(Object[] list, int newSize) {
    Class type = list.getClass().getComponentType();
    Object temp = Array.newInstance(type, newSize);
    System.arraycopy(list, 0, temp, 0,	
                     Math.min(Array.getLength(list), newSize));
    return temp;
  }

	/**
	 * Resizes an array of ints to a specified size.
	 *
	 * @param list Array to be resized.
	 * @param newSize Array size after resizing
	 * @return Array of ints with the specified size
	 */
	static public int [] resizeArrayInt(int[] val, int newSize) {
		int [] newval=new int[newSize];
    System.arraycopy(val, 0, newval, 0,	
                     Math.min(val.length, newSize));
    return newval;
  }


	/**
	 * Expands an array of floats to double its current size.
	 *
	 * @param f Array to be expanded.
	 * @return Array of floats with f.length*2 length. 
	 */
	static public float[] resizeArrayFloat(float[] f,int newSize) {
		float [] newf=new float[newSize];
    System.arraycopy(f, 0, newf, 0,	
                     Math.min(f.length, newSize));
    return newf;
  }
	
	static public long[] resizeArray(long[] f,int newSize) {
		long [] newf=new long[newSize];
    System.arraycopy(f, 0, newf, 0,	
                     Math.min(f.length, newSize));
    return newf;
	}

	/**
	 * Sine function based on lookup tables. About 4 times as fast as Math.sin, but be careful not to give 
	 * a value of more than 360 degrees or less than zero. If in doubt use @see #SINSafer, it rounds the value off before use.
	 * @param deg Angle in degrees
	 * @return Sine of angle 
	 */
	public final static float SIN(float deg) {
		deg*=SINCOS_INVPREC;
		return sinLUT[(int)deg];
	}

	/**
	 * Cosine function based on lookup tables. About 4 times as fast as Math.cos, but be careful not to give 
	 * a value of more than 360 degrees or less than zero. If in doubt use @see #COSSafer, it rounds the value off before use.
	 * @param deg Angle in degrees
	 * @return Cosine of angle 
	 */
	public final static float COS(float deg) {
		deg*=SINCOS_INVPREC;
		return cosLUT[(int)deg];
	}

	/**
	 * Fast sine function based on lookup tables. Unlike SIN(), SUBSafer() checks the bounds of the degree before use.
	 * @return Sine of angle 
	 * @param deg Angle in degrees
	 */
	public final static float SINSafer(float deg) {
		deg=deg%360;
		if(deg<0) deg+=360;
		
		deg*=SINCOS_INVPREC;
		return sinLUT[(int)deg];
	}

	/**
	 * Fast cosine function based on lookup tables. Unlike COS(), COSSafer() checks the bounds of the degree before use.
	 * @return Cosine of angle 
	 * @param deg Angle in degrees
	 */
	public final static float COSSafer(float deg) {
		deg=deg%360;
		if(deg<0) deg+=360;

		deg*=SINCOS_INVPREC;
		return cosLUT[(int)deg];
	}

	/**
	 * Calculates length of a circular arc, using the C=PI * a formula.
	 * @param a
	 * @param radius
	 * @return
	 */
	public final static float arcLength(float a,float radius) {
		float val=(PI*radius*2f)*(a/TWO_PI);		
		return val;
	}
	
	public static boolean deleteFile(String string) {
		// TODO Auto-generated method stub
		File f=new File(string);
		log("Delete: '"+string+"' "+f.exists());
		return f.delete();
		
	}
	
	
	////////////////////////////////////////////////////////
	// STRING UTILS
	
	public static StringBuffer buf;
	private static String strBuf[];
	private static int strBufCnt;
	private static NumberFormat formatFloat, formatInt;
	private static char numberChar[]=new char[] {'0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '-', '.'};

	// ///////////////////////////////////////////////////////
	// STRING BUFFERS

	public static void beginStringBuffer(ArrayList buf) {
	}

	public static void addToStringBuffer(ArrayList buf, String s) {
		buf.add(s);
	}

	public static String[] endStringBuffer(ArrayList buf) {
		String s[]=new String[buf.size()];
		s=(String[])buf.toArray(s);
		buf.clear();
		return s;
	}

	public static void beginStringBuffer() {
		strBuf=new String[100];
		strBufCnt=0;
	}

	public static void addToStringBuffer(String s) {
		if (strBufCnt==strBuf.length) strBuf=(String[])UUtil.expandArray(strBuf);
		strBuf[strBufCnt++]=s;
	}

	public static void addToStringBuffer(String s[]) {
		if (strBufCnt+s.length>=strBuf.length)
			strBuf=(String[])UUtil.resizeArray(strBuf, s.length+strBuf.length);
		System.arraycopy(s, 0, strBuf, strBufCnt, s.length);
		strBufCnt+=s.length;
	}

	public static String[] endStringBuffer() {
		String s[]=null;
		if (strBufCnt>0) s=(String[])UUtil.resizeArray(strBuf, strBufCnt);
		strBuf=null;

		return s;
	}

	/**
	 * Shortens the string "s" to a length of "len" characters.
	 */
	public static String shorten(String s, int len) {
		if (s==null) return null;
		if (s.length()>len) s=s.substring(0, len-2)+"..";
		return s;
	}

	/**
	 * Shortens the string "s" to a length of 70 characters.
	 */
	public static String shorten(String s) {
		if (s==null) return null;
		if (s.length()>70) s=s.substring(0, 67)+"...";
		return s;
	}

	public static String[] append(String[] list, String[] tmp) {
		int sz=list.length;
		list=(String[])UUtil.resizeArray(list, sz+tmp.length);
		System.arraycopy(tmp, 0, list, sz, tmp.length);
		return list;
	}

	/**
	 * Strips null Strings from an array of Strings.
	 * 
	 * @param str
	 *          Array to remove null strings from.
	 * @return String array with null strings removed.
	 */
	public static String[] stripNullStrings(String[] str) {
		if (str==null) return null;

		// propagate null strings to the end
		for (int i=str.length-1; i>0; i--) {
			if (str[i]!=null&&str[i-1]==null) {
				str[i-1]=str[i];
				str[i]=null;
			}
		}

		int numvalid=0;
		for (int i=0; i<str.length; i++)
			if (str[i]!=null) numvalid=i+1;

		if (numvalid==0) return null;

		String tmp[]=new String[numvalid];
		System.arraycopy(str, 0, tmp, 0, numvalid);
		return tmp;
	}
	
	public static String stripNonNumeric(String str) {
		if (str==null) return null;
		String s="";
		
		int n=str.length();
		char c;
		for(int i=0; i<n; i++) {
			c=str.charAt(i);
			if(Character.isDigit(c)) s+=c;
		}
		
		return s;
	}
	
	public static String [] copy(String s[]){
		return copy(s,s.length);
	}
	
	public static String [] copy(String s[],int n){
		String ss[]=new String[n];
		for(int i=0; i<n; i++) ss[i]=""+s[i];
		return ss;
	}

	/**
	 * Removes '[' and ']' from beginning and end of a String,
	 * but only if both are present. 
	 * @param s
	 * @return
	 */
	public static String chopBraces(String s) {
		if(s.startsWith(STRSQBRACKETSTART) && s.endsWith(STRSQBRACKETEND))
			return s.substring(1,s.length()-1);
//		boolean changed;
//		do {
//			changed=true;
//			if(s.startsWith(STRSQBRACKETSTART)) s=s.substring(1);
//			else if(s.startsWith(STRCURLYSTART)) s=s.substring(1);
//			else if(s.startsWith(STRLT)) s=s.substring(1);
//			else if(s.endsWith(STRSQBRACKETEND)) s=s.substring(0,s.length()-1);
//			else if(s.endsWith(STRCURLYEND)) s=s.substring(0,s.length()-1);
//			else if(s.endsWith(STRGT)) s=s.substring(0,s.length()-1);
//			else changed=false;
//		} while(changed);
		
		return s;
//		
//		if(s==null || (s.indexOf('[')<0 && s.indexOf('{')<0)) return s;
//		return s.substring(1,s.length()-1);		
	}

	/**
	 * Will remove all occurrences of the characters '&lt;', '&gt;', '[', ']', '{' and '}'
	 * from the input string and return the result. Beware: While <code>chopBraces()</code>
	 * only removes those characters from the beginning and end of a string, <code>stripBraces()</code>
	 * will remove characters from all positions in the string.
	 * @param s
	 * @return
	 */
	public static String stripBraces(String s) {
		char c[]=s.toCharArray();
		char tmp[]=new char[c.length];
		
		int id=0;
		for(int i=0; i<c.length; i++) {
			char cc=c[i];
			if(cc!='<' && cc!='>' && cc!='[' && cc!=']' &&
					cc!='{' && cc!='}') tmp[id++]=c[i];
		}
		
		char res[]=new char[id];
		System.arraycopy(tmp, 0, res, 0, id);
		return new String(res);
//		
//		if(s==null || (s.indexOf('[')<0 && s.indexOf('{')<0)) return s;
//		return s.substring(1,s.length()-1);		
	}
	
	public static String toString(float[] f) {
		if(f==null) return "null";
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<f.length; i++) {
			buf.append(nf(f[i]));
			if (i<f.length-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(int[] val) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<val.length; i++) {
			buf.append(val[i]);
			if (i<val.length-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(String[] s) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<s.length; i++) {
			buf.append(s[i]);
			if (i<s.length-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(String[] s,
			String pre,String post,String separator,boolean outputNull) {
		StringBuffer buf=new StringBuffer();
		buf.append(pre);
		for (int i=0; i<s.length; i++) {
			if(outputNull || s[i]!=null) {
				buf.append(s[i]);
				if (i<s.length-1) buf.append(separator);
			}
		}
		buf.append(post);
		return buf.toString();
	}

	public static String toString(Object[] o) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<o.length; i++) {
			if(o[i]!=null) buf.append(o[i].toString());
			else buf.append("null");
			if (i<o.length-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(Object[] o,String delim) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<o.length; i++) {
			if(o[i]!=null) buf.append(o[i].toString());
			else buf.append("null");
			if (i<o.length-1) buf.append(delim);
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(Object[] o,int start,int end) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=start; i<end; i++) {
			buf.append(o[i].toString());
			if (i<end-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(Object[] o,String delim,int start,int end) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=start; i<end; i++) {
			buf.append(o[i].toString());
			if (i<end-1) buf.append(delim);
		}
		buf.append(']');
		return buf.toString();
	}


	/**
	 * Prints an array of Strings to System.out.
	 * 
	 * @param s
	 *          Array to print
	 * @param lineNumbers
	 *          Controls whether line numbers are printed or not.
	 */
	public static void printStrArray(String[] s, boolean lineNumbers) {
		if (s!=null) {
			if (lineNumbers) {
				for (int i=0; i<s.length; i++)
					System.out.println(i+": "+s[i]);
			} else {
				for (int i=0; i<s.length; i++)
					System.out.println(s[i]);
			}

		}
	}

	/**
	 * Returns file name without path information.
	 * 
	 * @param filename
	 * @return
	 */
	public static String shortFilename(String filename) {
		int pos=filename.lastIndexOf(java.io.File.separatorChar);
		if (pos!=-1) return filename.substring(pos+1);
		else return filename;
	}

	/**
	 * Takes a long String and breaks it down into an array of separate Strings of
	 * maximum "len" length. Useful for preparing text for display on screen.
	 * 
	 * @param s
	 *          String to be wrapped
	 * @param len
	 *          Maximum character length of each line
	 * @return Array of String objects containing the wrapped text.
	 */
	public static String[] wrapTextArray(String s, int len) {
		String wrapped[], tmp[];
		int pos, num=0;

		if (buf==null) buf=new StringBuffer();
		buf.setLength(0);

		buf.append(s);
		wrapped=new String[10];
		while (buf.length()>0) {
			if (buf.length()>len) {
				pos=len-1;
				while (pos>0&&!Character.isWhitespace(buf.charAt(pos)))
					pos--;
				if (pos==0) pos=len-1;
				// ins=buf.substring(0,pos);
				buf.delete(0, pos);
			} else {
				// ins=buf.toString();
				buf.setLength(0);
			}

			if (wrapped.length==num) {
				tmp=new String[wrapped.length*2];
				System.arraycopy(wrapped, 0, tmp, 0, wrapped.length);
				wrapped=tmp;
			}
		}

		tmp=new String[num];
		System.arraycopy(wrapped, 0, tmp, 0, num);

		return tmp;
	}

	public static void numFloatFormat(float lead, float trail) {

	}

	public static boolean isNumber(String s) {
		char ch;
		int id, len;
		boolean valid=true;

		len=s.length();
		for (int i=0; i<len; i++) {
			ch=s.charAt(i);
			if (!(Character.isDigit(ch)||ch=='-'||ch=='+'||ch=='.')) return false;
		}

		return true;
	}

	static public String intToHex(int val) {
		return Integer.toHexString(val);
	}

	static public void nfInitFormats() {
		formatFloat=NumberFormat.getInstance();
		formatFloat.setGroupingUsed(false);

		formatInt=NumberFormat.getInstance();
		formatInt.setGroupingUsed(false);
	}

	/**
	 * Format floating point number for printing
	 * 
	 * @param num
	 *          Number to format
	 * @param lead
	 *          Minimum number of leading digits
	 * @param digits
	 *          Number of decimal digits to show
	 * @return Formatted number string
	 */
	static public String nf(float num, int lead, int decimal) {
		if (formatFloat==null) nfInitFormats();
		formatFloat.setMinimumIntegerDigits(lead);
		formatFloat.setMaximumFractionDigits(decimal);
		formatFloat.setMinimumFractionDigits(decimal);

		return formatFloat.format(num).replace(",", ".");
	}

	static public String nf(double num, int lead, int decimal) {
		return nf((float)num,lead,decimal);
	}

	/**
	 * Format floating point number for printing with maximum 3 decimal points.
	 * 
	 * @param num
	 *          Number to format
	 * @return Formatted number string
	 */
	static public String nf(float num) {
		if (formatFloat==null) nfInitFormats();
		formatFloat.setMinimumIntegerDigits(1);
		formatFloat.setMaximumFractionDigits(3);

		return formatFloat.format(num).replace(",", ".");
	}

	static public String nf(double num) {
		return nf((float)num);
	}

	/**
	 * Format integer number for printing, padding with zeros if number has fewer
	 * digits than desired.
	 * 
	 * @param num
	 *          Number to format
	 * @param digits
	 *          Minimum number of digits to show
	 * @return Formatted number string
	 */
	static public String nf(int num, int digits) {
		if (formatInt==null) nfInitFormats();
		formatInt.setMinimumIntegerDigits(digits);
		return formatInt.format(num);
	}

	static public String strRepeat(String s, int n) {
		String out="";
		for(int i=0; i<n; i++) out+=s;
		return s;
	}
	
	static public String strPad(String s, int len) {
		int strLen=s.length();
		if (len>strLen) {
			strLen=len-strLen;
			for (int i=0; i<strLen; i++)
				s+=' ';
		}

		return s;
	}

	static public String strPadLeft(String s, int len) {
		int strLen=s.length();
		if (len>strLen) {
			len-=strLen;
			for (int i=0; i<len; i++)
				s=' '+s;
		}

		return s;
	}

	public static SimpleDateFormat dateStrFormat;

	public static String dateStr() {
		return dateStr(Calendar.getInstance());
	}

	public static String dateStr(Calendar c) {
		if(dateStrFormat==null) dateStrFormat=new SimpleDateFormat("yyyyMMdd");
		return dateStrFormat.format(c.getTime());
	}

	public static String timeStr(long t) {
		int tmp;
		if (buf==null) buf=new StringBuffer();
		buf.setLength(0);

		long T=t/1000;
		if (T>59) {
			if (T>3600) {
				tmp=(int)T/3600;
				if (tmp<10) buf.append('0');
				buf.append(tmp).append(':');
				T-=3600*tmp;
			}
			tmp=(int)T/60;
			if (tmp<10) buf.append('0');
			buf.append(tmp).append(':');
			T-=60*tmp;
		} else if (T<60) buf.append("00:");

		if (T<10) buf.append('0');
		buf.append(T);
		return buf.toString();
	}

	public static String timeStr(Calendar c) {
		int tmp;
		if (buf==null) buf=new StringBuffer();
		buf.setLength(0);
		SimpleDateFormat sdf=new SimpleDateFormat("EEEE MMMM dd,yyyy HH:mm:ss");

		buf.append(sdf.format(c.getTime()));
		return buf.toString();
	}

	
	public static float[] parseFloat(String s[], int offs, float[] arr) {
		int len=s.length-offs;

		if (arr==null) arr=new float[len];
		else if (arr.length<len) arr=UUtil.resizeArrayFloat(arr, len);
		for (int i=0; i<len; i++) {
			if(s[i+offs].length()>0) {
				s[i+offs]=s[i+offs].replace(',', '.');
				try {
					arr[i]=Float.parseFloat(s[i+offs]);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					arr[i]=Float.NaN; 
				}				
			}
			else arr[i]=Float.NaN; 
		}
		return arr;
	}

	public static float[] parseFloat(String s[]) {
		return parseFloat(s, 0, null);
	}

	public static float parseFloat(String s) {
		return Float.parseFloat(s);
	}

	public static int parseInt(String s) {
		return Integer.parseInt(s);
	}

	public static int parseHex(String hex) {
		return 0xff000000|Integer.parseInt(hex, 16);
	}

	public static int[] parseInt(String s[]) {
		return parseInt(s, 0);
	}

	public static int[] parseInt(String s[], int offs) {
		int[] val=new int[s.length-offs];
		for (int i=0; i<s.length; i++)
			val[i]=Integer.parseInt(s[i+offs]);
		return val;
	}
	
	public static long parseLong(String string) {
		return Long.parseLong(string);
	}


	public static String repeatChar(char ch, int num) {
		char c[]=new char[num];
		for (int i=0; i<num; i++)
			c[i]=ch;
		return new String(c);
	}

	public static String removeDuplicateChar(String s, char ch) {
		int id, len;
		char curr;
		String fixed="";

		boolean isDupl=true;
		id=0;
		len=s.length();
		if (len<2) return s;

		do {
			curr=s.charAt(id++);
			if (curr==' ') {
				if (!isDupl) {
					isDupl=true;
					fixed+=curr;
				}
			} else {
				fixed+=curr;
				isDupl=false;
			}
		} while (id<len);

		return fixed;
	}

	public static String wrapText(String s, int len) {
		int pos, pos2;

		if (buf==null) buf=new StringBuffer();
		buf.setLength(0);

		buf.append(s);
		pos=0;
		pos2=0;

		while (buf.length()-pos>len&&pos<1000) {
			pos2=pos+len;
			while (pos2>pos&&!Character.isWhitespace(buf.charAt(pos2))
					&&buf.charAt(pos2)!=';'&&buf.charAt(pos2)!=',') {
				pos2--;
			}
			if (pos2==0) pos2=len-1;
			else {
				if (buf.charAt(pos2)==';'||buf.charAt(pos2)==',') pos2++;
			}

			buf.setCharAt(pos2, '\n');
			pos=pos2+1;
		}

		return buf.toString();
	}

	public static void log(List l) {
		for(Object o : l) log(o.toString());
		
	}

	public static long timestampStartOfDay(long time) {		
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		return cal.getTimeInMillis();
	}

	public static long timestampEndOfDay(long time) {		
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.SECOND,59);
		return cal.getTimeInMillis();
	}

	public static long timestamp(int year, int month, int dayOfMonth) {
		Calendar cal=Calendar.getInstance();
		cal.set(year,month,dayOfMonth,0,0);
		cal.set(Calendar.SECOND,0);
		return cal.getTimeInMillis();
	}
	
	public static long timestamp() {
		return timestampStartOfDay(System.currentTimeMillis());
	}

	public static float rnd(float max) {
		return rnd.random(max);
	}

	public static float rnd(float min, float max) {
		return rnd.random(min,max);
	}

	public static int rndInt(int max) {
		return rnd.integer(max);
	}

	public static int rndInt(int min, int max) {
		return rnd.integer(min,max);
	}

}
