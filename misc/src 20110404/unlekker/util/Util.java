package unlekker.util;

import java.io.*;

import java.lang.*;
import java.lang.reflect.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Set of static utility functions. 
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */


public class Util implements UConstants {
	private static Runtime runtime;
	private final static String NULLSTR="NULL",EQUALSTR=" = ",COMMASTR=", ";
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
    rnd=new Rnd();
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
    shapeTypes[LINE]="LINE";
    shapeTypes[LINES]="LINES";
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
  public static Rnd rnd;
  
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
  public static boolean doLog=true;
  public static long logStart;
  
	private static String logFilename;
	private static Logger logger;
	private static FileInputStream logStream;
	private static FileHandler logHandler;
	public static LogUtil logUtil;
  
  public static void logStyle(int style) {
  	logStyle=style;
  }
  
  /**
   * logPrint() prints the String s to System.out with a timestamp indicating the hour and minute
   * it was printed.
   * @param s String to be printed.
   */
  public static void logPrint(String s) {
  	if(!doLog) return;
  	
  	String tstr=null;
  	if(logStyle==LOGSINCESTART) 
  		tstr=Str.timeStr(System.currentTimeMillis()-logStart);
  	else {
    	cal.setTimeInMillis(System.currentTimeMillis());
    	tstr=Str.nf(cal.get(cal.HOUR_OF_DAY),2) +
    		":"+Str.nf(cal.get(cal.MINUTE),2);
  	}

  	System.out.println(tstr+" "+s);
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
  	for(int i=0; i<s.length; i++) logPrint(i+"/"+s.length+":"+s[i]);
  }

  public static void log(float [] f) {
  	if(f==null) logPrint("log: Float array == null.");
  	for(int i=0; i<f.length; i++) logPrint(i+":"+Str.nf(f[i]));
  }
  
  public static void log(int [] val) {
  	if(val==null) logPrint("log: Int array == null.");
  	for(int i=0; i<val.length; i++) logPrint(i+": "+val[i]);
  }
  
//  public static void log(String [] s,int min,int max) {
//  	if(s==null) logPrint("log: String array == null.");
//  	min=Data.max(0,min);
//  	max=Data.min(max,s.length);
//  	for(int i=min; i<max; i++) logPrint(i+":"+s[i]);
//  }

  public static void logDivider(String s) {
  	logPrint("\n----------------------------------------------------------------------");
  	logPrint(s);
  }

  public static void logDivider() {
  	logPrint("\n----------------------------------------------------------------------");
  }

  public static void logErr(String s) {
  	logPrint(s);
  }

  public static void logErrDivider(String s) {
  	logErr("\n----------------------------------------------------------------------");
  	logErr(s);
  }
  
  	
  /**
   * logPrintErr() prints the String s to System.err with a timestamp indicating the hour and minute
   * it was printed.
   * @param s String to be printed.
   */
  public static void logErrPrint(String s) {
  	cal.setTimeInMillis(System.currentTimeMillis());
  	System.err.println(
  			Str.nf(cal.get(cal.HOUR_OF_DAY),2) +
  			":"+Str.nf(cal.get(cal.MINUTE),2)+"|  "+
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
  	if(logger==null) try {
  		Calendar cal=Calendar.getInstance();
  		
    	logFilename=filename;   	
//    	java.util.logging.ConsoleHandler.level=NONE;
    	logger=Logger.getLogger("Util");
    	logger.setUseParentHandlers(false);
    	
    	logUtil=new LogUtil();
    	logger.setFilter(logUtil);
//    	logutil=new LogUtil(logger);

    	// Erase log once a week
    	File f=new File(logFilename);
    	if(f.exists()) {
    		Calendar mod=Calendar.getInstance();
    		mod.setTimeInMillis(f.lastModified());
    		log("Modified: "+Str.timeStr(mod));
    		int diff=(int)(
    			(System.currentTimeMillis()-mod.getTimeInMillis())/
    			(24*60*60*1000));
    		log("Diff: "+(diff)+" days.");
    		if(diff>=7) {
    			log("Log file older than 1 week. Deleting.");
    			logHandler=new FileHandler(logFilename);
    		}
    	}
    	if(logHandler==null) logHandler=new FileHandler(logFilename,true);
    	
    	logger.addHandler(logHandler);
    	logHandler.setFormatter(logUtil);
    	
//			logDivider("Added log file output: '"+logFilename+"'");
//    	log("Log started: "+Str.timeStr(cal));
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
  	String s="Mem: "+Str.nf((float)memoryFree()/MB,0,1)+" free "+
  		"| "+Str.nf((float)memoryTotal()/MB,0,1)+" total | "+
  		Str.nf((float)memoryMax()/MB,0,1)+" max.";
  	return s;
  }

  public static void runtimeGarbageCollector() {
  	long mem=memoryUsed();
  	runtime.runFinalization();
  	runtime.gc();
  	float diff=(float)(memoryUsed()-mem)/(1024f*1024f);
//  	Util.log("\nForcing Java garbage collection... "+
//  			Str.nf(diff,0,1)+" MB difference.");
  }
  
  public static String[] runtimeExec(String cmd,boolean wait) {
  	String s[]=null;
  	int n;
  	
	  try  { 
	    Process p=runtime.getRuntime().exec(cmd); 
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
	    
	    s=Str.stripNullStrings(s);
	  } 
	  catch(Exception e1) {
	    logErrPrint("execCommand() failed: "+e1.toString());
	    s=null;
	  } 
  	return s;
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
					if(ctmp.equals(types[0])) buf.append(Str.nf(f[idx].getFloat(o)));
					else if(ctmp.equals(types[1])) buf.append(f[idx].getInt(o));
					else if(ctmp.equals(types[2])) buf.append(Str.nf(f[idx].getDouble(o)));
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

			return Str.wrapText(buf.toString(),70);

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
	static public float[] expandArrayFloat(float[] f) {
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
	static public float[][] expandArrayFloat(float[][] f) {
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

}
