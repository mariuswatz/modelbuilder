/*
 * The code for this progressive TGA output is all thanks
 * to Dave Bollinger (http://www.davebollinger.com/), who
 * once upon a (pre-GitHub) time took it upon himself to 
 * improve my TileSaver code. 
 * 
 * Outputting a RLE-coded TGA file row by row allows for 
 * a drastic increase in maximum output resolution, 
 * I've done 20k x 20k but even higher should be possible.
 * 
 *  -marius watz, March 2013
 */

package unlekker.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import processing.core.*;

public class UTGAProgressive {
  PImage row,tileImg;
  public int tileN,tileCnt,width,height;
  public PApplet p;
  public String filename;
  
  public UTGAProgressive(PApplet p,int tileN,String filename) {
  	this.p=p;
  	this.tileN=tileN;
  	this.filename=filename;
  	
  	width=p.width;
  	height=p.height;
    tileImg=new PImage(p.width*tileN, p.height);

  }
  
  // DAVE:  added tga stuff below, stolen from PApplet and PImage
  
  BufferedOutputStream bos=null;

  public void add(PImage tile) {
    // Find image ID from reverse row order
    // DAVE:  in forward row order
    int imgid = tileCnt;
    int idx=(imgid%tileN);
//    int idy=(imgid/tileNum);
 
    // Get current image from sketch and draw it into buffer
    tile.loadPixels();
    tileImg.set(idx*width, 0, tile.get()); // DAVE: always output to y=0
    
    // DAVE: time to stream output image?
    if (idx==tileN-1) {
    	tileImg.loadPixels();
    	
    	appendTGA(tileImg.pixels);
    }  
    
    tileCnt++;
    if(tileCnt==tileN*tileN) finishTGA();
  }
  
  void startTGA() {
    try {
      UUtil.log("pathfilename = " + p.savePath(filename));
      File file = new File(filename);
      bos = new BufferedOutputStream(new FileOutputStream(file), 32768);
      byte header[] = new byte[18];
      header[2] = 0x0A;
      header[16] = 24;
      header[17] = 0x20;
      header[12] = (byte) ((int)(width*tileN) & 0xff);
      header[13] = (byte) ((int)(width*tileN) >> 8);
      header[14] = (byte) ((int)(height*tileN) & 0xff);
      header[15] = (byte) ((int)(height*tileN) >> 8);
      bos.write(header);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void appendTGA(int [] pixels) {
  	
    // DAVE: start streaming output image
  	//    tileFilename+="_"+(p.width*tileN)+"x"+
  	//            (p.height*tileN)+tileFileextension;
  	if(bos==null) startTGA();

    try {
      int maxLen = (int)(height * width * tileN);
      int index = 0;
      int col;
      int[] currChunk = new int[128];
      while (index < maxLen) {
        boolean isRLE = false;
        currChunk[0] = col = pixels[index];
        int rle = 1;
        // try to find repeating bytes (min. len = 2 pixels)
        // maximum chunk size is 128 pixels
        while (index + rle < maxLen) {
          if (col != pixels[index + rle] || rle == 128) {
            isRLE = (rle > 1); // set flag for RLE chunk
            break;
          }
          rle++;
        }
        if (isRLE) {
          bos.write(128 | (rle - 1));
          bos.write(col & 0xff);
          bos.write(col >> 8 & 0xff);
          bos.write(col >> 16 & 0xff);
        } else {  // not RLE
          rle = 1;
          while (index + rle < maxLen) {
            if ((col != pixels[index + rle] && rle < 128) || rle < 3) {
              currChunk[rle] = col = pixels[index + rle];
            } else {
              // check if the exit condition was the start of
              // a repeating colour
              if (col == pixels[index + rle]) rle -= 2;
              break;
            }
            rle++;
          }
          // write uncompressed chunk
          bos.write(rle - 1);
          for (int i = 0; i < rle; i++) {
            col = currChunk[i];
            bos.write(col & 0xff);
            bos.write(col >> 8 & 0xff);
            bos.write(col >> 16 & 0xff);
          }
        }
        index += rle;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void finishTGA() {
    try {
    	UUtil.log("Finish TGA output - "+width*tileN+" x "+height*tileN);
      bos.flush();
      bos.close();
      bos=null;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
 
	
}
