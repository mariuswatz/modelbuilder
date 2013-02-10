package unlekker.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.*;

public class UJarLister {
	public String path,packageName;
	public JarFile jar;
	public PackageMap packages;

	public UJarLister(String path) {
		this.path=path;
	}
	
	public void list(String packageName) {
		this.packageName=packageName.replace('.','/');
		packages=new PackageMap();
		
		ArrayList<String> classes=new ArrayList<String>();
		try {
			JarFile jar=new JarFile(path);
			Enumeration<JarEntry> entries=jar.entries();
			for (Enumeration<JarEntry> e = jar.entries() ; e.hasMoreElements() ;) {
				String name=e.nextElement().getName();
        if(name.startsWith(this.packageName) && name.indexOf('$')==-1) 
        	packages.addClass(name);
//        else System.out.println(name);
			}
			
			UUtil.log(packages.toString());
//			Collections.sort(classes);
//			UUtil.log(classes.toArray(new String[classes.size()]));
//			UUtil.log(getClassByName(classes.get(0)).getName());

		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Class getClassByName(String name) {
		try {
			return Class.forName(name.replaceAll("/", "\\.").
				substring(0, name.length() - 6));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void listClasses() {
		ArrayList<String> data=new ArrayList<String>();
		
//		try {
//			JarFile jar=new JarFile("deploy/modelbuilder.jar");
//			Enumeration<JarEntry> entries=jar.entries();
//			for (Enumeration<JarEntry> e = jar.entries() ; e.hasMoreElements() ;) {
//				JarEntry ent=e.nextElement();
//        System.out.println();
//    		if(ent.getName().endsWith("class") && ent.getName().startsWith("unlekker")) 
//    				data.add(
//    						listClass(Class.forName(ent.getName().
//    								replaceAll("/", "\\.").
//    								substring(0, ent.getName().length() - 6))));
//			}
//			
//			
//			for(int i=0; )
//			saveStrings("classList.html", data.toArray(new String[data.size()]));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
	}

	private String listClass(Class theClass) {
		String txt="";
		UUtil.logDivider(theClass.getName());
		Method meth[]=theClass.getMethods();
    for (Method method : meth) {
    	String s=method.getReturnType().getSimpleName()+" "+method.getName();
    	
        Class[] paramTypes = method.getParameterTypes();
        String param="";
        for (Class c : paramTypes) {
        	if(param.length()>0) param+=", "+c.getSimpleName();
        	else param+=c.getSimpleName();
        }

        s=s+"("+param+")";
        txt+="<li>"+s+"</li>\n";
    }
    
    return "<div class='classlist'><h3>"+theClass.getName()+"</h3>\n"+
    	"<ul>"+txt+"</ul>\n</div>\n\n";
	}
	
	class PackageMap extends HashMap<String, PackageList>{
		
		private ArrayList<PackageList> sortedtags;

		public void addClass(String name) {
			String packageName;
			if(name.endsWith("class")) name=name.substring(0,name.length()-6);
			name=name.replace('/', '.');
			int pos=name.lastIndexOf('.');
			packageName=(pos>-1) ? name.substring(0,pos) : "default";
			UUtil.log(name+" "+pos+" "+packageName);
			PackageList list=get(packageName);
			if(list==null) {
				list=new PackageList(packageName);
				list.add(name);
				put(packageName,list);
			}
			else list.add(name);
		}
		
	}
	
	class PackageList {
		String name;
		ArrayList<Class> classes;
		
		public PackageList(String name) {
			this.name=name;
			classes=new ArrayList<Class>();
		}
		
		public void add(String className) {
			classes.add(getClassByName(className));
			UUtil.log("classes "+classes.size());
		}
		
		public String toString() {
			return name+": "+classes.toString();
		}
	}
}
