package unlekker.modelbuilder;

import processing.core.*;
import unlekker.util.*;

public class UPShapeTool implements UConstants {

	public PShape sh;
	public String name;
	public UGeometry geo;
	public int vln,vertn;
	public UBBox bb;
	public float w,d,h;
	
	private static int undefCnt=0;
	private int debugLevel=0;
	
	public UPShapeTool(PShape _sh) {
		sh=_sh;
		name=getName(sh);
		geo=new UGeometry();
		getVertices();
		logDivider("PShapeUtil done.");
	}
	
	public void getVertices() {
		logDivider(toString(sh));
		getVertices(sh);
		
		UUtil.log("UShapeTool.getVertices - "+geo.vln+" paths found.");
	}
	

	public void getVertices(PShape ss) {
		log("getVertices "+toString(ss));

		int family=ss.getFamily();
		log("family "+PShapeFamilyNames[family]);
		if (family == GROUP) getVertGroup(ss);
    else if (family == PRIMITIVE) getVertPrimitive(ss);
    else if (family == GEOMETRY) getVertGeometry(ss);
    else if (family == PATH) getVertPath(ss);

	}

	private UVec3 toUVec3(float fv[]) {
		return new UVec3(fv[0],fv[1]);
	}
	
	private void getVertPath(PShape ss) {
		int vn,vcn;
		
		vn=ss.getVertexCount();
		vcn=ss.getVertexCodeCount();
		
		log("\ngetVertPath "+toString(ss)+" vn="+vn+" vcn="+vcn);

		if(vcn>0) {
			UVertexList vl=new UVertexList();
			
			int id=0;
			UVec3 pos=new UVec3();
			UVertexList vnlist=new UVertexList();
			for(int i=0; i<vn; i++) {
				vnlist.add(toUVec3(ss.getVertex(i)));
//				vl.add(vnlist.v[vnlist.n-1]);
			}
			
			for(int i=0; i<vcn; i++) {
				log(i+" "+ss.getVertexCode(i));
				int vertcode=ss.getVertexCode(i);
				if(vertcode==VERTEX) {
					pos.set(vnlist.v[id++]);
					vl.add(pos);
					log(id+" VERTEX "+pos.toString()+" "+vl.n);
				}
				else if(vertcode==BREAK) {
					add(vl);
					vl=new UVertexList();
					log("BREAK "+geo.vln);
				}
				else if(vertcode==BEZIER_VERTEX) {
					log("BEZIER_VERTEX from "+pos.toString());
					UVertexList cp=new UVertexList().add(pos).
						add(vnlist.v[id++]).
						add(vnlist.v[id++]).
						add(vnlist.v[id++]);
					UBezier3D bez=new UBezier3D(cp).eval(PApplet.max(3,(int)(cp.getLength()/5)));
					
					log(id+"/"+vnlist.n+" "+cp.toString());
					pos.set(vnlist.v[id-1]);
					vl.add(bez.result);
				}
				else log("UNSUPPORTED - vertex code = "+vertcode);
			}
			if(vl.n>0) add(vl);
		}
		else if(vn>0) {
			UVertexList vl=new UVertexList();
			for(int i=0; i<vn; i++) vl.add(ss.getVertex(i),false);
			if(vl.n>0) add(vl);
		}
		

		
/*		if(ss.get.)
    if (vertices == null) return;

    g.beginShape();

    if (vertexCodeCount == 0) {  // each point is a simple vertex
      if (vertices[0].length == 2) {  // drawing 2D vertices
        for (int i = 0; i < vertexCount; i++) {
          g.vertex(vertices[i][X], vertices[i][Y]);
        }
      } else {  // drawing 3D vertices
        for (int i = 0; i < vertexCount; i++) {
          g.vertex(vertices[i][X], vertices[i][Y], vertices[i][Z]);
        }
      }

    } else {  // coded set of vertices
      int index = 0;

      if (vertices[0].length == 2) {  // drawing a 2D path
        for (int j = 0; j < vertexCodeCount; j++) {
          switch (vertexCodes[j]) {

          case VERTEX:
            g.vertex(vertices[index][X], vertices[index][Y]);
            index++;
            break;

          case BEZIER_VERTEX:
            g.bezierVertex(vertices[index+0][X], vertices[index+0][Y],
                           vertices[index+1][X], vertices[index+1][Y],
                           vertices[index+2][X], vertices[index+2][Y]);
            index += 3;
            break;

          case CURVE_VERTEX:
            g.curveVertex(vertices[index][X], vertices[index][Y]);
            index++;

          case BREAK:
            g.breakShape();
          }
        }
      } else {  // drawing a 3D path
        for (int j = 0; j < vertexCodeCount; j++) {
          switch (vertexCodes[j]) {

          case VERTEX:
            g.vertex(vertices[index][X], vertices[index][Y], vertices[index][Z]);
            index++;
            break;

          case BEZIER_VERTEX:
            g.bezierVertex(vertices[index+0][X], vertices[index+0][Y], vertices[index+0][Z],
                           vertices[index+1][X], vertices[index+1][Y], vertices[index+1][Z],
                           vertices[index+2][X], vertices[index+2][Y], vertices[index+2][Z]);
            index += 3;
            break;

          case CURVE_VERTEX:
            g.curveVertex(vertices[index][X], vertices[index][Y], vertices[index][Z]);
            index++;

          case BREAK:
            g.breakShape();
          }
        }
      }
    }
    g.endShape(close ? CLOSE : OPEN);	*/
    }

	private void getVertGeometry(PShape ss) {
		int type=ss.getPrimitive();
		log("getVertGeometry "+toString(ss)+" "+UUtil.shapeType(type));
		log("Vert "+ss.getVertexCount());
	}

	private void getVertPrimitive(PShape ss) {
		UVertexList vert=new UVertexList(); 
		float [] params=ss.getParams();
		int pcnt=params.length;

		int type=ss.getPrimitive();
		log("getVertPrimitive "+toString(ss)+" "+UUtil.shapeType(type));
		log("params "+UUtil.toString(params));
		
		if(type==LINE) {
			if(pcnt==4) {
				vert.add(params[0], params[1]);
				vert.add(params[2], params[3]);
			}
			else if(pcnt==4) {
				vert.add(params[0], params[1],params[2]);
				vert.add(params[3],params[4], params[5]);
			}
		}		
		else if (type == QUAD) vert.addQuad(params);
		else if (type == RECT) {
			float q[]={params[0],params[1],
					params[0]+params[2],params[1],
					params[0]+params[2],params[1]+params[3],
					params[0],params[1]+params[3]
			};
			vert.addQuad(q);
			
		}
		
		if(vert.n>0) add(vert);
	}

	private void getVertGroup(PShape ss) {
		log("getVertGroup "+toString(ss));
		for(int i=0; i<ss.getChildCount(); i++) {
			PShape child=ss.getChild(i);
			log(i+" "+PShapeFamilyNames[child.getFamily()]);
			getVertices(child);
		}
		
	}
	

	private String getName(PShape ss) {
		// TODO Auto-generated method stub
		String name=ss.getName();
		if(name==null) {
			name="Undef-"+(undefCnt++);
			ss.setName(name);
		}
		return name;
	}

	void add(UVertexList _vl) {
		geo.addVertexList(_vl);
	}
	
	public String toString(PShape ss) {
		return "["+getName(ss)+" "+
			PShapeFamilyNames[ss.getFamily()]+" "+
			ss.getChildCount()+"]";
		
	}
	
	private void log(String s) {
		if(debugLevel>0) UUtil.log(s);
	}
	
	private void logDivider(String s) {
		if(debugLevel>0) UUtil.logDivider(s);
	}

	public void draw(PApplet p) {
		geo.drawVertexLists(p);
		
	}

}
