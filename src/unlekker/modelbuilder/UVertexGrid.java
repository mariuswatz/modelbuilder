package unlekker.modelbuilder;

import java.util.ArrayList;

import processing.core.PApplet;
import unlekker.util.*;

/**
 * Convenience class for defining and manipulating grids of UVec3 vertices. Useful for Bezier patch design, especially 
 * since it contains the join() function for joining two UVertexGrids along a given edge.
 * @author marius
 *
 */
public class UVertexGrid implements UConstants {
	public int u,v;
	public UVec3 [][] grid;
	static public int LEFT_EDGE=0,RIGHT_EDGE=1,TOP_EDGE=2,BOTTOM_EDGE=3;
	public UBBox bb;
	public UGeometry geo;
	
	public UVertexGrid(int _u,int _v,float w,float h) {
		vertexGrid(_u,_v,w,h);
	}
	
	public void draw(PApplet p) {
		if(geo!=null) {
			geo.draw(p);
			return;
		}

		for(int i=0; i<u-1; i++) {
			p.beginShape(QUAD_STRIP);
			for(int j=0; j<v; j++) {
				p.vertex(grid[i][j].x,grid[i][j].y,grid[i][j].z);
				p.vertex(grid[i+1][j].x,grid[i+1][j].y,grid[i+1][j].z);
			}
			p.endShape();
		}
		
	}
	
	/**
	 * Generates vertex grid with dimension u x v vertices, centered around <0,0,0> and
	 * extending from <-w/2,-h/2,0> to <w/2,h/2,0>  
	 * @param u
	 * @param v
	 * @param w
	 * @param h
	 * @return
	 */
	public UVertexGrid vertexGrid(int _u,int _v,float w,float h) {
		grid=new UVec3[_u][_v];
		UVec3 start;
		float xD,yD;
		
		u=_u;
		v=_v;
		
		start=new UVec3(-w/2f,-h/2f,0);
		xD=w/(float)(u-1);
		yD=h/(float)(v-1);
		
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j]=new UVec3(xD*(float)i,yD*(float)j,0).add(start);
			}
		}
		
		return this;
	}
	
	public UGeometry toUGeometry() {
		if(geo==null) geo=new UGeometry();
		else geo.reset();
		
		for(int i=0; i<u-1; i++) {
			geo.beginShape(QUAD_STRIP);
			for(int j=0; j<v; j++) {
				geo.vertex(grid[i][j]);
				geo.vertex(grid[i+1][j]);
			}
			geo.endShape();
		}

		return geo;
	}

	public static UGeometry toUGeometry(UVertexGrid [] grid) {
		UGeometry mesh=new UGeometry();
		for(int i=0; i<grid.length; i++) mesh.add(grid[i].toUGeometry());
		
		return mesh;
	}

	public UVec3 getRandomVertex() {
		return grid[UUtil.rnd.integer(u)][UUtil.rnd.integer(v)];
	}
	
	/**
	 * Translates all vertices in a UVec3[][] grid by the vector trans.
	 * @param grid
	 * @param trans
	 * @return
	 */
	public UVertexGrid translate(UVec3 trans) {
		return translate(trans.x,trans.y,trans.z);
	}

	/**
	 * Translates all vertices in a UVec3[][] grid by <tx,ty,tz>.
	 * @param grid
	 * @param trans
	 * @return
	 */
	public UVertexGrid translate(float tx,float ty,float tz) {
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j].add(tx,ty,tz);
			}
		}
		return this;
	}

	public UVertexGrid calcBounds() {
		bb=new UBBox();
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				bb.add(grid[i][j]);
			}
		}
		bb.finishCalc();
		return this;
	}

	public static UBBox calcBounds(UVertexGrid grid[]) {
		UBBox bb=new UBBox();
		for(int k=0; k<grid.length; k++) {
			for(int i=0; i<grid[k].u; i++) {
				for(int j=0; j<grid[k].v; j++) {
					bb.add(grid[k].grid[i][j]);
				}
			}
		}
		bb.finishCalc();
		return bb;
	}

	/**
	 * Translate multiple UVertexGrids as one object. Safe to use even after any edges have been joined,
	 * since it checks to see if a given UVec3 instance has already been transformed. If so, no duplicate 
	 * transformation is executed.  
	 * @param vv
	 * @param tx
	 * @param ty
	 * @param tz
	 */
	public static void translate(UVertexGrid[] vv,float tx,float ty,float tz) {
		ArrayList<UVec3> moved;
		UVertexGrid grid;
		UVec3 curr;
		
		moved=new ArrayList<UVec3>();
		
		for(int gridIndex=0; gridIndex<vv.length; gridIndex++) {
			grid=vv[gridIndex];
			for(int i=0; i<grid.u; i++) 
				for(int j=0; j<grid.v; j++) {
					curr=grid.grid[i][j];
					if(!moved.contains(curr)) {
						curr.add(tx,ty,tz);
						moved.add(curr);
					}
				}
		}
	}

	/**
	 * Scale multiple UVertexGrids as one object. Safe to use even after any edges have been joined,
	 * since it checks to see if a given UVec3 instance has already been transformed. If so, no duplicate 
	 * transformation is executed.  
	 * @param vv
	 * @param mx
	 * @param my
	 * @param mz
	 */
	public static void scale(UVertexGrid[] vv,float mx,float my,float mz) {
		ArrayList<UVec3> moved;
		UVertexGrid grid;
		UVec3 curr;
		
		moved=new ArrayList<UVec3>();
		
		for(int gridIndex=0; gridIndex<vv.length; gridIndex++) {
			grid=vv[gridIndex];
			for(int i=0; i<grid.u; i++) 
				for(int j=0; j<grid.v; j++) {
					curr=grid.grid[i][j];
					if(!moved.contains(curr)) {
						curr.mult(mx,my,mz);
						moved.add(curr);
					}
				}
		}
	}
	
	/**
	 * Scale multiple UVertexGrids as one object. Safe to use even after any edges have been joined,
	 * since it checks to see if a given UVec3 instance has already been transformed. If so, no duplicate 
	 * transformation is executed.  
	 * @param vv
	 * @param deg
	 */
	public static void rotateX(UVertexGrid[] vv,float deg) {
		ArrayList<UVec3> moved;
		UVertexGrid grid;
		UVec3 curr;
		
		moved=new ArrayList<UVec3>();
		
		for(int gridIndex=0; gridIndex<vv.length; gridIndex++) {
			grid=vv[gridIndex];
			for(int i=0; i<grid.u; i++) 
				for(int j=0; j<grid.v; j++) {
					curr=grid.grid[i][j];
					if(!moved.contains(curr)) {
						curr.rotateX(deg);
						moved.add(curr);
					}
				}
		}
	}

	/**
	 * Rotates multiple UVertexGrids as one object. Safe to use even after any edges have been joined,
	 * since it checks to see if a given UVec3 instance has already been transformed. If so, no duplicate 
	 * transformation is executed.  
	 * @param vv
	 * @param deg
	 */
	public static void rotateY(UVertexGrid[] vv,float deg) {
		ArrayList<UVec3> moved;
		UVertexGrid grid;
		UVec3 curr;
		
		moved=new ArrayList<UVec3>();
		
		for(int gridIndex=0; gridIndex<vv.length; gridIndex++) {
			grid=vv[gridIndex];
			for(int i=0; i<grid.u; i++) 
				for(int j=0; j<grid.v; j++) {
					curr=grid.grid[i][j];
					if(!moved.contains(curr)) {
						curr.rotateY(deg);
						moved.add(curr);
					}
				}
		}
	}

	/**
	 * Rotates multiple UVertexGrids as one object. Safe to use even after any edges have been joined,
	 * since it checks to see if a given UVec3 instance has already been transformed. If so, no duplicate 
	 * transformation is executed.  
	 * @param vv
	 * @param deg
	 */
	public static void rotateZ(UVertexGrid[] vv,float deg) {
		ArrayList<UVec3> moved;
		UVertexGrid grid;
		UVec3 curr;
		
		moved=new ArrayList<UVec3>();
		
		for(int gridIndex=0; gridIndex<vv.length; gridIndex++) {
			grid=vv[gridIndex];
			for(int i=0; i<grid.u; i++) 
				for(int j=0; j<grid.v; j++) {
					curr=grid.grid[i][j];
					if(!moved.contains(curr)) {
						curr.rotateZ(deg);
						moved.add(curr);
					}
				}
		}
	}

	/**
	 * Scales all vertices by the multiplier sc.
	 * @param grid
	 * @param sc
	 * @return
	 */
	public UVertexGrid scale(UVec3 sc) {
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j].mult(sc);
			}
		}
		return this;
	}

	/**
	 * Rotates all vertices around the X axis.
	 * @param deg
	 * @return
	 */
	public UVertexGrid rotateX(float deg) {
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j].rotateX(deg);
			}
		}
		return this;
	}

	/**
	 * Rotates all vertices around the Y axis.
	 * @param deg
	 * @return
	 */
	public UVertexGrid rotateY(float deg) {
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j].rotateY(deg);
			}
		}
		return this;
	}

	/**
	 * Rotates all vertices around the Z axis.
	 * @param deg
	 * @return
	 */
	public UVertexGrid rotateZ(float deg) {
		for(int i=0; i<u; i++) {
			for(int j=0; j<v; j++) {
				grid[i][j].rotateZ(deg);
			}
		}
		return this;
	}
	
	/**
	 * Simple utility to join the edges of two vertex grids. Edges are specifed as LEFT_EDGE, RIGHT_EDGE, BOTTOM_EDGE, TOP_EDGE.
	 * The two edges must have the same number of vertices. All vertices on the two edges are set to use the same UVec3 instances
	 * so that any future change to those vertices affect both vertex grids. 
	 *   
	 * Note: Once a UVertexGrid has been joined any further transformations on the joined pieces should only be
	 * executed through translate(UVertexGrid vv[],float tx,float ty,float tz) and matching functions. Since vertices are
	 * shared between joined UVertexGrids regular transformations will duplicate the transformation on the shared
	 * vertices.
	 * @param edge
	 * @param grid2
	 * @return
	 */
	public UVertexGrid joinEdge(int edge1,int edge2,UVertexGrid grid2,boolean reverse) {
		UVec3 D,edgeVert[];
		
		edgeVert=getEdge(edge1,reverse);
//		grid2.translate(new UVec3(edgeVert[0]).sub(grid2.getEdge(edge2)[0]));
  	grid2.setEdge(edge2, edgeVert);
		
		return this;
	}
	
	public UVec3 [] getEdge(int edge,boolean reverse) {
		UVertexList vl=new UVertexList();
		
		if(edge==LEFT_EDGE) for(int i=0; i<u; i++) vl.add(grid[0][i]);
		if(edge==RIGHT_EDGE) for(int i=0; i<u; i++) vl.add(grid[u-1][i]);
		if(edge==TOP_EDGE) for(int i=0; i<v; i++) vl.add(grid[i][u-1]);
		if(edge==BOTTOM_EDGE) for(int i=0; i<v; i++) vl.add(grid[i][0]);
		
		if(reverse) vl.reverseOrder();
		vl.trimEmpty();
		return vl.v;
	}
	
	public void setEdge(int edge,UVec3 edgeVert[]) {
		if(edge==LEFT_EDGE) {
			for(int i=0; i<edgeVert.length; i++) grid[0][i]=edgeVert[i];
		}
		if(edge==RIGHT_EDGE) {
			for(int i=0; i<edgeVert.length; i++) grid[u-1][i]=edgeVert[i];
		}
		if(edge==TOP_EDGE) {
			for(int i=0; i<edgeVert.length; i++) grid[i][v-1]=edgeVert[i];
		}
		if(edge==BOTTOM_EDGE) {
			for(int i=0; i<edgeVert.length; i++) grid[i][0]=edgeVert[i];
		}
	}
}
