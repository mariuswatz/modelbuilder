class Pt {
  float x,y,z;
  
  Pt(float _x,float _y,float _z) {
    set(_x,_y,_z);
    x=_x;
    y=_y;
    z=_z;
  }

  void set(float _x,float _y,float _z) {
    x=_x;
    y=_y;
    z=_z;
  }
  
  void add(float _x,float _y,float _z) {
    x=x+_x;
    y=y+_y;
    z=z+_z;
  }

}
