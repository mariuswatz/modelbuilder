package unlekker.test;

import javax.media.opengl.GL;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class ModelBuilderTest extends PApplet {
	UGeometry primitives,shapes;
	UNav3D nav;
	UGeometry struct;
	
	public void setup() {
		size(1000,1000,OPENGL);
		buildPrimitives();
//		buildShapes();
		nav=new UNav3D(this);
		nav.setTranslation(width/2,height/2,0);
	}
	
	public void draw() {
		background(0);
		lights();
		nav.doTransforms();
		GL gl = ((PGraphicsOpenGL)g).gl;
//	  gl.glEnable(GL.GL_CULL_FACE);

		
		translate(-200,0,0);
		primitives.draw(this);

		translate(400,0,0);
//		shapes.draw(this);
		
		translate(0,200,0);
//		struct.draw(this);
	}

	public void buildPrimitives() {
		UVertexList vl,vl2[];
		
		// PRIMITIVES
		UUtil.log("PRIMITIVES");
		primitives=new UGeometry().add(UPrimitive.box(50, 100, 100));
		UUtil.log("faces: "+primitives.faceNum+" quads: "+
				primitives.quadNum+" strips: "+primitives.stripNum);
		UUtil.log("----------");
		primitives.add(UPrimitive.box(30, 30, 30).translate(0, 100,0));
		UUtil.log("faces: "+primitives.faceNum+" quads: "+
			primitives.quadNum+" strips: "+primitives.stripNum);
		UUtil.log("----------");
		
		primitives.add(UPrimitive.cylinder(50, 100, 12, true).translate(125*1, 0, 0));
		UUtil.log("faces: "+primitives.faceNum+" quads: "+
				primitives.quadNum+" strips: "+primitives.stripNum);
		UUtil.log("----------");
		primitives.add(UPrimitive.sphere(50, 12).translate(125*2, 0, 0));
		UUtil.log("----------");
		primitives.add(UPrimitive.cone(50, 10,100,36,true).translate(125*3, 0, 0));
		primitives.add(UPrimitive.disc(50, 36).translate(125*4, 0, 0));
		primitives.add(UPrimitive.rect(50, 100).translate(125*5, 0, 0));

		primitives.add(UPrimitive.box(30, 30, 30).translate(125*6-30,-50,-50));
		primitives.add(UPrimitive.box(30, 30, 30).translate(125*6+30,50,50));
		primitives.add(UPrimitive.box2Points(new UVec3(125*6-30,-50,-50), new UVec3(125*6+30,50,50), 20));
		primitives.calcBounds();
		primitives.center().writeSTL(this, "ModelBuilder-primitives.stl");
		
	}

	public void buildShapes() {
		UVertexList vl,vl2[];
		
		shapes=new UGeometry();
		shapes.beginShape(shapes.QUAD_STRIP);
		for(int i=0; i<20; i++) {
			shapes.vertex(i*50,(i%2)*50-25,25);
			shapes.vertex(i*50,(i%2)*50-25,-25);
		}
		shapes.endShape().translate(0, 100, 0);
		UUtil.log(""+shapes.faceNum);
		
		vl=new UVertexList();		
		for(int i=0; i<20; i++) {
			vl.add(i*50,(i%2)*100-25,25);
			vl.add(i*50,(i%2)*100-25,-25);
		}
		shapes.quadStrip(vl.translate(0, -125, 00));
		UUtil.log("vl "+vl.n+" "+shapes.faceNum);

		// populate array of vertex lists
		vl2=new UVertexList[6];
		vl2[0]=new UVertexList();		
		for(int i=0; i<20; i++) vl2[0].add(i*50,(i%2)*25+25,0);
		vl2[0].rotateX(-HALF_PI/2);
		
		for(int i=0; i<vl2.length; i++)
			vl2[i]=new UVertexList(vl2[0]).rotateX(-HALF_PI*(float)i).translate(0, -125*2,0);

		// make series of quad strips using arrays of vertex lists
		shapes.quadStrip(vl2);
		
		// close the form with a quad strip connecting the last and first vertex lists
		// in the array
		shapes.quadStrip(vl2[vl2.length-1], vl2[0]);
		
		UUtil.log("vl "+vl.n+" "+shapes.faceNum);
		
		shapes.center().writeSTL(this, "ModelBuilder-shapes.stl");
		
		
	  UVertexList vlStruct[]=new UVertexList[3];
	  for(int i=0; i<vlStruct.length; i++) vlStruct[i]=new UVertexList();
	  
	  for(int i=0; i<37; i++) {
	    float r=random(150,250);
	    vlStruct[0].add(cos(radians(i*10))*r,sin(radians(i*10))*r,0);
	    vlStruct[1].add(cos(radians(i*10))*r,sin(radians(i*10))*r,50);
	    vlStruct[2].add(cos(radians(i*10))*r*0.75f,sin(radians(i*10))*r*0.75f,100);
	  }
	  for(int i=0; i<vlStruct.length; i++) vlStruct[i].close();
	  
	  
	  struct=new UGeometry();
	  struct.quadStrip(vlStruct[0],vlStruct[1]);
	  struct.quadStrip(vlStruct[1],vlStruct[2]);
	  
	  struct.triangleFan(vlStruct[0],true,false);
	  struct.triangleFan(vlStruct[2],true,true);
	  
	  struct.center().writeSTL(this, "ModelBuilder-structure.stl");

	}


  static public void main(String args[]) {
    PApplet.main(new String[] { "unlekker.test.ModelBuilderTest" }); 
  }

}
