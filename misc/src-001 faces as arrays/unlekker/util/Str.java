package unlekker.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import processing.core.*;

/**
 * Utility class for manipulating and generating text.
 * 
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */

public class Str {
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
		if (strBufCnt==strBuf.length) strBuf=(String[])Util.expandArray(strBuf);
		strBuf[strBufCnt++]=s;
	}

	public static void addToStringBuffer(String s[]) {
		if (strBufCnt+s.length>=strBuf.length)
			strBuf=(String[])Util.resizeArray(strBuf, s.length+strBuf.length);
		System.arraycopy(s, 0, strBuf, strBufCnt, s.length);
		strBufCnt+=s.length;
	}

	public static String[] endStringBuffer() {
		String s[]=null;
		if (strBufCnt>0) s=(String[])Util.resizeArray(strBuf, strBufCnt);
		strBuf=null;

		return s;
	}

	/**
	 * Shortens the string "s" to a length of "len" characters.
	 */
	public static String shorten(String s, int len) {
		if (s==null) return null;
		if (s.length()>len) s=s.substring(0, len-3)+"...";
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
		list=(String[])Util.resizeArray(list, sz+tmp.length);
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
	

	public static String chopBraces(String s) {
		return s.substring(1,s.length()-1);		
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
	
	public static String toString(Object[] o) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<o.length; i++) {
			buf.append(o[i].toString());
			if (i<o.length-1) buf.append(',');
		}
		buf.append(']');
		return buf.toString();
	}

	public static String toString(Object[] o,String delim) {
		StringBuffer buf=new StringBuffer();
		buf.append('[');
		for (int i=0; i<o.length; i++) {
			buf.append(o[i].toString());
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

	/**
	 * Returns a string containing the floating point number "num", shortened to
	 * maximum 3 decimal points.
	 */
	public static String numStr(float num) {
		return nf(num, 1, 3);
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
			strLen-=len;
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
		else if (arr.length<len) arr=Util.resizeArrayFloat(arr, len);
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

}