package unlekker.data;

import java.util.ArrayList;

import unlekker.modelbuilder.UVec3;
import unlekker.util.*;

public class UPDB implements UConstants {
	public static String ATOM="ATOM";
	public ArrayList<UAtom> atoms;
	
	public UPDB(String filename) {
		parse(new UDataText().load(filename));
		
		
	}

	private void parse(UDataText txt) {
		String in=txt.parseGetLine();
		atoms=new ArrayList<UPDB.UAtom>();
		
		while(in!=null) {
			if(in.startsWith(ATOM)) {
				UAtom a=new UAtom(in);
				atoms.add(a);
				UUtil.log(atoms.size()+" "+a.toString());
		 	}
			in=txt.parseGetLine();
		}
		
	}
	
	
	class UAtom {
		String at,res;
		UVec3 v;
		
		UAtom(String in) {
	     at = in.substring(12, 15).trim();
	     res = in.substring(22, 26).trim();
	      v = new UVec3(
	      		UUtil.parseFloat(in.substring(30,37).trim()), 
	      		UUtil.parseFloat(in.substring(38,45).trim()),
	      		UUtil.parseFloat(in.substring(46,53).trim()));
		}

		public String toString() {
			return at+"|"+res+"|"+v.toString();
			
		}
	}
}
