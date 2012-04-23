package unlekker.modelbuilder;

import processing.core.PApplet;
import processing.core.PConstants;
import unlekker.util.*;

public class UPrimitive implements PConstants {
	public static int SPHERE=1,BOX=2,CYL=3,CONE=4,DISC=5;
	public int type;
	private static int sphRes=-1;
	
	public static UGeometry cylinderGrid(float w,float h,int u,int v, boolean capped) {
		UGeometry geo=new UGeometry();
		UVertexList vl[]=UVertexList.getVertexLists(u);
		float degD=TWO_PI/(float)(u-1);
		float hD=h*2/(float)(v-1);
		
		for(float i=0; i<v; i++) vl[0].add(w,hD*i-h,0); 
		for(int i=1; i<u; i++) {
			vl[i].set(vl[0]).rotateY(degD*(float)i);
		}
			
		geo.quadStrip(vl);
		
		if(capped) {
			geo.add(circleGrid(w, u, u/2, true).translate(0,h,0));
			geo.add(circleGrid(w, u, u/2, false).translate(0,-h,0));
			
		}
		
		return geo;
	}
	
	public static UGeometry circleGrid(float w,int u,int v, boolean reversed) {
		UGeometry geo=new UGeometry();	
		UVertexList vl[]=UVertexList.getVertexLists(u);
		
		float wD=w/(float)(v-1);
		float degD=TWO_PI/(float)(u-1);
		if(reversed) degD=-degD;
		
		for(float i=1; i<v; i++) vl[0].add(wD*i,0,0); 
		UVertexList center=new UVertexList();
		center.add(0,0,0);
		for(int i=0; i<u; i++) {
			if(i>0) vl[i].set(vl[0]).rotateY(degD*(float)i);
			center.add(vl[i].v[0]);
		}			
		geo.quadStrip(vl);
		geo.triangleFan(center, false, true);
		
		return geo;		
	}

	public static UGeometry cylinder(float w,float h,int detail,boolean capped) {
		detail++;
		float D=(TWO_PI)/(float)(detail-1);
		UVertexList vl;
		UVec3 v;
		UGeometry g=new UGeometry();
 
		vl=new UVertexList();
		for(int i=0; i<detail; i++) 
			vl.add(
					PApplet.cos(D*(float)i),
					0,
					PApplet.sin(D*(float)i));

		g.beginShape(QUAD_STRIP);
		for(int i=0; i<detail; i++) {
			v=vl.v[i];
			g.vertex(v.x,1,v.z);
			g.vertex(v.x,-1,v.z);
		}
		g.endShape();
		
		// ADD CYLINDER CAPS IF SPECIFIED
		if(capped) {
			g.beginShape(TRIANGLE_FAN);
			g.vertex(0,1,0);
			for(int i=0; i<detail; i++) 
				g.vertex(vl.v[i].x,1,vl.v[i].z);
			g.endShape();

			g.beginShape(TRIANGLE_FAN);
			g.vertex(0,-1,0);
			for(int i=detail-1; i>-1; i--) 
				g.vertex(vl.v[i].x,-1,vl.v[i].z);
			g.endShape();
		}
		
		g.scale(w, h, w);

		return g;
	}

	public static UGeometry rect(float w,float h) {
		UGeometry g=new UGeometry();
		g.beginShape(QUADS);
		g.vertex(-w,-h,0);
		g.vertex(w,-h,0);
		g.vertex(w,h,0);
		g.vertex(-w,h,0);
		g.endShape();
		
		return g;
	}

	public static UGeometry disc(float rad,int detail) {
		float D=(TWO_PI)/(float)(detail-1);
		UGeometry g=new UGeometry();
		
		g.beginShape(TRIANGLE_FAN);
		g.vertex(0,0,0);
		for(int i=0; i<detail; i++) 
			g.vertex(
					PApplet.cos(D*(float)i),
					PApplet.sin(D*(float)i),
					0);
		g.endShape();

		g.scale(rad);
		
		return g;
	}

	public static UGeometry cone(float r1,float r2,float h,int detail,boolean capped) {
		detail++;
		float D=(TWO_PI)/(float)(detail-1);
		UVertexList vl;
		UVec3 v;
		UGeometry g=new UGeometry();
 
 		vl=new UVertexList();
		for(int i=0; i<detail; i++) 
			vl.add(
					PApplet.cos(D*(float)i),
					0,
					PApplet.sin(D*(float)i));

		g.beginShape(QUAD_STRIP);
		for(int i=0; i<detail; i++) {
			v=vl.v[i];
			g.vertex(v.x*r1,h,v.z*r1);
			g.vertex(v.x*r2,-h,v.z*r2);
		}
		g.endShape();
		
		// ADD CYLINDER CAPS IF SPECIFIED
		if(capped) {
			g.beginShape(TRIANGLE_FAN);
			g.vertex(0,1,0);
			for(int i=0; i<detail; i++) 
				g.vertex(vl.v[i].x,1,vl.v[i].z);
			g.endShape();

			g.beginShape(TRIANGLE_FAN);
			g.vertex(0,-1,0);
			for(int i=detail-1; i>-1; i--) 
				g.vertex(vl.v[i].x,-1,vl.v[i].z);
			g.endShape();
		}

		return g;
	}

	public static UGeometry box(float xdim,float ydim,float zdim) {
		UGeometry g=new UGeometry();
		
		g.beginShape(QUAD_STRIP);

		g.vertex(-xdim,ydim,-zdim);
		g.vertex(xdim,ydim,-zdim);
		
		g.vertex(-xdim,ydim,zdim);
		g.vertex(xdim,ydim,zdim);

		g.vertex(-xdim,-ydim,zdim);
		g.vertex(xdim,-ydim,zdim);

		g.vertex(-xdim,-ydim,-zdim);
		g.vertex(xdim,-ydim,-zdim);	
		
		g.vertex(-xdim,ydim,-zdim);
		g.vertex(xdim,ydim,-zdim);

		g.endShape();

		g.beginShape(QUADS);
		
		g.vertex(-xdim,ydim,-zdim);
		g.vertex(-xdim,ydim,zdim);
		g.vertex(-xdim,-ydim,zdim);
		g.vertex(-xdim,-ydim,-zdim);

		g.vertex(xdim,-ydim,-zdim);
		g.vertex(xdim,-ydim,zdim);
		g.vertex(xdim,ydim,zdim);
		g.vertex(xdim,ydim,-zdim);

		g.endShape();
		
		return g;
	}

  /**
   * Code from PGraphics.java
   * Copyright (c) 2004-09 Ben Fry and Casey Reas
   * Copyright (c) 2001-04 Massachusetts Institute of Technology
   * 
   * Draw a sphere with radius r centered at coordinate 0, 0, 0.
   * <P>
   * Implementation notes:
   * <P>
   * cache all the points of the sphere in a static array
   * top and bottom are just a bunch of triangles that land
   * in the center point
   * <P>
   * sphere is a series of concentric circles who radii vary
   * along the shape, based on, er.. cos or something
   * <PRE>
   * [toxi 031031] new sphere code. removed all multiplies with
   * radius, as scale() will take care of that anyway
   *
   * [toxi 031223] updated sphere code (removed modulos)
   * and introduced sphereAt(x,y,z,r)
   * to avoid additional translate()'s on the user/sketch side
   *
   * [davbol 080801] now using separate sphRes/V
   * </PRE>
   */

	public static UGeometry sphere(float rad,int detail) {
		UGeometry g=new UGeometry();
		
		sphereDetail(detail);
		
    // 1st ring from south pole
    g.beginShape(TRIANGLE_STRIP);
    for (int i = 0; i < sphRes; i++) {
      g.vertex(0, -1, 0);      
      g.vertex(sphereX[i], sphereY[i], sphereZ[i]);
    }
    g.vertex(0, -1, 0);
    g.vertex(sphereX[0], sphereY[0], sphereZ[0]);
    g.endShape();

    int v1,v11,v2;

    // middle rings
    int voff = 0;
    for (int i = 2; i < sphRes; i++) {
      v1 = v11 = voff;
      voff += sphRes;
      v2 = voff;
      g.beginShape(TRIANGLE_STRIP);
      for (int j = 0; j < sphRes; j++) {
      	g.vertex(sphereX[v1], sphereY[v1], sphereZ[v1++]);
      	g.vertex(sphereX[v2], sphereY[v2], sphereZ[v2++]);
      }
      // close each ring
      v1 = v11;
      v2 = voff;
      g.vertex(sphereX[v1], sphereY[v1], sphereZ[v1]);
      g.vertex(sphereX[v2], sphereY[v2], sphereZ[v2]);
      g.endShape();
    }

    // add the northern cap
    g.beginShape(TRIANGLE_STRIP);
    for (int i = 0; i < sphRes; i++) {
      v2 = voff + i;
      g.vertex(sphereX[v2], sphereY[v2], sphereZ[v2]);
      g.vertex(0, 1, 0);
    }
    g.vertex(sphereX[voff], sphereY[voff], sphereZ[voff]);
    g.vertex(0, 1, 0);
    g.endShape();


    g.scale(rad);
		return g;
	}
	

  // [toxi031031] new & faster sphere code w/ support flexibile resolutions
  // will be set by sphereDetail() or 1st call to sphere()
  static float sphereX[];
	static float sphereY[];
	static float sphereZ[];
  
  // ........................................................
  // precalculate sin/cos lookup tables [toxi]
  // circle resolution is determined from the actual used radii
  // passed to ellipse() method. this will automatically take any
  // scale transformations into account too

  // [toxi 031031]
  // changed table's precision to 0.5 degree steps
  // introduced new vars for more flexible code
  static final protected float sinLUT[];
  static final protected float cosLUT[];
  static final protected float SINCOS_PRECISION = 0.5f;
  static final protected int SINCOS_LENGTH = (int) (360f / SINCOS_PRECISION);
  static {
    sinLUT = new float[SINCOS_LENGTH];
    cosLUT = new float[SINCOS_LENGTH];
    for (int i = 0; i < SINCOS_LENGTH; i++) {
      sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
      cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
    }
  }

  /**
   * Code from PGraphics.java
   * Copyright (c) 2004-09 Ben Fry and Casey Reas
   * Copyright (c) 2001-04 Massachusetts Institute of Technology
   * 
   * Set the detail level for approximating a sphere. The ures and vres params
   * control the horizontal and vertical resolution.
   *
   * Code for sphereDetail() submitted by toxi [031031].
   * Code for enhanced u/v version from davbol [080801].
   */
  private static void sphereDetail(int res) {
  	if(res==sphRes) return;
    if (res < 3) res = 3; // force a minimum res
    if ((res == sphRes)) return;
    sphRes=res;

    float delta = (float)SINCOS_LENGTH/sphRes;
    float[] cx = new float[sphRes];
    float[] cz = new float[sphRes];
    // calc unit circle in XZ plane
    for (int i = 0; i < sphRes; i++) {
      cx[i] = cosLUT[(int) (i*delta) % SINCOS_LENGTH];
      cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
    }
    // computing vertexlist
    // vertexlist starts at south pole
    int vertCount = sphRes * (sphRes-1) + 2;
    int currVert = 0;

    // re-init arrays to store vertices
    sphereX = new float[vertCount];
    sphereY = new float[vertCount];
    sphereZ = new float[vertCount];

    float angle_step = (SINCOS_LENGTH*0.5f)/sphRes;
    float angle = angle_step;

    // step along Y axis
    for (int i = 1; i < sphRes; i++) {
      float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
      float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
      for (int j = 0; j < sphRes; j++) {
        sphereX[currVert] = cx[j] * curradius;
        sphereY[currVert] = currY;
        sphereZ[currVert++] = cz[j] * curradius;
      }
      angle += angle_step;
    }
  }


	/*
	//////////////////////////////////////
	// STATIC UTILITY FUNCTIONS
	
	public static void cylinderDetail(int num) {
	  preCylNum=num;
	  preCylX=new float[num];
	  preCylY=new float[num];

	  float deg=(360/(float)num);
	  for(int i=0; i<num; i++) {
	    preCylX[i]=Util.COS((float)i*deg);
	    preCylY[i]=Util.SIN((float)i*deg);
	  }
	}

	public static void cylinder(PApplet p,float x,float y,float z, float w,float h) {
	  // checking to see if cylinderDetail() has been
	  // called yet
	  int next;
	  if(preCylX==null) cylinderDetail(12);  
	  
	  p.pushMatrix(); // save current world matrix
	  p.translate(x,y,z); 
	  
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum; i++) {
	  	next=(i+1)%preCylNum;
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
		  p.vertex(0,h,0);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>-1; i--) {
	  	next=i-1;
	  	if(next<0) next=preCylNum-1;
	  	p.vertex(preCylX[next]*w, -h, preCylY[next]*w);
		  p.vertex(0,-h,0);
	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  }
	  p.endShape();
	  
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum; i++) {
	  	next=(i+1)%preCylNum;
	  	
	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);

	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);
	  	p.vertex(preCylX[next]*w, -h, preCylY[next]*w);
	  }
	  p.endShape();

	  p.popMatrix(); // restore saved world matrix
	}
	
	public static void hollowCylinder(PApplet p,float x,float y,float z, float r1,float r2,float h) {
		hollowCylinderArc(p,x,y,z,0,360,r1,r2,h);
	}

	public static void hollowCylinderArc(PApplet p,float x,float y,float z, 
			float deg1,float deg2, float r1,float r2,float h) {

		int num=preCylNum;
		float cx[],cy[];

		if(num==0) {
			cylinderDetail(12);
			num=preCylNum;
		}
		
	  cx=new float[num];
	  cy=new float[num];

//	  Util.log("hollowCylinderArc: "+deg1+" "+deg2);
	  if(deg1<0) deg1+=360;
	  if(deg2<0) deg2+=360;
//	  Util.log("hollowCylinderArc: "+deg1+" "+deg2+" (corrected)");
	  
	  if(deg1>deg2) {
	  	float tmp=deg2;
	  	deg2=deg1;
	  	deg1=tmp;
	  }
	  
	  float deg=((deg2-deg1)/(float)(num-1));
	  int deglut;
	  for(int i=0; i<num; i++) {
	  	deglut=(int)(((float)i*deg+deg1)*Util.SINCOS_INVPREC);
	  	deglut=deglut % Util.SINCOS_LENGTH;
	    cx[i]=Util.cosLUT[deglut];
	    cy[i]=-Util.sinLUT[deglut];
	    
	    cx[i]=(float)Math.cos(((float)i*deg+deg1)*DEG_TO_RAD);
	    cy[i]=-(float)Math.sin(((float)i*deg+deg1)*DEG_TO_RAD);
	  }

		
		p.pushMatrix();
		p.translate(x,y,z);

	
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum-1; i++) {
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  	p.vertex(cx[i]*r2, -h, cy[i]*r2);
	  	p.vertex(cx[i+1]*r2, -h, cy[i+1]*r2);

	  	p.vertex(cx[i+1]*r2, -h, cy[i+1]*r2);
	  	p.vertex(cx[i+1]*r2, h, cy[i+1]*r2);
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>0; i--) {
	  	p.vertex(cx[i-1]*r2, h, cy[i-1]*r2);
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  	p.vertex(cx[i]*r1, h, cy[i]*r1);

	  	p.vertex(cx[i]*r1, h, cy[i]*r1);
	  	p.vertex(cx[i-1]*r1, h, cy[i-1]*r1);
	  	p.vertex(cx[i-1]*r2, h, cy[i-1]*r2);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>0; i--) {
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  	p.vertex(cx[i]*r2, -h, cy[i]*r2);
	  	p.vertex(cx[i-1]*r2, -h, cy[i-1]*r2);

	  	p.vertex(cx[i-1]*r2, -h, cy[i-1]*r2);
	  	p.vertex(cx[i-1]*r1, -h, cy[i-1]*r1);
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum-1; i++) {
	  	p.vertex(cx[i+1]*r1, -h, cy[i+1]*r1);
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  	p.vertex(cx[i]*r1, h, cy[i]*r1);

	  	p.vertex(cx[i]*r1, h, cy[i]*r1);
	  	p.vertex(cx[i+1]*r1, h, cy[i+1]*r1);
	  	p.vertex(cx[i+1]*r1, -h, cy[i+1]*r1);
	  }
	  p.endShape();
		
	  p.popMatrix();
	}

	public static void cone(PApplet p,float x,float y,float z, 
	  float w,float w2,float h) {
	  // checking to see if cylinderDetail() has been
	  // called yet
	  if(preCylX==null) cylinderDetail(12);  
	  
	  p.pushMatrix(); // save current world matrix
	  p.translate(x,y,z); 
	  
	  p.beginShape(TRIANGLE_FAN);
	  p.vertex(0,h,0);
	  for(int i=0; i<preCylNum; i++) 
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w); 

	  // provide last vertex to come full circle  
	  p.vertex(preCylX[0]*w, h, preCylY[0]*w);
	  p.endShape();

	  p.beginShape(TRIANGLE_FAN);
	  p.vertex(0,-h,0);
	  for(int i=0; i<preCylNum; i++)
	  	p.vertex(preCylX[i]*w2, -h, preCylY[i]*w2); 

	  // provide last vertex to come full circle  
	  p.vertex(preCylX[0]*w2, -h, preCylY[0]*w2);
	  p.endShape();
	  
	  p.beginShape(QUAD_STRIP);
	  for(int i=0; i<preCylNum; i++) {
	  	p.vertex(preCylX[i]*w2, -h, preCylY[i]*w2);
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
	  }
	  p.vertex(preCylX[0]*w2, -h, preCylY[0]*w2);
	  p.vertex(preCylX[0]*w, h, preCylY[0]*w);
	  p.endShape();
	  
	  p.popMatrix(); // restore saved world matrix
	}	
	 */
}
