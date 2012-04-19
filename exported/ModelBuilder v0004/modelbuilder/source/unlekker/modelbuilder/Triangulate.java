package unlekker.modelbuilder;

/*
 * Code taken from http://wiki.processing.org/w/Triangulation
 * Tom Carden modified Florian Jenett's adaptation of Paul 
 * Bourke's C code.
 * 
 *	ported from p bourke's triangulate.c
 *	http://astronomy.swin.edu.au/~pbourke/modelling/triangulate/
 *
 *	fjenett, 20th february 2005, offenbach-germany.
 *	contact: http://www.florianjenett.de/
 *
 *      adapted to take a Vector of Point3f objects and return a Vector of Triangles
 *      (and generally be more Java-like and less C-like in usage - 
 *       and probably less efficient but who's benchmarking?)
 *      Tom Carden, tom (at) tom-carden.co.uk 17th January 2006
 *
 *      adapted to get rid of those ugly Vector and Point3f objects. it now takes an
 *      ArrayList of PVector objects and return an ArrayList of Triangles objects.
 *      see what Sun thinks about Vector objects here:
 *      http://java.sun.com/developer/technicalArticles/Collections/Using/index.html
 *      antiplastik, 28 june 2010, paris-france
 *
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PVector;
import unlekker.util.Util;

public class Triangulate {

  /*
    From P Bourke's C prototype - 

    qsort(p,nv,sizeof(XYZ),XYZCompare);
		
    int XYZCompare(void *v1,void *v2) {
      XYZ *p1,*p2;
      p1 = v1;
      p2 = v2;
      if (p1->x < p2->x)
        return(-1);
      else if (p1->x > p2->x)
        return(1);
      else
        return(0);
    }
  */
  private static class XComparator implements Comparator {
    
    public int compare(Object _p1,Object _p2) {
    	PVector p1=(PVector)_p1;
    	PVector p2=(PVector)_p2;
    	
      if (p1.x < p2.x) {
        return -1;
      }
      else if (p1.x > p2.x) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

	public static boolean DODEBUG=true;;

  /*
    Return TRUE if a point (xp,yp) is inside the circumcircle made up
    of the points (x1,y1), (x2,y2), (x3,y3)
    The circumcircle centre is returned in (xc,yc) and the radius r
    NOTE: A point on the edge is inside the circumcircle
  */
  private static boolean circumCircle(PVector p, Triangle t, PVector circle) {

    float m1,m2,mx1,mx2,my1,my2;
    float dx,dy,rsqr,drsqr;
		
    /* Check for coincident points */
    if ( PApplet.abs(t.p1.y-t.p2.y) < PApplet.EPSILON && PApplet.abs(t.p2.y-t.p3.y) < PApplet.EPSILON ) {
      System.err.println("CircumCircle: Points are coincident.");
      return false;
    }

    if ( PApplet.abs(t.p2.y-t.p1.y) < PApplet.EPSILON ) {
      m2 = - (t.p3.x-t.p2.x) / (t.p3.y-t.p2.y);
      mx2 = (t.p2.x + t.p3.x) / 2.0f;
      my2 = (t.p2.y + t.p3.y) / 2.0f;
      circle.x = (t.p2.x + t.p1.x) / 2.0f;
      circle.y = m2 * (circle.x - mx2) + my2;
    }
    else if ( PApplet.abs(t.p3.y-t.p2.y) < PApplet.EPSILON ) {
      m1 = - (t.p2.x-t.p1.x) / (t.p2.y-t.p1.y);
      mx1 = (t.p1.x + t.p2.x) / 2.0f;
      my1 = (t.p1.y + t.p2.y) / 2.0f;
      circle.x = (t.p3.x + t.p2.x) / 2.0f;
      circle.y = m1 * (circle.x - mx1) + my1;	
    }
    else {
      m1 = - (t.p2.x-t.p1.x) / (t.p2.y-t.p1.y);
      m2 = - (t.p3.x-t.p2.x) / (t.p3.y-t.p2.y);
      mx1 = (t.p1.x + t.p2.x) / 2.0f;
      mx2 = (t.p2.x + t.p3.x) / 2.0f;
      my1 = (t.p1.y + t.p2.y) / 2.0f;
      my2 = (t.p2.y + t.p3.y) / 2.0f;
      circle.x = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
      circle.y = m1 * (circle.x - mx1) + my1;
    }
	
    dx = t.p2.x - circle.x;
    dy = t.p2.y - circle.y;
    rsqr = dx*dx + dy*dy;
    circle.z = PApplet.sqrt(rsqr);
		
    dx = p.x - circle.x;
    dy = p.y - circle.y;
    drsqr = dx*dx + dy*dy;
	
    return drsqr <= rsqr;
  }

  /*
   * Marius Watz adaptation to use Vec3 array
   */
  public static UVec3 [] triangulate(UVec3 v[]) {
  	UVec3 tri[]=null;
  	ArrayList pv=new ArrayList();
  	
  	if(DODEBUG) Util.log("triangulate() - "+v.length+" "+v.length/3);
  	
  	for(int i=0; i<v.length; i++) 
  		pv.add(new PVector(v[i].x,v[i].y,v[i].z));
  	
  	ArrayList triArr=triangulate(pv);
  	if(DODEBUG) Util.log("result: "+triArr.size());

  	if(triArr.size()>0) {
    	tri=new UVec3[triArr.size()*3];
      Iterator pIter = triArr.iterator();
      
      int id=0;
      while (pIter.hasNext()) {
      	Triangle t = (Triangle)pIter.next();
      	tri[id++]=new UVec3(t.p1.x,t.p1.y,t.p1.z);
      	tri[id++]=new UVec3(t.p2.x,t.p2.y,t.p2.z);
      	tri[id++]=new UVec3(t.p3.x,t.p3.y,t.p3.z);
      }
  	}
  	return tri;
  }

  /*
    Triangulation subroutine
    Takes as input vertices (PVectors) in ArrayList pxyz
    Returned is a list of triangular faces in the ArrayList triangles 
    These triangles are arranged in a consistent clockwise order.
  */
  public static ArrayList triangulate( ArrayList pxyz ) {
  
    // sort vertex array in increasing x values
    Collections.sort(pxyz, new XComparator());
    		
    /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
    */
    float xmin = ((PVector)pxyz.get(0)).x;
    float ymin = ((PVector)pxyz.get(0)).y;
    float xmax = xmin;
    float ymax = ymin;
    
    Iterator pIter = pxyz.iterator();
    while (pIter.hasNext()) {
      PVector p = (PVector)pIter.next();
      if (p.x < xmin) xmin = p.x;
      if (p.x > xmax) xmax = p.x;
      if (p.y < ymin) ymin = p.y;
      if (p.y > ymax) ymax = p.y;
    }
    
    float dx = xmax - xmin;
    float dy = ymax - ymin;
    float dmax = (dx > dy) ? dx : dy;
    float xmid = (xmax + xmin) / 2.0f;
    float ymid = (ymax + ymin) / 2.0f;
	
    ArrayList triangles = new ArrayList(); // for the Triangles
    HashSet complete = new HashSet(); // for complete Triangles

    /*
      Set up the supertriangle
      This is a triangle which encompasses all the sample points.
      The supertriangle coordinates are added to the end of the
      vertex list. The supertriangle is the first triangle in
      the triangle list.
    */
    Triangle superTriangle = new Triangle();
    superTriangle.p1 = new PVector( xmid - 2.0f * dmax, ymid - dmax, 0.0f );
    superTriangle.p2 = new PVector( xmid, ymid + 2.0f * dmax, 0.0f );
    superTriangle.p3 = new PVector( xmid + 2.0f * dmax, ymid - dmax, 0.0f );
    triangles.add(superTriangle);
    
    /*
      Include each point one at a time into the existing mesh
    */
    ArrayList edges = new ArrayList();
    pIter = pxyz.iterator();
    while (pIter.hasNext()) {
    
      PVector p = (PVector)pIter.next();
      
      edges.clear();
      
      /*
        Set up the edge buffer.
        If the point (xp,yp) lies inside the circumcircle then the
        three edges of that triangle are added to the edge buffer
        and that triangle is removed.
      */
      PVector circle = new PVector();
      
      for (int j = triangles.size()-1; j >= 0; j--) {
      
        Triangle t = (Triangle)triangles.get(j);
        if (complete.contains(t)) {
          continue;
        }
          
        boolean inside = circumCircle( p, t, circle );
        
        if (circle.x + circle.z < p.x) {
          complete.add(t);
        }
        if (inside) {
          edges.add(new Edge(t.p1, t.p2));
          edges.add(new Edge(t.p2, t.p3));
          edges.add(new Edge(t.p3, t.p1));
          triangles.remove(j);
        }
                
      }

      /*
        Tag multiple edges
        Note: if all triangles are specified anticlockwise then all
        interior edges are opposite pointing in direction.
      */
      for (int j=0; j<edges.size()-1; j++) {
        Edge e1 = (Edge)edges.get(j);
        for (int k=j+1; k<edges.size(); k++) {
          Edge e2 = (Edge)edges.get(k);
          if (e1.p1 == e2.p2 && e1.p2 == e2.p1) {
            e1.p1 = null;
            e1.p2 = null;
            e2.p1 = null;
            e2.p2 = null;
          }
          /* Shouldn't need the following, see note above */
          if (e1.p1 == e2.p1 && e1.p2 == e2.p2) {
            e1.p1 = null;
            e1.p2 = null;
            e2.p1 = null;
            e2.p2 = null;
          }
        }
      }
      
      /*
        Form new triangles for the current point
        Skipping over any tagged edges.
        All edges are arranged in clockwise order.
      */
      for (int j=0; j < edges.size(); j++) {
        Edge e = (Edge)edges.get(j);
        if (e.p1 == null || e.p2 == null) {
          continue;
        }
        triangles.add(new Triangle(e.p1, e.p2, p));
        if(DODEBUG) Util.log("addTriangle "+triangles.size());
      }
      
    }
      
    /*
      Remove triangles with supertriangle vertices
    */
    for (int i = triangles.size()-1; i >= 0; i--) {
      Triangle t = (Triangle)triangles.get(i);
      if (t.sharesVertex(superTriangle)) {
        triangles.remove(i);
        if(DODEBUG) Util.log("removeTriangle "+triangles.size());
      }
    }

    return triangles;
  }
	
  static class Triangle {

    public PVector p1, p2, p3;
    
    public Triangle() { 
      p1=null;
      p2=null;
      p3=null; 
    }
    
    public Triangle(PVector p1, PVector p2, PVector p3) {
      this.p1 = p1;
      this.p2 = p2;
      this.p3 = p3;
    }
    
    public boolean sharesVertex(Triangle other) {
      return p1 == other.p1 || p1 == other.p2 || p1 == other.p3 ||
             p2 == other.p1 || p2 == other.p2 || p2 == other.p3 || 
             p3 == other.p1 || p3 == other.p2 || p3 == other.p3;
    }
    
  }

  static class Edge {
    
    public PVector p1, p2;
    
    public Edge() {
      p1=null;
      p2=null;
    }
    
    public Edge(PVector p1, PVector p2) {
      this.p1 = p1;
      this.p2 = p2;
    }
    
  }
}
