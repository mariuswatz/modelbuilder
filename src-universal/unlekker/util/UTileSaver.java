package unlekker.util;

import java.io.File;

import processing.core.*;
import processing.core.PGraphics;
import processing.opengl.*;
import unlekker.modelbuilder.*;

public class UTileSaver {
	private PApplet p;
	private UProgressInfo progress;
	public PImage buffer;
	public int numTiles,numTilesTotal,tile,frameCount=-1;
	public long start;
	public boolean isTiling;
	public String filename,saveFormat,savePath;
	public double aspect,tileSize;
	public float near,far,frameRate; 
	public UVec3 offsets[],tileSizeV;
	
	public UTileSaver(PApplet _p,int num,String _filename) {
		p=_p;
		filename=p.savePath(_filename);
	  p.println(filename+" "+new File(filename).isAbsolute());
		tilingSetup(num);
	  isTiling=true;
	}

	public UTileSaver(PApplet _p,int num,String _saveFormat,String _savePath) {
		p=_p;
		saveFormat=_saveFormat;
		savePath=_savePath;
		savePath=UIO.getIncrementalFolder(savePath+"/"+saveFormat);
		UUtil.log(saveFormat+" savePath "+savePath);
		
		tilingSetup(num);
		start=System.currentTimeMillis();
	}
	
	public void nextFrame() {
		frameCount++;
		if(frameCount>0) {
			frameRate=1000f/(float)(System.currentTimeMillis()-start);
			start=System.currentTimeMillis();
		}
		
	  isTiling=true;
	  tile=0;
	  filename=UIO.getIncrementalFilename(saveFormat+".tga", savePath);
	}

	public void tilingSetup(int num) {
		numTiles=num;
		numTilesTotal=numTiles*numTiles;

		tile=0;
		
		buffer=p.createImage(p.width*numTiles,p.height*numTiles,p.ARGB);
		aspect=(double)p.height/(double)p.width;
		tileSize=2.0/(double)numTiles;
		
		near=0.01f;
		far=10000f;
		
		offsets = new UVec3[numTiles * numTiles];

		near=((PGraphics3D)p.g).cameraNear;
		far=((PGraphics3D)p.g).cameraFar;
		
    double cameraFOV = ((PGraphics3D)p.g).cameraFOV; //fov;
    double cameraNear = near;
    double cameraFar = far;
    double top = Math.tan(cameraFOV * 0.5) * cameraNear;
    double bottom = -top;
    double left = aspect * bottom;
    double right = aspect * top;
    
    int idx = 0;
    tileSizeV = new UVec3((float) (2 * right / numTiles), (float) (2 * top / numTiles));
    double y = top - tileSizeV.y;
    while (idx < offsets.length) {
			double x = left;
			for (int xi = 0; xi < numTiles; xi++) {
				offsets[idx++] = new UVec3((float) x, (float) y);
			  x += tileSizeV.x;
			}
			y -= tileSizeV.y;
    }
	}
	
	public void pre() {
		if(isTiling) {
			if(tile==0) progress=new UProgressInfo();
			
			p.camera(p.width/2.0f, p.height/2.0f, ((PGraphics3D)p.g).cameraZ,
					p.width/2.0f, p.height/2.0f, 0, 0, 1, 0);
			
      UVec3 o=offsets[tile];
      p.frustum(o.x, o.x+tileSizeV.x, 
      		(float) (o.y * aspect),
      		(float) (aspect*(o.y + tileSizeV.y)), 
      		near,far);
		}
	}
	
	public void post() {
		if(isTiling) {
			int x=tile%numTiles;
			int y=tile/numTiles;
			p.loadPixels();
			
			buffer.set(x*p.width, y*p.height, p.g);
			UUtil.log(progress.update(p, 
					100f*((float)tile/(float)(numTilesTotal))));
						
			tile++;
			
			if(tile==numTilesTotal) {
				isTiling=false;
				UUtil.log("Tiling done.");
				UUtil.log("Saving to '"+UIO.noPath(filename)+"'");
				buffer.save(filename);

				resetCamera();
			}
		}
	}

	public void resetCamera() {
    float mod=1f/10f;

		p.camera(p.width/2.0f, p.height/2.0f, ((PGraphics3D)p.g).cameraZ,
				p.width/2.0f, p.height/2.0f, 0, 0, 1, 0);
		  p.frustum(-(p.width/2)*mod, (p.width/2)*mod,
		    -(p.height/2)*mod, (p.height/2)*mod,
		    near,far);
	}
}
