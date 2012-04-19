package unlekker.util;

import java.io.File;
import java.util.HashMap;

import processing.core.PShape;

public interface UConstants extends processing.core.PConstants {
  public static final int savePNG=0,saveTGA=1,saveJPG=2,savePDF=100,
  saveSTL=101,saveDXF=102,saveOBJ=103,saveMOV=200,saveTILE=201;

  public static final String classOBJ    = "aLib.aData.RawOBJ";
  public static final String classSTL    = "aLib.aData.RawSTL";
  public static final String MODELRECORDER="unlekker.geom.ModelRecorder";
  public static final String POVRAY="unlekker.geom.POVRAY";
  
  public static final char COMMACHAR=',',TABCHAR='\t',DIRCHAR=File.separatorChar;
  public static final String COMMASTR=",",TABSTR="\t",DIRSTR=""+File.separatorChar;

  public static String EOF=System.getProperty("line.separator");
  
	public static final float A1W=2383.937f,A1H=1683.7795f,PTTOCM=72f/2.54f,PTTOMM=72f/25.4f,PTTOINCH=72f;
	public static final int XYPLANE=0,XZPLANE=1,YZPLANE=2;
	public static final int FADEIN=1,FADEOUT=-1,FADEINDONE=2,FADEOUTDONE=3;
	
	// will be filled with values by unlekker.util.Util initialization
	public static String shapeTypes []=new String[43];

	public static int GROUP=PShape.GROUP;
	public static int PRIMITIVE=PShape.PRIMITIVE;
	public static int GEOMETRY=PShape.GEOMETRY;
	public static int PATH=PShape.PATH;
	public static String [] PShapeFamilyNames = {
		"GROUP","PRIMITIVE","PATH","GEOMETRY"
	};

}

