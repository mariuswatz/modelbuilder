float data[][];
float maxDataVal=-10000;
int numRow, numCol;
float maxDataValVol=-10000;
void loadStockData() {
  UDataText txt=
    UDataText.loadFile(dataPath("Google2008.csv"));

  txt.convertDelimiter(",");

  numRow=txt.numStr-1;
  txt.parseString();
  numCol=txt.numToken-1;
  //  println("numRow "+numRow+" numCol "+numCol+" "
  //    +txt.str[0]);

  data=new float[numCol][numRow];
  for (int i=0; i<numRow; i++) {
    txt.parseString();

    txt.getString(); // throw away date column
    for (int j=0; j<numCol; j++) {
      // get correct column data
      data[j][i]=txt.getFloat();
      //      println(i+","+j+" "+data[j][i]);
    }

    maxDataVal=max(maxDataVal, data[3][i]);
    maxDataValVol= max(maxDataValVol, data[4][i]);
  } 

  // normalize closing value
  for (int i=0; i<numRow; i++) {
    data[3][i]=data[3][i]/maxDataVal;
    data[4][i]=data[4][i]/maxDataValVol;
  }
}

