package unlekker.util;

import java.util.Calendar;
import java.util.logging.*;

public class LogUtil 
	extends Formatter 
	implements Filter {

	public String[] log;
	public static int logBufferSize=100;
	public int logN;
	              
	public String format(LogRecord record) {
		
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuffer sb = new StringBuffer();
		
  	String tstr=null;
  	if(Util.logStyle==Util.LOGSINCESTART) 
  		tstr=Str.timeStr(System.currentTimeMillis()-Util.logStart)+" ";
  	else {
  		Calendar cal=Util.cal;
    	cal.setTimeInMillis(System.currentTimeMillis());
    	tstr=Str.nf(cal.get(cal.HOUR_OF_DAY),2) +
    		":"+Str.nf(cal.get(cal.MINUTE),2)+" ";
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
