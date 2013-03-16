package unlekker.util;

import unlekker.util.*;
//////////////////////////////////////////////
// FFT calculation code by Karsten Schmidt aka Toxi

public class UFFT implements UConstants {
  int WINDOW_SIZE,WS2;
  int BIT_LEN;
  int[] _bitrevtable;
  float _normF;
  float[] _equalize;
  float[] _envelope;
  float[] _fft_result;
  float[][] _fftBuffer;
  float[] _cosLUT,_sinLUT;
  float[] _FIRCoeffs;
  boolean _isEqualized, _hasEnvelope;

  // FFT DATA FRAMEs 
  public static int FFTFRAMES=0,BANDFRAMES=1;
  public float fftFrame[][],fftBandFrame[][];
  public int numFrames=0;
  public boolean doRecord=false;
  
  // FFT DAMPING DATA
  public float max,spectrum[],bands[];
  public int fftSize,numBands,bandStep;
  private UDataDamper fftdamp,banddamp;
  private boolean useBands=false;
  private float bandsRaw[],tmpData[];

  public UFFT(int _fftSize,int _bands,float _damp,
  		boolean _equalize,float _env) {
  	this(_fftSize,_bands);
  	useDamping(_damp);
  	useEqualizer(_equalize);
    if(_env!=-1) useEnvelope(true,_env);
  }
    
  public UFFT(int windowSize) {
  	this(windowSize,-1);
  }

  public UFFT(int windowSize,int _bandNum) {
    if(_bandNum>0) useBands(_bandNum);
        
    WINDOW_SIZE=windowSize*2;
    WS2=windowSize;
    fftSize=windowSize;

  	useBands(_bandNum);

    //WS2>>=1;
    BIT_LEN = (int)(Math.log((double)WINDOW_SIZE)/0.693147180559945+0.5);
    _normF=2f/WINDOW_SIZE;
    _hasEnvelope=false;
    _isEqualized=false;
    initFFTtables();

//    Util.log("FFT size: "+WINDOW_SIZE+" "+WS2+", spectrum bands: "+_bandNum);
  }

  ////////////////////////////////////
  // MW ADDITION - USE DAMPING
  
  public void useBands(int _n) {
  	useBands=true;
  	numBands=_n;
  	bandStep=WS2/numBands;
  	bandsRaw=new float[numBands];
  	bands=new float[numBands];
  }
  
  public void useDamping(float dampval) {
  	useDamping(dampval,100,1000);
  }
  
  public void useDamping(float dampval,float low,float high) {
  	if(fftdamp==null) {
  		fftdamp=new UDataDamper(WS2);
  		banddamp=new UDataDamper(numBands);
  	}
//  	Util.log(dampval+" "+low+" "+high);
  	setDamping(dampval,low,high);
  }
  
  public void setDamping(float dampval) {
		fftdamp.setDamping(dampval);
  }

  public float getDamping() {
		return (float)fftdamp.dampFactor;
  }

  public void setDamping(float dampval,float low,float high) {
  	if(fftdamp==null) useDamping(dampval,low,high);
		fftdamp.setDamping(dampval);
		fftdamp.setLimits(low, high);
		banddamp.setDamping(dampval);
		banddamp.setLimits(low, high);		
  }
  
  public void update(float [] data) {
  	if(useBands) {
  		int cnt=0;
  		for(int i=0; i<numBands; i++) {
  			bandsRaw[i]=0;
  			for(int j=0; j<bandStep; j++) bandsRaw[i]+=data[cnt++];
  			bandsRaw[i]/=(float)bandStep;
//  			for(int j=0; j<bandStep; j++) Util.log(i+" "+bandsRaw[i]);
  		}

  		if(banddamp!=null) {
  			banddamp.update(bandsRaw);
  			for(int i=0; i<numBands; i++) bands[i]=banddamp.val[i];
  		}
  		else 
  			for(int i=0; i<numBands; i++) bands[i]=bandsRaw[i];
  	}
  	
    if(fftdamp!=null) {
    	fftdamp.update(data);
    	spectrum=fftdamp.val;
    	max=fftdamp.max;
    }
    else System.arraycopy(_fft_result, 0, spectrum, 0, fftSize);
    
    if(doRecord) recordFrame();
  }
  
  
  ////////////////////////////////////
  // MW ADDITION - RECORD DATA FRAMES
  
  public void useRecording(boolean _record) {
  	doRecord=_record;
  	if(doRecord) {
			numFrames=0;

  		if(fftFrame==null) {
  			fftFrame=new float[100][fftSize];
  			fftBandFrame=new float[100][numBands];
  		}
  	}
  }

  public void recordFrame() {
  	if(fftFrame.length==numFrames) {
  		float [][] tmp=new float[numFrames*2][fftSize];
  		for(int i=0; i<numFrames; i++) tmp[i]=fftFrame[i];
  		fftFrame=tmp;

  		float [][] tmpBands=new float[numFrames*2][numBands];
  		for(int i=0; i<numFrames; i++) tmpBands[i]=fftBandFrame[i];
  		fftBandFrame=tmpBands;
  	}
  	
  	System.arraycopy(spectrum, 0, fftFrame[numFrames], 0, fftSize);
  	System.arraycopy(bands, 0, fftBandFrame[numFrames], 0, numBands);
  	numFrames++;
  }
  
  public float[][] getFrames(int type) {
  	float[][] tmp;
  	if(type==FFTFRAMES) {
  		tmp=new float[numFrames][fftSize];
  		for(int i=0; i<numFrames; i++) tmp[i]=fftFrame[i];
  	}
  	else {
  		tmp=new float[numFrames][numBands];
  		for(int i=0; i<numFrames; i++) tmp[i]=fftBandFrame[i];
  	}

		return tmp;
  }
  
  ////////////////////////////////////
  // MW ADDITION - USE DAMPING

  void initFFTtables() {
    _cosLUT=new float[BIT_LEN];
    _sinLUT=new float[BIT_LEN];
    _fftBuffer=new float[WINDOW_SIZE][2];
    _fft_result=new float[WS2];
    
    spectrum=new float[WS2]; // MW


    // only need to compute (float)Math.sin/Math.cos at BIT_LEN angles
    float phi=PI;
    for(int i=0; i<BIT_LEN; i++) {
      _cosLUT[i]=(float)Math.cos(phi);
      _sinLUT[i]=(float)Math.sin(phi);
      phi*=0.5;
    }


    // precalc bit reversal lookup table ala nullsoft
    int i,j,bitm,temp;
    _bitrevtable = new int[WINDOW_SIZE];


    for (i=0; i<WINDOW_SIZE; i++) _bitrevtable[i] = i;
    for (i=0,j=0; i < WINDOW_SIZE; i++) {
      if (j > i) {
        temp = _bitrevtable[i];
        _bitrevtable[i] = _bitrevtable[j];
        _bitrevtable[j] = temp;
      }
      bitm = WS2;
      while (bitm >= 1 && j >= bitm) {
        j -= bitm;
        bitm >>= 1;
      }
      j += bitm;
    }
  }


  // taken from nullsoft VMS
  // reduces impact of bassy freqs and slightly amplifies top range
  public void useEqualizer(boolean on) {
    _isEqualized=on;
    if (on) {
      int i;
      float scaling = -0.02f;
      float inv_half_nfreq = 1.0f/WS2;
      _equalize = new float[WS2];
      for (i=0; i<WS2; i++) _equalize[i] = scaling * (float)Math.log( (double)(WS2-i)*inv_half_nfreq );
    }
  }


  // bell filter envelope to reduce artefacts caused by the edges of standard filter rect
  // 0.0 < power < 2.0
  public void useEnvelope(boolean on, float power) {
    _hasEnvelope=on;
    if (on) {
      int i;
      float mult = 1.0f/(float)WINDOW_SIZE * TWO_PI;
      _envelope = new float[WINDOW_SIZE];
      if (power==1.0f) {
        for (i=0; i<WINDOW_SIZE; i++) _envelope[i] = 0.5f + 0.5f*(float)Math.sin(i*mult - HALF_PI);
      } else {
        for (i=0; i<WINDOW_SIZE; i++) _envelope[i] = (float)Math.pow(0.5f + 0.5f*(float)Math.sin(i*mult - HALF_PI), power);
      }
    }
  }

  void computeFFT(float[] waveInData,int offs) {
  	if(tmpData==null) tmpData=new float[WINDOW_SIZE];
  	System.arraycopy(waveInData, offs, tmpData, 0, WINDOW_SIZE);
  	
  	computeFFT(tmpData);
  }
  
  // compute actual FFT with current settings (eq/filter etc.)
  public void computeFFT(float[] waveInData) {
    float  u_r,u_i, w_r,w_i, t_r,t_i;
    int    l, le, le2, j, jj, ip, ip1, i, ii, phi;


    // ensure that we don't overwrite old results - Watz 21.03.05
//    _fft_result=new float[WS2];

    // check if we need to apply window function or not
    if (_hasEnvelope) {
      for (i=0; i<WINDOW_SIZE; i++) {
        int idx = _bitrevtable[i];
        if (idx < WINDOW_SIZE) _fftBuffer[i][0] = waveInData[idx]*_envelope[idx];
        else  _fftBuffer[i][0] = 0;
        _fftBuffer[i][1] = 0;
      }
    } else {
      for (i=0; i<WINDOW_SIZE; i++) {
        int idx = _bitrevtable[i];
        if (idx < WINDOW_SIZE) _fftBuffer[i][0] = waveInData[idx];
        else  _fftBuffer[i][0] = 0;
        _fftBuffer[i][1] = 0;
      }
    }

    for (l = 1,le=2, phi=0; l <= BIT_LEN; l++) {
      le2 = le >> 1;
      w_r = _cosLUT[phi];
      w_i = _sinLUT[phi++];
      u_r = 1f;
      u_i = 0f;
      for (j = 1; j <= le2; j++) {
        for (i = j; i <= WINDOW_SIZE; i += le) {
          ip  = i + le2;
          ip1 = ip-1;
          ii  = i-1;
          t_r = _fftBuffer[ip1][0] * u_r - u_i * _fftBuffer[ip1][1];
          t_i = _fftBuffer[ip1][1] * u_r + u_i * _fftBuffer[ip1][0];
          _fftBuffer[ip1][0] = _fftBuffer[ii][0] - t_r;
          _fftBuffer[ip1][1] = _fftBuffer[ii][1] - t_i;
          _fftBuffer[ii][0] += t_r;
          _fftBuffer[ii][1] += t_i;
        }
        t_r = u_r * w_r - w_i * u_i;
        u_i = w_r * u_i + w_i * u_r;
        u_r = t_r;
      }
      le<<=1;
    }
    // normalize bands or apply EQ
    float[] currBin;
    if (_isEqualized) {
      for(i=0; i<WS2; i++) {
        currBin=_fftBuffer[i];
        _fft_result[i]=_equalize[i]*(float)Math.sqrt(currBin[0]*currBin[0]+currBin[1]*currBin[1]);
      }
    } else {
      for(i=0; i<WS2; i++) {
        currBin=_fftBuffer[i];
        _fft_result[i]=_normF*(float)Math.sqrt(currBin[0]*currBin[0]+currBin[1]*currBin[1]);
      }
    }

    // MW ADDITION - USE DAMPING
    update(_fft_result);
    
  }
}
