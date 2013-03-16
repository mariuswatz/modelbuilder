package unlekker.moviemaker;

import static org.monte.media.VideoFormatKeys.*;
import processing.core.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;

import org.monte.media.*;
import org.monte.media.math.Rational;
import org.monte.media.quicktime.*;
import org.monte.moviemaker.MovieMakerMain;


/**
 * Simple alternative to the now-defunct MovieMaker class by Dan Shiffman, using the  UMovieMaker is used similar to Moviee  
 * @author Marius Watz
 *
 */
public class UMovieMaker {

  QuickTimeWriter qt=null;

  public int frameCount=0, vt, w, h;

//  Buffer buf;

  Graphics2D gg=null;

  Format videoFormat;

  BufferedImage img=null;

  BufferedImage prevImg=null;

  int[] data=null;

  int[] prevData=null;

  int prevImgDuration=0;

  int duration=100;

  PApplet p;

  public UMovieMaker(PApplet p, String filename, int w, int h, int fps) {
    this.p=p;
    try {
      this.w=w;
      this.h=h;
      qt=new QuickTimeWriter(new File(filename));

      Format format=new Format(
          EncodingKey, ENCODING_QUICKTIME_PNG, 
          DepthKey,24);
      format=format.prepend(
          MediaTypeKey, MediaType.VIDEO, //
          FrameRateKey, new Rational(fps, 1),//
          WidthKey, w, //
          HeightKey, h);

      qt.addTrack(format);

//      buf=new Buffer();
      img=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      
      data=((DataBufferInt)img.getRaster().getDataBuffer()).getData();
      prevImg=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      prevData=((DataBufferInt)prevImg.getRaster().getDataBuffer()).getData();
      
      gg=img.createGraphics();
      System.out.println("vt "+vt+" "+img.getWidth());
      gg.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addFrame() {
    try {
      BufferedImage frame=(BufferedImage)p.g.getImage();
      gg.drawImage(frame, 0, 0, w, h, null);
      if (frameCount!=0&&Arrays.equals(data, prevData)) {
        prevImgDuration+=duration;
      } else {
        if (prevImgDuration!=0) {
          qt.write(vt, prevImg, 1);
        }
        prevImgDuration=duration;
        System.arraycopy(data, 0, prevData, 0, data.length);
      }

      if ((frameCount++)%1==0) System.out.println(nf(frameCount, 4)+" "+frameCount);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void finish() {
    try {
      qt.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private String nf(int frames, int i) {
    int n=i-new String(""+frames).length();
    if (n==1) return "0"+frames;
    if (n==2) return "00"+frames;
    if (n==3) return "000"+frames;
    if (n==4) return "0000"+frames;
    if (n==5) return "00000"+frames;
    if (n==6) return "000000"+frames;
    if (n==7) return "0000000"+frames;
    if (n==8) return "00000000"+frames;
    if (n==9) return "000000000"+frames;
    if (n==10) return "0000000000"+frames;

    return null;
  }
}