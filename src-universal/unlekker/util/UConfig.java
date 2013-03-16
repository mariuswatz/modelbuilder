package unlekker.util;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;

public class UConfig extends Properties {
  public String path;

  public UConfig() {

  }

  public UConfig(String filename) {
    init(filename);
  }

  public void init(String filename) {
    FileInputStream in;

    try {
      File f=new File(filename);
      if(!f.exists()) {
      	filename="conf/"+filename;
      	f=new File(filename);
      }
      f=f.getCanonicalFile();
      path=f.getParent();
//      System.out.println("path "+path+" filename "+filename);
      in=new FileInputStream(f);
      load(in);
//      listProperties();
    } catch (Exception ex) {
      System.err.println("Error reading properties: "+filename);
    }
  }

  public boolean getBoolean(String id, boolean defState) {
    String tmp=getProperty(id,""+defState).toLowerCase().trim();
    if(tmp.compareTo("true")==0) return true;
    return false;
  }

  public int getInt(String id, int defVal) {
  	int tmp;
		try {
			tmp=Integer.parseInt(getProperty(id,""+defVal));
		} catch (NumberFormatException e) {
			System.out.println(id+": "+e.toString());
			tmp=defVal;
		}
    return tmp;
  }

  public float getFloat(String id, float defVal) {
    return Float.parseFloat(getProperty(id,""+defVal));
  }

  public void listProperties() {
    for (java.util.Enumeration e=propertyNames(); e.hasMoreElements(); ) {
      String id=""+e.nextElement();
      System.out.println(id+" = "+getProperty(id));
    }
  }
}
