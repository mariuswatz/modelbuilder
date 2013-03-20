float bezCurve[]=new float[4];
float shaper[];
int shapeStyle=1,oldShaper=-1;

String shapeNames[]= {
  "NORMAL", "SQ", "POW()",
  "SIN()","SQ(SIN())","WAVE","WAVE-STEPS",
  "BEZIER"};
Slider shapeSlider;

float shapeP1,shapeP2,shapeP3,shapePsteps;

void rndShaper() {
  float rndval=random(8000)%8+1;
  shapeStyle=(int)rndval;
  println("shapeStyle "+shapeStyle+" "+rndval);

  // this next line will trigger a ControlP5.controlEvent()
  shapeSlider.setValue(shapeStyle); 
  
  initShaper();
}
 
void initShaper() { 
  oldShaper=shapeStyle;
  
  shapeP1=random(1);
  shapeP2=random(1);
  shapeP3=random(1);
  shapePsteps=(int)random(8,20);
  
  // initialize bezier shaper profile
  float maxBez=-100;
  for(int i=0; i<4; i++) {
    bezCurve[i]=random(1);
    maxBez=max(bezCurve[i],maxBez);
  }
  for(int i=0; i<4; i++) bezCurve[i]/=maxBez;
    
  shapeNames[2]="POW ( x, "+nf((1+shapeP1*3),0,2)+" )";
  shapeNames[3]="SIN ( x * "+nf(degrees(R*HALF_PI*shapeP1),0,2)+" )";
  shapeNames[4]="SQ ( SIN( "+nf(degrees(R*HALF_PI*shapeP1*2),0,2)+" )";
  
//  shapeSlider.setCaptionLabel("Shaping function: "+shapeNames[shapeStyle-1]);
}

// PRE-CALCULATE SHAPER PROFILE VALUES
void buildShapeProfile() { 
  float maxval=-1000;
  numSeg2=numSeg*2;

  shaper=new float[numSeg];
  for(int i=0; i<numSeg; i++) {
    float tt=(float)i/(float)(numSeg-1);
    shaper[i]=getShapeMod(1-tt);
    maxval=max(maxval,shaper[i]);
  }
  
  // NORMALIZE
//  println("shaper[i]/="+maxval);
  for(int i=0; i<numSeg; i++) shaper[i]/=maxval;
}
  
  
// CALCULATES SELECTED SHAPER FUNCTION OF NATURE F(T)=X
float getShapeMod(float R) {
  // SQUARE DECELERATION
  if(shapeStyle==2) return sq(R);
  
   // EXPONENTIAL
  if(shapeStyle==3) return pow(R,(1+shapeP1*3));  
  
   // SINE WAVE
  if(shapeStyle==4) return sin(R*HALF_PI*shapeP1);
  
  // SQUARE OF SINE
  if(shapeStyle==5) return sq(sin(R*PI*shapeP1*2)); 
  
  // MULTI SINE WAVE
  if(shapeStyle==6) { 
    float val=R*((float)((int)(1+shapeP1*12))*PI+HALF_PI)-HALF_PI;
    val=sin(val)*0.15+0.15+(0.5*R*R);
    return val;
  }
    
  // CHAOTIC SINE/COS WAVE
  if(shapeStyle==7) {
    float d1=(float)((int)(0.5+5*shapeP1))*PI;
    float d2=(float)((int)(0.5+6*shapeP2*1.5))*PI;
    float newR=R;
    
    float val=sin((1-R)*(d1+HALF_PI*shapeP3))*0.3+0.4;
    val+=cos((1-R)*d2)*0.1+0.1;
    val=sq(R)*0.2+val*0.8;

    return val;
  }
  if(shapeStyle==8) 
    return bezierPoint(bezCurve[0],bezCurve[1],bezCurve[2],bezCurve[3], R);
    
  // default when shapeStyle==1
  return R;
}

