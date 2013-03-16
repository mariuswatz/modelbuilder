package unlekker.util;

import java.io.*;

import java.awt.*;

import javax.swing.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.regex.*;
import com.sun.image.codec.jpeg.*;

import java.awt.image.*;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import processing.core.*;

/**
 * Utility functions related to file IO, such as producing auto-incrementing filename sequences and listing
 * directories.
 *
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 *
 */
public class UIO implements Serializable, PConstants {
	private static final int MB=1024*1024;
	public static String currentDir;
	public static UIO ioInstance;
	public static boolean useGZIP=false; 

  public static final char COMMACHAR=',',TABCHAR='\t',DIRCHAR='/',DIRCHARDEFAULT='/';
  public static final String COMMASTR=",",TABSTR="\t",DIRSTR=""+File.separatorChar;

  public static final String EXT[] = {"pdf","png","rar","zip","txt",
  	"gif","jpg","java","doc","xls"};
  public static final String NOEXT="NoExt";
  public static final int UNKNOWN=-1,PDF=0,PNG=1,RAR=2,ZIP=3,TXT=4,GIF=5,JPG=6,
  	JAVA=7,DOC=8,XLS=9,DIRECTORY=10;

  private static MultiFilter filter;
  private static String inFilename,inFilenameNoPath;
  private static BufferedReader inReader;
  private static BufferedWriter outWriter;
  private static ObjectInputStream inObj;
  private static OutputStream out;
  private static ObjectOutputStream outObj;
  private static int inCnt,outCnt;
  private static long ioStartTime;
  public static int debugLevel=4;
  
  
  public void init() {
  	UIO.getCurrentDir();
  }
  
  public static void useCompression(boolean _useGZIP) {
  	useGZIP=_useGZIP;
  }
  
  public static UIO getInstance() {
  	if(ioInstance==null) ioInstance=new UIO();
  	return ioInstance;
  }
  
  public static void mkdir(String name) {
    java.io.File f=new java.io.File(name);
    f.mkdir();
  }

  /**
   * Deletes the file indicated by "filename" permanently from the disk.
   * Use at your own peril.
   * @param name Name of file to delete
   * @return Returns true if successful, false if not
   */
  public static boolean deleteFile(String name) {
  	boolean ok=new java.io.File(name).delete();
    return ok;
  }

  /**
   * Checks for the existence of a file or directory on the
   * local disk.
   * @param filename
   * @return True if the file indicated by filename exists,
   * false otherwise.
   */
	public static boolean fileExists(String filename) {
		return new File(filename).exists();
	}

	/**
	 *
	 * @param p Reference to a valid PApplet instance
	 * @param path
	 * @return
	 */
  // File chooser borrowed from
  // http://www.processing.org/hacks/doku.php?id=hacks:filechooser
  public static String getFilenameChooserDialog(PApplet p,String path) {
  	String s=null;


		// set system look and feel
		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			UUtil.log("Start");
			FileDialog fd = new FileDialog( 
			    (Frame)p.frame, "Open", 
			    FileDialog.LOAD);
			fd.setDirectory(path);

			fd.setVisible(true);
			s=fd.getFile();
			if(s!=null) {
				UUtil.log("got "+s);
				return fd.getDirectory()+DIRCHAR+s;
			}

//			System.out.println("File dialog: "+fs.toString());
//			Util.log("Save file = " + filedialog.getFile());
//			Util.log("Save directory = " + filedialog.getDirectory()); 

//			// create a file chooser
//			JFileChooser fc=new JFileChooser(path);
//			fc.setFileSelectionMode(fc.DIRECTORIES_ONLY);
//			// in response to a button click:
//			int returnVal=fc.showOpenDialog(p);
//
//			if (returnVal==JFileChooser.APPROVE_OPTION) {
//				File file=fc.getSelectedFile();
//				s=file.getCanonicalPath();
//				UUtil.log("getFilenameChooserDialog returned: "+s);
//			} else {
//				UUtil.logErr("Open command cancelled by user.");
//			}
			
			
		} catch (Exception e) {
			UUtil.logErr("Exception: "+e.toString());
			e.printStackTrace();
			s=null;
		}

		return s;
  }



  /**
   * Uses Runtime.getRuntime().exec to try to find the disk volume name
   * @param path Path to search from
   */
	public static String getDiskVolumeName(String path) {
		String volname="Unknown";

		try {
			String check="Volume in drive "+path.charAt(0)+" is ";
			String res[]=UUtil.runtimeExec("cmd /c dir "+path, false);
			if(res!=null) {
				for(int i=0; i<res.length; i++) {
					if(res[i].indexOf(check)!=-1)
			  		volname=res[i].substring(res[i].indexOf(check)+check.length());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return volname;
	}

  /**
   * Finds the canonical path of the current directory.
   * @return Name of current directoy
   */
  public static String getCurrentDir() {
    try {
    	currentDir=new File (".").getCanonicalPath();
    	currentDir=currentDir.replace('\\',UIO.DIRCHAR);
    }
    catch(Exception e) {
      e.printStackTrace();
      currentDir=null;
    }
    return currentDir;
  }

  public static String savePath(String name) {
  	name=getAbsolutePath(name);
  	File f=new File(name);
  	String parent=f.getParent();
  	if(parent!=null) new File(parent).mkdirs();
  	
  	return name;
  }
  
  /**
   * Finds the canonical path of the current directory.
   * @return Name of current directoy
   */
  public static String getAbsolutePath(String name) {
  	File f=new File(name);  	
  	if(f.isAbsolute()) return name;
//  	else Util.log("Not absolute path: "+name);

  	if(currentDir==null) currentDir=getCurrentDir();
  	String s=currentDir+DIRCHAR;
  	if(name.startsWith("data")) s+=name;
  	else  s+="data"+DIRCHAR+name;
  	if((s.endsWith("/") || s.endsWith(DIRSTR))) {
  		s+="/";
  	}
//  	if(debugLevel>2) Util.log("IO.getAbsolutePath "+s);
  	return s;
  }

  /**
   * Returns the filename minus extension
   * @param name
   * @return Filename without extension
   */

  public static String noExt(String name) {
  	int pos;
  	
//  	pos=name.lastIndexOf(DIRCHAR);
//  	if(pos!=-1) name=name.substring(pos+1);
  	pos=name.lastIndexOf(".");
  	if(pos==-1) return name;
  	return name.substring(0,pos);
  }

  public static String getPath(String name) {
  	int pos;
  	
  	name=name.replace('\\',DIRCHAR);
  	pos=name.lastIndexOf(DIRCHAR);
  	if(pos==-1) return getCurrentDir();
  	else return name.substring(0,pos);
  }
  
  /**
   * Returns only the filename part of a path.
   * @param name
   * @return Filename without path
   */

  public static String noPath(String name) {
  	int pos=name.lastIndexOf('\\');
  	int pos2=name.lastIndexOf('/');
  	if(pos<0 || (pos2>-1 && pos2<pos)) pos=pos2;
//  	UUtil.log(pos+" "+name.length()+" "+name);

  	return name.substring(pos+1);
  }

  public static String getExt(String name) {
  	String ext=NOEXT;
  	int pos=name.lastIndexOf('.');

  	if(pos>0) ext=name.substring(pos+1);
  	return ext;
  }

  /**
   * Returns File object with absolute path. Will try to fix a non-absolute by
   * applying dataPath() to the filename.
   *
   * @param filename
   * @return File object with absolute path
   */

  public static File getFile(String filename) {
    File file = new File(filename);
    if (!file.isAbsolute()) {
    	file = new File(getAbsolutePath(filename));
    	if (!file.isAbsolute()) file=null;
    }

    if (file == null) {
      throw new RuntimeException("Filename '"+filename+"' invalid. An absolute path " +
                                 "is needed.");
    }
  	return file;
  }
  
  /**
   * Gets a list of all files in a given directory. Results may be filtered.
   *
   * @param path Directory to list
   * @return List of filenames or null if no files were found.
   */

  public static String [] listFiles(String _path) {
  	String [] list=null;
  	
  	try {
  		_path=UIO.getAbsolutePath(_path);
//  		if(debugLevel>2) Util.log("listFiles() "+_path);
  		File f=new File(_path);
  		
			list=f.list(filter);
//			if(list!=null) java.util.Arrays.sort(list);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
  }

  public static String [] listFilesByDate(String _path) {
  	String list[]=listFiles(_path);
  	File f[]=new File[list.length];
  	for(int i=0; i<f.length; i++) f[i]=new File(_path+list[i]);

  	Arrays.sort(f, new Comparator<File>(){
  	    public int compare(File f1, File f2)
  	    {
  	        return (int)(f1.lastModified()-f2.lastModified());
  	    } });
  	
  	for(int i=0; i<f.length; i++) list[i]=f[i].getName();
		return list;
  }

	public static void filterReset() {
		if(filter==null) filter=UIO.getInstance().new MultiFilter();
		filter.reset();
	}
	
  public static void filterExtension(String _ext[]) {
		if(filter==null) filter=UIO.getInstance().new MultiFilter();
		filter.filterExtension(_ext);
  }

  public static void filterExtension(String _ext) {
		if(filter==null) filter=UIO.getInstance().new MultiFilter();
		String [] tmp={_ext};
		filter.filterExtension(tmp);
  }

  public static void filterPattern(String _pat) {
		if(filter==null) filter=UIO.getInstance().new MultiFilter();
		filter.filterPattern(_pat);
  }


  /**
   * Returns the size of a given file.
   * @param name The name of the file to get information about.
   * @return Returns file size in kilobytes (KB)
   */
  public static float getFileSize(String name) {
    File f=getFile(name);
    if(f.exists()) return (float)f.length()/1024f;

    return 0;
  }


  /**
   * Returns a String representation of a given file size
   * @param len The file size
   * @return Returns file size as a string representation, either in kilobytes
   * (kB)or Megabytes depending on the file size.
   */
  public static String getFileSizeString(float len) {
  	String s;
    float kb=(float)len;
    if(kb<1) return UUtil.nf(kb,1,1)+" b";
    else if(kb<1024) return UUtil.nf(kb,0,0)+" b";
    else if(kb<MB) return UUtil.nf(kb/1024f,0,1)+" kb";

    return UUtil.nf(kb/MB,0,1)+" MB";
  }

  /**
   * Returns a String representation of the file size for given file
   * @param name The name of the file to get information about.
   * @return Returns file size as a string representation, either in kilobytes
   * (kB)or Megabytes depending on the file size.
   */
  public static String getFileSizeString(String name) {
  	return getFileSizeString(getFileSize(name));
  }

  /**
   * Get prefix of a String containing a sequence code, i.e. "Simplename ####.png".
   * @param s String with sequence code.
   * @return String containing the prefix
   */
  public static String getPrefixFromSequence(String s) {
    String prefix=null;

    int first=s.indexOf('#');
    prefix=s.substring(0,first);

    return prefix;
  }

  /**
   * Get suffix of a String containing a sequence code, i.e. "Simplename ####.png".
   * @param s String with sequence code.
   * @return String containing the suffix
   */
  public static String getSuffixFromSequence(String s) {
    String suffix=null;
    int last=s.lastIndexOf('#');
    suffix=s.substring(last+1);

    return suffix;
  }

  /**
   * Get padding size from a String containing a sequence code, i.e. "Simplename ####.png".
   * @param s String with sequence code.
   * @return Number of characters to pad
   */
  public static int getPadCountFromSequence(String s) {
    int first,last,count;
    first=s.indexOf('#');
    last=s.lastIndexOf('#');
    count=last-first+1;

    return count;
  }

  /**
   * Function for creating auto-incremented directories with a name format specified by
   * the nameFormat parameter, which should be a String looking like the following: "SimpleName ####".
   * The name becomes the prefix, while the hashes are turned into padded number strings. This
   * function will try to find the next correct name, and then create the directory.
   * @param nameFormat
   * @return
   */
  public static String getIncrementalFolder(String nameFormat) {
    String s=null,prefix,path;
    int padCnt;
    File f,parent;

    try {
			path=getPath(nameFormat);
			nameFormat=noPath(nameFormat);
			padCnt=getPadCountFromSequence(nameFormat);
			prefix=getPrefixFromSequence(nameFormat);

			if(currentDir==null) getCurrentDir();

//			FileFilter filter=new FileFilter();
//			filter.filterPrefix(prefix);
//			filter.filterDirectoriesOnly();
			String [] list=listFiles(path);

			int last=-1;
			if(list!=null)
				try {
					{
					  String numstr=list[list.length-1].substring(prefix.length(),prefix.length()+padCnt);
					  last=Integer.parseInt(numstr);
					}
				} catch (RuntimeException e) {
					last=-1;
//					println("getIncrementalFolder - "+e.toString());
				}

			last++;
			s=path+DIRCHAR+prefix+UUtil.nf( last, padCnt);
			//println("Got new folder: '"+s+"' "+padCnt);

			f=new File(s);
			parent=f.getParentFile();
			if(parent!=null) parent.mkdirs();
			f.mkdirs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    return s;
  }


  public static String getIncrementalFilename(String nameFormat, String path) {
		int pos1,pos2,digits,high;
  	String s=null,numstr,last,list[];
  	String pre,post;
  	  	
  	nameFormat=nameFormat.replace('\\', '/');
//  	nameFormat=UIO.noExt(nameFormat);
  	if(nameFormat.indexOf("#")==-1) nameFormat+=" ###";
  	
  	filterReset();
  	filterPattern(nameFormat);
  	
  	if(path==null) path=currentDir+DIRCHAR+"data";
		list=listFiles(path);
		
		pos1=nameFormat.indexOf("#");
		pos2=nameFormat.lastIndexOf("#");
		digits=pos2-pos1+1;
		pre=nameFormat.substring(0,pos1);
		post=nameFormat.substring(pos1+digits);
//		if(debugLevel>-1) 
			UUtil.log(path+" '"+pre+"' '"+post+"' "+digits+
			    (list==null || list.length==0 ? "list=null" : list[0]));
		
//  	Util.log("incr '"+nameFormat+"' "+pos1+"-"+pos2+", "+(pos2-pos1)+" digits. "+path);

		// empty directory? just return
		if(list==null || list.length==0) {
		  UUtil.log("Warning: Path empty or not found '"+path+"'");
			s=path+DIRCHAR+pre+UUtil.nf((int)0,digits)+post;			
			return s;
		}
		
//		if(debugLevel>2) Util.log(list);
		last=list[list.length-1];
		pos2++;
		if(debugLevel>2) UUtil.log(nameFormat+"|"+last);		
//		if(pos2>last.length()) pos2=last.length();
		
		numstr=last.substring(pos1,pos2);;
		high=Integer.parseInt(numstr)+1;
		
		s=nameFormat.substring(0,pos1)+UUtil.nf(high,digits);
//		if(nameFormat.length()>pos2+1) s+=nameFormat.substring(pos2+1);
		s=path+DIRCHAR+s+post;
//		if(debugLevel>0) Util.log("New filename: '"+s+"'");
		
  	return s;
  }
  
/*  public static void saveJPEG(PImage img,String fname, float quality) {
    try {
    	img.loadPixels();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
      BufferedImage bimg = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
      bimg.setRGB(0,0,img.width,img.height,img.pixels,0,img.width);

   // set jpeg quality ... with baseline optimization
      JPEGEncodeParam p = encoder.getDefaultJPEGEncodeParam(bimg);
      p.setQuality(quality,true);
      encoder.setJPEGEncodeParam(p);

      encoder.encode(bimg);
      byte [] a = out.toByteArray();
      PApplet.saveBytes(new File(fname),a);
      encoder=null;
      a=null;
    }
    catch (Exception e) {
      System.err.println("SaveJpg: "+e.toString());
    }
  }
*/
  /**
   * Writes a 2-dimensional array of floats to a CSV file
   * @param filename Name of output file
   * @param val Array containing data to be written
   * @param separator Character to use as separator
   */
  public void writeCSV(String filename,float val[][],char separator) {
  	int rows,cols;

  	try {
      UUtil.log("\nPreparing data for output.");

      cols=val[0].length;
      rows=val.length;
      String str[]=new String[rows];
      StringBuffer buf=new StringBuffer(500);

      String tmp=""+rows;
      int cnt=tmp.length();

      for (int i=0; i<rows; i++) {
        buf.setLength(0);
        for (int j=0; j<cols; j++) {
          buf.append(UUtil.nf(val[i][j], 0, 5));
          if (j<511) buf.append(separator);
        }
        if (i%250==0||i==rows-1)
        	UUtil.log("Data frame "+UUtil.nf(i, cnt)+"/"+UUtil.nf(rows, cnt));
        str[i]=buf.toString();
      }

//      filename=dataPath(filename);
      if(!filename.endsWith(".csv")) filename+=".csv";
      PrintWriter writer=new PrintWriter(new FileOutputStream(filename));
      for (int i=0; i<str.length; i++) writer.println(str[i]);
      writer.flush();
      writer.close();
  	}
  	catch (Exception e) {
    // TODO Auto-generated catch block
  		e.printStackTrace();
  		return;
  	}

  	UUtil.log("\nSaved FFT data to '"+noPath(filename)+"'");
  }

  // read FFT data from a file output by a previous analysis
  public float[][] readCSV(String filename,char separator) {
    String str[], tokens[];
    float data[]=null,val[][]=null;
  	int rows=0,cols=0,rcnt=0;

    try {
      StringBuffer contents = new StringBuffer();
      BufferedReader input =
        new BufferedReader(new FileReader(filename));
      String line = null; //not declared within while loop
      String EOF=System.getProperty("line.separator");
      while (( line = input.readLine()) != null){
        contents.append(line);
        contents.append(EOF);
      }
      input.close();

      str=PApplet.split(contents.toString(), EOF);
      rows=str.length;

      println("Reading FFT data from '"+noPath(filename));

      rcnt=0;
      for (int i=0; i<rows; i++) try {
      	tokens=PApplet.split(str[i], separator);
        if (i==0) {
        	data=new float[tokens.length];
          cols=data.length;
          val=new float[rows][cols];
        }
//      	Util.log(i+" "+tokens.length+" "+cols);

        for (int j=0; j<cols; j++)
        	val[rcnt][j]=Float.parseFloat(tokens[j]);
//          data[j]=Float.parseFloat(tokens[j]);

//        if (i%250==0||i==rows-1)
//        	println("Line "+UUtil.nf(i, 5)+"/"+UUtil.nf(rows, 5));
        rcnt++;
      } catch (Exception e) {
      	UUtil.log("Invalid row: "+e.toString());
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      val=null;
    }
    
    if(rcnt!=rows) {
    	float [][] tmp=new float[rcnt][];
    	for(int i=0; i<rcnt; i++) tmp[i]=val[i];
    	val=tmp;
    }

    println(rows+" lines read, "+rcnt+" valid, "+cols+" colums.");
    return val;
  }
  
  public static InputStream getInputStream(String filename) {
  	InputStream in=null;

  	try {
			filename=getAbsolutePath(filename);		
			if(filename.endsWith("gz")) 
				in=new GZIPInputStream(new FileInputStream(filename));
			else in=new FileInputStream(filename);
		} catch (Exception e) {			
			UUtil.logErr("getOutputStream - "+e.toString());
		}
		return in;
  }

  public static OutputStream getOutputStream(String filename,boolean append) {
  	out=null;

  	try {
			filename=getAbsolutePath(filename);		
			new File(getPath(filename)).mkdirs();
			if(useGZIP || filename.endsWith("gz")) {
				if(!filename.endsWith("gz")) filename+=".gz";
				out=new GZIPOutputStream(new FileOutputStream(filename));
			}
			else out=new FileOutputStream(filename,append);
		} catch (Exception e) {		
			e.printStackTrace();
			UUtil.logErr("getOutputStream - "+e.toString());
		}
		return out;
  }

	public static OutputStream getOutputStream(String filename) {
		return getOutputStream(filename,false);
	}

  public static void saveStrings(String filename, String strings[]) {
    try {
    	getOutputStream(filename);
			PrintWriter writer=new PrintWriter(new OutputStreamWriter(out));
			for (int i=0; i<strings.length; i++)
				if (strings[i]!=null) writer.println(strings[i]);

			writer.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("saveStrings failed: "+e.getMessage());
		}
  }

  public static void getFilename(String filename) {
  	inFilename=UIO.getAbsolutePath(filename);
  	inFilenameNoPath=noPath(inFilename);  	
  }
  
  ///////////////////////////////////////////////////////////////////
  // LOADING + SAVING OF SERIALIZED DATA
  
  public static Object[] loadObjects(String filename) {
  	Object tmp,o[]=new Object[100];
  	int cnt=0;
  	
  	beginLoadObjects(filename);
  	do {
  		tmp=loadObject();
  		if(tmp!=null) {
    		if(cnt==o.length) o=(Object [])UUtil.expandArray(o);
    		o[cnt++]=tmp;
  		}
  	} while(tmp!=null);
  	o=(Object [])UUtil.resizeArray(o, cnt);
  	return o;
  }
  
  public static boolean beginLoadObjects(String filename) {
  	getFilename(filename);
  	if(debugLevel>0) UUtil.log("Begin loading '"+inFilenameNoPath+"'");
  	filename=UIO.getAbsolutePath(inFilename);
  	inCnt=0;
  	ioStartTime=System.currentTimeMillis();
  	
  	try {
  		inObj=new ObjectInputStream(getInputStream(inFilename));
		} catch (Exception e) {
			e.printStackTrace();			
			return false;
		}
		
		return true;  	
  }
  
  public static Object loadObject() {
  	Object o=null;
  	try {
			o=inObj.readObject();
			if(o!=null) inCnt++;
		} catch (Exception e) {
			UUtil.logErr("IO.loadObject(): "+e.toString());
		}
		
  	return o;
  }
  
  public static void endLoadObjects() {
  	if(debugLevel>0) UUtil.log("Done loading '"+inFilenameNoPath+"' - "+inCnt+" objects read. "+
  			UUtil.timeStr(System.currentTimeMillis()-ioStartTime)+" elapsed.");
  	try {
			inObj.close();
			inObj=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
  }

  public static boolean beginSaveObjects(String filename) {
  	getFilename(filename);
		if(debugLevel>0) UUtil.log("Begin saving '"+inFilenameNoPath+"'");
  	
  	inCnt=0;
  	ioStartTime=System.currentTimeMillis();
  	
  	try {
  		outObj=new ObjectOutputStream(getOutputStream(inFilename));
		} catch (Exception e) {
			UUtil.logErr("IO.beginSaveObjects(): "+e.toString());
//			e.printStackTrace();			
			return false;
		}
		
		return true;  	
  }
  
  public static void saveObject(Serializable o) {
  	if(o==null) return;
  	try {
			outObj.writeObject(o);
			inCnt++;
			if(debugLevel>0) if(inCnt%50==0) UUtil.log(inCnt+" objects saved.");
		} catch (Exception e) {
			UUtil.logErr("IO.saveObject(): "+e.toString());
		}
  }

  public static void saveObject(Serializable o[]) {
  	if(o==null) return;
  	try {
  		for(int i=0; i<o.length; i++) {
  			outObj.writeObject(o[i]);
  			inCnt++;
  			if(inCnt%50==0) UUtil.log(inCnt+" objects saved.");
  		}
		} catch (Exception e) {
			UUtil.logErr("IO.saveObject(): "+e.toString());
		}
  }

  public static void endSaveObjects() {
  	try {    		
  		outObj.flush();
  		out.flush();
			outObj.close();
			out.close();
			outObj=null;
			String fstr=getFileSizeString(inFilename);
			if(debugLevel>0) UUtil.log("Saved '"+inFilenameNoPath+"' - "+inCnt+" objects saved ("+fstr+"). "+
	  			UUtil.timeStr(System.currentTimeMillis()-ioStartTime)+" elapsed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
  }

  /**
   * Serialize the object o (and any Serializable objects it refers to) and
   * store its serialized state in File f.
   **/
  public static void saveObject(Serializable o, String filename) throws IOException {
  	getFilename(filename);
    UUtil.log("Saving '"+inFilenameNoPath+"' "+o.getClass().getSimpleName());
		ObjectOutputStream out= // The class for serialization
			new ObjectOutputStream(getOutputStream(inFilename));
		out.writeObject(o); // This method serializes an object graph
		out.close();
		out=null;
  }

  /**
	 * Deserialize the contents of File f and return the resulting object
	 */
  public static Object loadObject(String filename) throws IOException, ClassNotFoundException {
  	getFilename(filename);
    UUtil.log("Loading object stream '"+inFilenameNoPath+"'");
		ObjectInputStream in= // The class for deserialization
			new ObjectInputStream(getInputStream(inFilename));
		
		Object o=in.readObject();
		in.close();
		in=null;
		return o; // This method deserializes an object graph
  }


  ///////////////////////////////////////////////////////////////////
  // LOADING + SAVING OF TEXT FILES
  
  public boolean beginReadStrings(String filename) {
  	getFilename(filename);
  	if(debugLevel>0) UUtil.log("Begin loading '"+inFilenameNoPath+"'");
		
  	inCnt=0;
  	ioStartTime=System.currentTimeMillis();
  	
  	try {
			inReader=new BufferedReader(
						new InputStreamReader(getInputStream(inFilename)));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
  }
  
  public String readString() {
		String s=null;

		if(inReader==null) return null;
		try {
			s=inReader.readLine();
			inCnt++;
			
			if(debugLevel>1) if(inCnt%100000==0) {
				UUtil.log(inCnt+" lines loaded. "+
		  			UUtil.timeStr(System.currentTimeMillis()-ioStartTime)+" elapsed.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}
  
  public int readCount() {
  	return inCnt;
  }
  
  public void endReadStrings() {
  	if(debugLevel>0) UUtil.log("Done loading '"+inFilenameNoPath+"' - "+inCnt+" lines read. "+
  			UUtil.timeStr(System.currentTimeMillis()-ioStartTime)+" elapsed.");
  	try {
			inReader.close();
			inReader=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }  

  public static String [] readTextFile(String filename) {
  	UIO io=new UIO();
  	String ln,s[]=null;
  	int cnt=0;
  	
//  	getFilename(filename);

  	File f=new File(filename);
  	if(!f.exists()) {
  		if(new File(filename+".gz").exists()) filename+=".gz";
  		else {
  			UUtil.log("Could not find file '"+filename+"'");
  			return null;
  		}
  	}

  	io.beginReadStrings(filename);
  	s=new String[10];
  	do {
			ln=io.readString();
			if(ln!=null) {
				if (s.length==cnt) s=(String[])UUtil.expandArray(s);
				s[cnt++]=ln;
			}
		} while (ln!=null);
  	
  	io.endReadStrings();
		s=(String [])UUtil.resizeArray(s, cnt);
		
  	return s;
  }

  public static void beginSaveStrings(String filename) {
  	beginSaveStrings(filename,false);
  }

  public static void beginSaveStrings(String filename,boolean append) {
  	getOutputStream(filename,append);
  	outCnt=0;
  	outWriter=new BufferedWriter(new OutputStreamWriter(out));
  	UUtil.log("beginSaveStrings '"+filename+"'");
  }

  public static void saveStrings(String str) {
  	if(outWriter==null) {
  		UUtil.logErr("saveStrings() - No output stream exists.");
  		return;
  	}

  	try {
			outWriter.write(str);
			outWriter.newLine();
			outCnt++;
			if(outCnt%50000==0) UUtil.log(outCnt+" lines - '"+str+"'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  public static void saveStrings(String str[]) {
  	if(outWriter==null) {
  		UUtil.logErr("saveStrings() - No output stream exists.");
  		return;
  	}

  	try {
  		for(int i=0; i<str.length; i++) {
  			if(str[i]==null) outWriter.write("null");
  			else outWriter.write(str[i]);
  			outWriter.newLine();
  			outCnt++;
  			if(outCnt%50000==0) UUtil.log(outCnt+" lines - '"+str+"'");
  		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  public static void endSaveStrings() {
  	if(outWriter==null) {
  		UUtil.logErr("endSaveStrings() - No output stream exists.");
  		return;
  	}
  	try {
			outWriter.flush();
			outWriter.close();
			outWriter=null;
			out=null;
			
			UUtil.log(outCnt+" lines saved.");
  	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
  }


  public static void debugLevel(int level) {
  	debugLevel=level;
  }
  
  public static void println(String s) {
  	System.out.println(s);
  }
  
  
  ///////////////////////////////////////////////////////////7
  // FILE FILTER FUNCTIONS
  
  class MultiFilter implements FilenameFilter {
    public String filterExt[],filterPat;
    public Pattern filterRegex;  
   
    public void filterExtension(String _ext[]) {
    	filterExt=_ext;
    }

    public void filterPattern(String _pat) {
    	int pos=_pat.indexOf("#");
    	int pos2=_pat.lastIndexOf("#");
    	filterPat=_pat.substring(0,pos);
    	for(int i=pos; i<pos2+1; i++) filterPat=filterPat+"\\d";
    	if(_pat.length()>pos2+1) filterPat+=_pat.substring(pos2+1);
    	filterRegex=Pattern.compile(filterPat);
//    	Util.log("Pattern: '"+filterPat+"'");  	
    }

    public void reset() {
    	filterExt=null;
    	filterRegex=null;
    }
    
  	public boolean accept(File f,String filename) {
  		boolean ok=false;

  		if(filterExt!=null) {
  			String ext=getExt(filename);
  			for(int i=0; i<filterExt.length; i++) {
  				if(filterExt[i].equals(ext)) ok=true;
  			}
  		}
  		if(filterRegex!=null) {
  			Matcher m = filterRegex.matcher(filename);
  			
  			if(m.find()) ok=true; 
//  			Util.log(ok+" Matcher: "+filterRegex.pattern()+" - "+f.getName());
  		}
  				
  		return ok;
  	}
  	
  }


  public static String fixPath(String thePath) {
    thePath=thePath.replace('\\', '/');
    if(!thePath.endsWith("/")) thePath+='/';
    return thePath;
  }
}
