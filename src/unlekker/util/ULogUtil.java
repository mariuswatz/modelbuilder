package unlekker.util;

import java.util.Calendar;
import java.util.logging.*;

public class ULogUtil 
	extends Formatter 
	implements Filter {

	public int logStyle=UUtil.LOGCURRTIME;
	private final static String DIVIDER=
			"----------------------------------------------------------------------";
	
	public String[] log;
	public static int logBufferSize=100;
	public int logN;
	
	private static Logger logger;
	private boolean doPrint;
	
	public void startLog(boolean doPrint) {
		if(logger==null) {
			this.doPrint=doPrint;
			logger=Logger.getLogger(this.toString());
    	logger.setUseParentHandlers(false);    	
    	logger.setFilter(this);
		}
	}
	
	public void log(String s) {
		if(doPrint) UUtil.log(s);
		logger.info(s);
	}

  public void logDivider(String s) {
  	log(DIVIDER);
  	log(s);
  }

  public void logDivider() {
  	log(DIVIDER);
  }

	
	public String format(LogRecord record) {
		
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuffer sb = new StringBuffer();
		
  	String tstr=null;
  	if(logStyle==UUtil.LOGSINCESTART) 
  		tstr=UUtil.timeStr(System.currentTimeMillis()-UUtil.logStart)+" ";
  	else {
  		Calendar cal=UUtil.cal;
    	cal.setTimeInMillis(System.currentTimeMillis());
    	tstr=UUtil.nf(cal.get(cal.HOUR_OF_DAY),2) +
    		":"+UUtil.nf(cal.get(cal.MINUTE),2)+" ";
  	}

  	// Get the date from the LogRecord and add it to the buffer
		sb.append(tstr);
		
		// Get the formatted message (includes localization 
		// and substitution of paramters) and add it to the buffer
		sb.append(formatMessage(record));
		sb.append("\n");

		return sb.toString();
	}

	public boolean isLoggable(LogRecord record) {
		if(log==null) log=new String[logBufferSize];
		else System.arraycopy(log, 0, log, 1, log.length-1);
		log[0]=format(record);
		if(logN<logBufferSize) logN++;
		
//		System.out.println(format(record));
		return true;
	}
}
