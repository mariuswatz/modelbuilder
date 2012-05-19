package unlekker.util;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class UColorTool {
	public int n=-1,colors[];
	ArrayList<UColorNode> colorNodes=new ArrayList<UColorTool.UColorNode>();
	
	///////////////////////////////////////////
	// PALETTE FUNCTIONS
	
	public UColorTool(UColorTool col) {
		n=col.n;
		colors=new int[n];
		System.arraycopy(col.colors, 0, colors, 0,n);
  }

	public UColorTool() {
	  // TODO Auto-generated constructor stub
  }

	public void reset() {
		n=0;
		colorNodes.clear();
	}

	public static UColorTool parse(String parseStr) {
		UColorTool t=new UColorTool();
		
		UUtil.log("UColorTool parse(String parseStr) "+parseStr);
		
		String tok[]=UUtil.chopBraces(parseStr).split("\t");
		UUtil.log(tok);
		for(int id=0; id<tok.length; id++) {
			if(!tok[id].startsWith("[")) 
				t.colorNodes.add(t.new UColorNode(tok[id]));
			else { 
				tok[id]=UUtil.chopBraces(tok[id]);
				String coltok[]=tok[id].split(",");
				t.n=coltok.length;
				t.colors=new int[t.n];
				for(int j=0; j<coltok.length; j++) 
					t.colors[j]=UUtil.parseInt(coltok[j]);
			}
		}
		
		return t;
	}

	public String toDataString() {
		String s="";
		
		int index=0;
		for(UColorNode node : colorNodes) s+=node.toDataString()+"\t";
		
		if(n>0 && colors!=null) {
			if(colors.length>n) colors=UUtil.expandArray(colors, n);
			s+=UUtil.toString(colors);
		}
		
		return "["+s+"]";
	}

	/**
	 * Adds single color to palette.
	 * @param _col
	 */
	public UColorTool add(int _col) {
		colorNodes.add(new UColorNode(_col));
		return this;
	}

	/**
	 * Adds array of colors to palette.
	 * @param _col
	 */
	public UColorTool add(int _col[]) {
		for(int i=0; i<_col.length; i++) colorNodes.add(new UColorNode(_col[i]));
		return this;
	}

	/**
	 * Adds single hex color to palette.
	 * @param hex
	 */
	public UColorTool add(String hex) {
		add(toColor(hex));
		return this;
	}
	
	/**
	 * Adds single color to palette.
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public UColorTool add(float r, float g, float b, float a) {
		add(toColor(r,g,b,a));
		return this;
	}

	/**
	 * Adds single color to palette.
	 * @param r
	 * @param g
	 * @param b
	 */
	public UColorTool add(float r, float g, float b) {
		add(toColor(r,g,b));
		return this;
	}

	/**
	 * Adds gradient to palette. User must specify the minimum and maximum number of colors the gradient should generate when building a
	 * random color palette.
	 *  
	 * @param min Minimum number of colors this gradient should produce
	 * @param max Maximum number of colors this gradient should produce
	 * @param _col1
	 * @param _col2
	 * @return 
	 */
	public UColorTool addGradient(int min,int max,int _col1,int _col2) {
		colorNodes.add(new UColorNode(min,max,_col1,_col2));
		return this;
	}

	/**
	 * Adds gradient to palette. User must specify the number of colors the gradient should generate when building a
	 * random color palette.
	 *  
	 * @param _num
	 * @param _col1
	 * @param _col2
	 * @return 
	 */
	public UColorTool addGradient(int _num,int _col1,int _col2) {
		addGradient(_num,_num,_col1,_col2);
		return this;
	}

	/**
	 * 	 * Adds gradient to palette. User must specify the minimum and maximum number of colors the gradient should generate when building a
	 * random color palette.
	 *  
	 * @param min
	 * @param max
	 * @param _col1
	 * @param _col2
	 * @return 
	 */
	public UColorTool addGradient(int min,int max,String _col1,String _col2) {
		addGradient(min,max,toColor(_col1),toColor(_col2));
		return this;
	}

	public UColorTool addGradient(int _num,String _col1,String _col2) {
		addGradient(_num,_num,toColor(_col1),toColor(_col2));
		return this;
	}

	public UColorTool addFromImage(PImage img) {
		addFromImage(img,5);
		return this;
	}

	public UColorTool addFromImage(PImage img,int mindiff) {
		int n=0,dupn=0,c,pxn,col[]=new int[100];
		boolean ok;
		
		pxn=img.pixels.length;
		for(int i=0; i<pxn; i++) {
			ok=true;
			c=img.pixels[i] | 0xff000000;
			
			for(int j=0; j<n; j++) {
				if(col[j]==c || getColorDist(c, col[j])<mindiff) {
					ok=false;
					j=n;
					dupn++;
				}
			}
			
			if(ok) {
				if(n==col.length) col=UUtil.expandArray(col);
				col[n++]=c;
			}
		}

		add(col);
		UUtil.log(n+" colors, "+dupn+" duplicates.");
		return this;
	}
	
	/**
	 * Generates randomized palette. All single colors that have been added are automatically included. For each gradient a random 
	 * number of interpolated (t=0..1) colors is generated, decided by the user-provided minimum and maximum number.
	 * 
	 * @param minimum Mininum Minimum number of colors that must be generated. If this number is not reached generateColors() 
	 * is called again.
	 * 	
	 */
	public void generateColors(int minimum) {
		n=0;
		for(UColorNode node : colorNodes) node.generate();
		if(n<minimum) generateColors(minimum);
	}

	/**
	 * Generates randomized palette. All single colors that have been added are automatically included. For each gradient the function
	 * makes a decision whether or not to include it, with the odds specified by the parameter skipGradientChance. if random(100) < skipGradientChance
	 * the gradient is passed over for this palette.
	 * added.
	 * 
	 * @param minimum
	 * @param skipGradientChance
	 */
	public void generateColors(int minimum,int skipGradientChance) {
		n=0;
		for(UColorNode node : colorNodes) {
			// check to see if the node is a gradient, and if so test if we should skip it this time around
			if(!node.isGradient) node.generate();
			else if(UUtil.rnd.random(100)>skipGradientChance) node.generate();
		}
		if(n<minimum) generateColors(minimum);
	}

	/**
	 * Generates randomized palette. 	
	 */
	
	public void generateColors() {
		generateColors(0);
	}
	
	
	protected void buildPalette(int col) {
		if(colors==null) colors=new int[100];
		else if(colors.length==n) colors=UUtil.expandArrayInt(colors);
		colors[n++]=col;
	}

	public int getRandomColor() {
		if(n<1 || colors==null) {
			UUtil.log("UColorTool: No colors have been generated.");
			return toColor(0,0,0);
		}
		
		int id=UUtil.rnd.integer(n);
		return colors[id];
	}

	public void setPalette(int c[]) {
		n=c.length;
		colors=c;
	}

	/** 
	 * Randomly shuffle the contents of the colors array.
	 */
	public void shuffleColors() {
		int id1,id2,tmp;
		if(n<2) return;
		for(int i=0; i<n; i++) {
			do {
				id1=UUtil.rnd.integer(n);
				id2=UUtil.rnd.integer(n);
			} while(id1==id2);
			tmp=colors[id2];
			colors[id2]=colors[id1];
			colors[id1]=tmp;
		}
	}

	public void drawColors(PApplet p,int xx,int yy) {
		String str;
		float lineHeight;
		int x=xx,y=yy,w,h;
		
		p.pushMatrix();
		p.translate(x,y);
		if(n<17) {w=n; h=1;}
		else {
			w=16;
			h=n/16+1;
		}
		
		p.fill(100);
		p.noStroke();
		
		p.rect(0, 0, w*20+10, h*20+10);
		for(int i=0; i<n; i++) {
			p.fill(colors[i]);
			p.rect(5+(i%16)*20,5+(i/16)*20, 18,18);
		}
		
		p.popMatrix();
		
	}
	
	///////////////////////////////////////////
	// GENERAL COLOR FUNCTIONS

	public static final int toColor(int r, int g, int b, int a) {
		return ((a&0xff)<<24)|((r&0xff)<<16)|((g&0xff)<<8)|(b&0xff);
	}

	public static final int toColor(int r, int g, int b) {
		return toColor((int)r,(int)g,(int)b,255);
	}

	public static final int toColor(float r, float g, float b, float a) {
		return toColor((int)r,(int)g,(int)b,(int)a);
	}

	public static final int toColor(float r, float g, float b) {
		return toColor((int)r,(int)g,(int)b,255);
	}

	public static final int luminosity(int c) {
		float rr=red(c),gg=green(c),bb=blue(c);
		float val=(int)(255*(rr*0.21f+gg*0.71f+0.07f*bb));
		return toColor(val,val,val);		
	}
	
  public static final int interpolate(float fract,String col1,String col2) {
  	return interpolate(fract, toColor(col1), toColor(col2));
  }

  public static final int interpolate(float fract,int col1,int col2) {
  	float r,r2,g,g2,b,b2;

  	r=(col1 >> 16)&0xff;
  	r2=((col2 >> 16)&0xff)-r;
  	g=(col1 >> 8)&0xff;
  	g2=((col2 >> 8)&0xff)-g;
  	b=col1&0xff;
  	b2=(col2&0xff)-b;
 
  	return toColor(r + r2 * fract, g + g2 * fract, b + b2 * fract);
  }

	public static final int getAlpha(int col) {
		return (col>>24)&0xff;
	}

	public static final int setAlpha(int col, float alpha) {
		return (((int)alpha<<24)|(0x00ffffff&col));
	}

	public static final int toColor(String hex) {
		int alpha=255;
		if(hex.length()==8) {
			alpha=Integer.parseInt(hex.substring(0,2),16);
			UUtil.log("hex: "+hex+" alpha: "+alpha);
			hex=hex.substring(2);
		}
		
		return (alpha<<24) | Integer.parseInt(hex, 16);
	}

	public static final String colToString(int col) {
		return ("rgba=("+((col>>16)&0xff)+","+((col>>8)&0xff)+","+(col&0xff)+","
				+((col>>24)&0xff)+")");
	}

	/**
	 * Returns the HSB representation of a RGB color. If the parameter "hsb" is  
	 * null a new array will be created, otherwise the existing array is used. 
	 * Brightness and saturation is returned in the ranges 0..255, while hue is
	 * has the range 0..360.
	 * 
	 * @param col
	 * @param hsb
	 * @return
	 */
	public static final float [] toHSB(int col,float hsb[]) {
		if (hsb==null) hsb=new float[3];
		Color.RGBtoHSB((col>>16)&0xff, (col>>8)&0xff, (col&0xff), hsb);
		hsb[0]*=360f;
		hsb[1]*=255f;
		hsb[2]*=255f;
//		
		
//		UUtil.log(UColorTool.colToString(col)+" HSB "+Str.toString(hsb));
		return hsb;
	}

	public static final int adjustBrightness(int c,float mod) {
//	float a=alpha(c);
	float [] hsb=toHSB(c,null);
//	UUtil.log(Str.toString(hsb));
	hsb[2]*=mod;
	
	if(hsb[2]<0) hsb[2]=0; 
	else if(hsb[2]>255) hsb[2]=255; 
	
//	c=setAlpha(fromHSB(hsb), a);
//	UUtil.log(Str.toString(hsb)+" "+Str.toString(toHSB(fromHSB(hsb),null)));
	return fromHSB(hsb);
}

	public static final int adjustHue(int c,float mod) {
//		float a=alpha(c);
		float [] hsb=toHSB(c,null);
//		UUtil.log(Str.toString(hsb));
		hsb[0]=(hsb[0]+mod)%360f;
		if(hsb[0]<0) hsb[0]+=360;
		
//		c=setAlpha(fromHSB(hsb), a);
//		UUtil.log(Str.toString(hsb)+" "+Str.toString(toHSB(fromHSB(hsb),null)));
		return fromHSB(hsb);
	}

	public static final int adjustSaturation(int c,float mod) {
		float a=alpha(c);
		float [] hsb=toHSB(c,null);
		hsb[1]*=mod;
		
		if(hsb[1]<0) hsb[1]=0; 
		else if(hsb[1]>255) hsb[1]=255; 
		
//		c=setAlpha(fromHSB(hsb), a);
		return fromHSB(hsb);
	}

	/**
	 * Calculates RGB from a HSB representation.
	 * 
	 * @param palette
	 * @param hsb Array of three floats, containing hue (in the range 0..360), 
	 * saturation and brightness (both in the range 0.255).
	 * @return RGB value
	 */
	public static final int fromHSB(float hsb[]) {
		int col=fromHSB(hsb[0],hsb[1], hsb[2]);
		return col;
	}

	/**
	 * Calculates RGB from a HSB representation.
	 * 
	 * @param h Hue (in the range 0..360)
	 * @param s Saturation (in the range 0..255)
	 * @param b Brightness (in the range 0..255)
	 * @return RGB value
	 */
	public static final int fromHSB(float h,float s,float b) {
		int col=Color.HSBtoRGB(h/360f,s/255f,b/255f);
		return col;
	}
	
	public static float getColorDist(int c,int c2) {
		return (PApplet.abs(red(c2)-red(c))+
				PApplet.abs(blue(c2)-blue(c))+
				PApplet.abs(green(c2)-green(c)));
	}
	
	public static boolean isWhite(int c) {
		return ((c&0xffffff)==0xffffff);
	}

  public static final float alpha(int col) {
    return (col >> 24) & 0xff;
  }

  public static final float red(int col) {
    return (col >> 16) & 0xff;
  }

  public static final float green(int col) {
    return (col >> 8) & 0xff;
  }

  public static final float blue(int col) {
    return (col) & 0xff;
  }

	public static String toHex(int col) {
		String s="",tmp;
		
		int a=(col >> 24) & 0xff;
		if(a<255) {
			tmp=Integer.toHexString(a);
			if(tmp.length()<2) s+="0"+tmp;
			else s+=tmp;
		}

		tmp=Integer.toHexString((col>>16)&0xff);
		if(tmp.length()<2) s+="0"+tmp;
		else s+=tmp;
		tmp=Integer.toHexString((col>>8)&0xff);
		if(tmp.length()<2) s+="0"+tmp;
		else s+=tmp;
		tmp=Integer.toHexString((col)&0xff);
		if(tmp.length()<2) s+="0"+tmp;
		else s+=tmp;
		
		s=s.toUpperCase();
		return s;
	}
	
	public static int[] colorSort(int cc[]) {
		Object hsb[];
		int n=cc.length;
		hsb=new Object[n];
		
		for(int i=0; i<n; i++) {
			hsb[i]=toHSB(cc[i], null);
//			UUtil.log(i+" "+colToString(cc[i])+" "+
//					Str.toString((float [])hsb[i])+" "+
//					colToString(fromHSB((float [])hsb[i])));
		}
		
		java.util.Arrays.sort(hsb,new ColorCompare());
		for(int i=0; i<n; i++) {
			cc[i]=fromHSB((float [])hsb[i]);
		}
		return cc;
	}

	public static class ColorCompare implements java.util.Comparator{
	  public int compare(Object _o1, Object _o2){
	  	float [] o1=(float [])_o1;
	  	float [] o2=(float [])_o2;

	  	// BRIGHTNESS
	  	if(o1[2]>o2[2]) return 1;
	  	else if(o1[2]<o2[2]) return -1;

	  	// HUE
	  	if(o1[0]>o2[0]) return 1;
	  	else if(o1[0]<o2[0]) return -1;

	  	
	  	// SATURATION
	  	if(o1[1]>o2[1]) return 1;
	  	else if(o1[1]<o2[1]) return -1;
	    return 0;
	  }
	}

	public static String toHex(int r, int g, int b) {
		return toHex(toColor(r, g, b));
	}
	
	class UColorNode {
		boolean isGradient;
		public int col1,col2;
		public int minNum,maxNum;

		public UColorNode(int col) {
			isGradient=false;
			col1=col;			
		}
		
		public UColorNode(int min,int max,int _col1,int _col2) {
			isGradient=true;
			
			col1=_col1;
			col2=_col2;
			minNum=min;
			maxNum=max;
	  }
		
		public UColorNode(int _num,int _col1,int _col2) {
			this(_num,-1,_col1,_col2);
		}
		
		public UColorNode(String parseStr) {
			String tok[]=parseStr.split(",");
			UUtil.log(tok);
			if(tok.length==1) col1=UUtil.parseInt(tok[0]);
			else {
				isGradient=true;
				minNum=UUtil.parseInt(tok[0]);
				maxNum=UUtil.parseInt(tok[1]);
				col1=toColor(tok[2]);
				col2=toColor(tok[3]);
			}
			
			UUtil.log("parsed: "+toDataString());
		}
		
		public void generate() {
			int c,n;
			
			// is single color
			if(!isGradient) {
				buildPalette(col1);
				return;
			}
				
			if(maxNum>minNum) n=UUtil.rnd.integer(minNum,maxNum); 
			else n=minNum;
			
			for(float i=0; i<n; i++) {
				c=interpolate((float)i/(float)(n-1), col1, col2);
				buildPalette(c);
			}
		}
		
		public String toDataString() {
			String s="";
			if(isGradient) 
				s=minNum+","+maxNum+","+toHex(col1)+","+toHex(col2);
			else s=""+col1;
			
			return s;
		}	
	}
}