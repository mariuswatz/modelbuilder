package unlekker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import processing.core.PApplet;

public class UDataText {
	private static final String DIVIDER="------------";
	public static final String ENDBLOCK="------------\tEND BLOCK";
	private int dataLinePos;
	private StringBuffer strbuf;
	private ArrayList dataBuffer;	
	public String[] str;
	public int numStr;

	public static String DELIM="\t";
	public String tokens[];
	public String parseStr;
	public static String COMMENT="# ",SPACER="\t",BLOCKSPACER="|";
	public int parseLine,currToken,numToken;
	
	public static int EOF=-1,STRING=0,DIVIDERSTR=3,ENDSTR=1,TOKENSTR=2,EMPTYSTRING=-2;
	
	public UDataText() {
		numStr=0;
		strbuf=new StringBuffer();
		parseLine=0;		
	}

	public UDataText(String s[]) {
		numStr=0;
		strbuf=new StringBuffer();
		parseLine=0;		
		add(s);
		toArray();
	}

	////////////////////////////////
	// PARSING
	
	/////////////////////////// PARSE
	
	public void beginParse() {
	  parseLine=0;	  
  }

	public void endParse() {
	  UUtil.log("endParse() - "+parseLine+"/"+numStr);	  
	  
  }

	public String getParseString() {
		parseString();
		return parseStr;
	}
	
	public void convertDelimiter(String delim) {
		for (int i=0; i<numStr; i++) str[i]=str[i].replaceAll(delim, DELIM);
	}
	
	public int parseString() {
		numToken=-1;
		if(parseLine<numStr) parseStr=str[parseLine++];
		else {
			parseStr=null;
			endParse();
			return EOF;
		}

		numToken=0;
		currToken=-1;
		
		if(parseStr.equals(DIVIDER)) return DIVIDERSTR;
		else if(parseStr.equals(ENDBLOCK)) return ENDSTR;
		else if(parseStr.indexOf(DELIM)!=-1) {
			tokens=parseStr.split(DELIM);
			numToken=tokens.length;
			currToken=0;
			return TOKENSTR;
		}
		else if(parseStr.length()>0) {
			tokens=new String[] {parseStr};
			numToken=1;
			currToken=0;
			return TOKENSTR;
		}
		if(parseStr.length()==0) return EMPTYSTRING;
		return STRING;

	}

	public String [] parseStringGetTokens() {
		String s[]=null;
		
		parseString();
		if(numToken>0) s=tokens;
		
		return s;
	}

	
	public String getString() {
		if(currToken<0 || tokens==null) return null;
		return tokens[currToken++];
	}

	public int getInt() {
		return UUtil.parseInt(tokens[currToken++]);
	}

	public long getLong() {
		return UUtil.parseLong(tokens[currToken++]);
	}

	
	public boolean getBool() {
		String bool=tokens[currToken++];
		if(bool.equals("true")) return true;
		return false;
	}

	public float getFloat() {
		return UUtil.parseFloat(tokens[currToken++]);
	}

	public boolean parseTokenString() {
		parseStr=str[parseLine++];
		if(parseStr==null) return false;
		
		tokens=parseStr.split(DELIM);
		numToken=tokens.length;
		currToken=0;
		
		return true;
	}

	public void parseSkipLine(int n) {
		parseLine+=n;
	}

	public void parseSkipLine() {
		parseLine++;
	}

	public String parseGetLine() {
		parseString();
		return parseStr; 
	}

	////////////////////////////////
	// DATA SAVING

	public UDataText addLn(String s) {
		if(dataBuffer==null) dataBuffer=new ArrayList();
		dataBuffer.add(s);
		strbuf.setLength(0);
		dataLinePos=0;
		return this;
	}
	
	public UDataText endLn() {
		if(dataLinePos>0)
			return addLn(strbuf.toString());
		return this;
	}

	/**
	 * Returns the current line that's being built and 
	 * erases it.
	 * @return
	 */
	public String popCurrentLine() {
		String s=strbuf.toString();
		strbuf.setLength(0);
		dataLinePos=0;

		return s;
	}

	public UDataText add(String s) {
		if(dataLinePos>0) strbuf.append(SPACER+s);
		else strbuf.append(s);
		dataLinePos++;
		return this;
	}

	public UDataText add(String s[]) {
		for(int i=0; i<s.length; i++) {
			if(s[i]!=null) addLn(s[i]);
		}
		return this;
	}

	public UDataText add(boolean b) {
		if(dataLinePos>0) strbuf.append(SPACER+b);
		else strbuf.append(""+b);
		dataLinePos++;
		return this;
	}

	public UDataText add(int i) {
		if(dataLinePos>0) strbuf.append(SPACER+i);
		else strbuf.append(""+i);
		dataLinePos++;
		return this;
	}

	public UDataText add(float f,int decimalPrec) {
		String fs=UUtil.nf(f,1,decimalPrec);
		if(dataLinePos>0) strbuf.append(SPACER+fs);
		else strbuf.append(""+fs);
		dataLinePos++;
		return this;
	}
	
	public UDataText add(float f[]) {
		add(UUtil.toString(f));
		return this;
	}

	public UDataText add(int i[]) {
		return add(UUtil.toString(i));
	}

	public UDataText add(Object o[]) {
		return add(UUtil.toString(o));
	}
	
	public UDataText add(Object o) {
		return add(o.toString());
	}



	public UDataText add(float f) {
		if(dataLinePos>0) strbuf.append(SPACER+f);
		else strbuf.append(""+f);
		dataLinePos++;
		return this;
	}

	public String [] toArray() {
		numStr=dataBuffer.size();
		str=(String [])dataBuffer.toArray(new String[numStr]);
//		for(int i=0; i<numStr; i++) str[i]=(String)dataBuffer.get(i);
		
		return str;
	}

	///////////////////////////////////
	// FILE OPERATIONS
	
	public File f;
	public String filename,shortname,path;
	
	private PrintWriter outWriter;
	private Writer outStream;
	private BufferedReader inReader;
	private InputStream inStream;
	
	public boolean silent=false;
	private boolean doAppend;	

	public void setFileOptions(String _name,boolean _append) {
		filename=_name;
		shortname=UIO.noPath(filename);
		path=UIO.getPath(filename);
		doAppend=false;
	}

	public void save() {
		save(filename,false);
	}

	public void save(String _filename) {
		save(_filename,false);
	}
	
	public void save(String _filename,boolean _append) {
		setFileOptions(_filename, _append);
	
		try {
			outStream=new OutputStreamWriter(
					UIO.getOutputStream(filename,doAppend));		
			outWriter=new PrintWriter(outStream);
			
			toArray();
			
			for (int i=0; i<numStr; i++) outWriter.println(str[i]);
			outWriter.flush();
			outStream.close();			
		  if(!silent) UUtil.log("Saved '"+filename+"' "+numStr);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("DataText.save() failed: "+e.getMessage());
		}
	  catch (Exception e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
	  }
	  
	}

	public UDataText load(String _filename) {
  	String ln;
		try {
			setFileOptions(_filename, false);			
			inStream=UIO.getInputStream(filename);
			inReader=new BufferedReader(new InputStreamReader(inStream));

			do {
				ln=inReader.readLine();
				if(ln!=null) addLn(ln);
			} while (ln!=null);

			toArray();			
			if(!silent) UUtil.log("DataText.load('"+UIO.noPath(filename)+":')': "+numStr+" lines read.");
			inStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public static UDataText loadFile(String filename) {	
		UDataText txt=new UDataText();		
		return txt.load(filename);
	}

	public UDataText addDivider() {
		addLn(DIVIDER);
		return this;
	}
	
	public UDataText addDivider(String s) {
		add(DIVIDER).add(s).endLn();
		return this;
	}

	public UDataText endBlock() {
		return 	addLn(ENDBLOCK);
	}

	public int[] getIntArray() {
		String intStr[]=getStringAsTokens(",");		
		UUtil.log("getIntArray - "+UUtil.toString(intStr)+" "+numToken+" "+currToken);
		int i[]=UUtil.parseInt(intStr);
		UUtil.log("getFloatArray - Return: "+UUtil.toString(i));
	  return i;
  }

	public float[] getFloatArray() {
		String arr=UUtil.chopBraces(tokens[currToken++]);
		String floatStr[]=PApplet.split(arr, ',');
		UUtil.log("getFloatArray - "+arr);
		UUtil.log("getFloatArray - "+UUtil.toString(floatStr));
		float f[]=UUtil.parseFloat(floatStr);
		UUtil.log("getFloatArray - Return: "+UUtil.toString(f));
	  return f;
  }

	public String [] getStringAsTokens(String in,String delim) {
		String arr=UUtil.chopBraces(in);
		String str[]=PApplet.split(arr, delim);
	  return str;
  }

	public String [] getStringAsTokens(String delim) {
		String arr=UUtil.chopBraces(tokens[currToken++]);
		String str[]=PApplet.split(arr, delim);
	  return str;
  }

	public void setTokens(String[] stringAsTokens) {
		numToken=tokens.length;
		currToken=0;
		tokens=stringAsTokens;
	}

}
