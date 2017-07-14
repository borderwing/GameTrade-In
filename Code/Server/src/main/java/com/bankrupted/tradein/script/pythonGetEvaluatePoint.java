package com.bankrupted.tradein.script;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.Properties;

/**
 * Created by homepppp on 2017/7/7.
 */
public class pythonGetEvaluatePoint {
    public static String getPoints(String title,String platform){
        Properties props=new Properties();
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site","false");

        Properties preprops=System.getProperties();
        PythonInterpreter.initialize(preprops,props,new String[0]);

        PythonInterpreter interpreter=new PythonInterpreter();

        interpreter.exec("import sys");
        System.out.println("---------------------------------------------------------");
        System.out.println("here1");
        interpreter.exec("sys.path.append('F:\\python2\\Lib')");
        interpreter.exec("sys.path.append('F:\\python2\\Lib\\site-packages')");
        interpreter.exec("from bs4 import BeautifulSoup");
        System.out.println("---------------------------------------------------------");
        System.out.println("here1");
        interpreter.execfile("F:\\Git\\Trade-In\\Code\\Server\\src\\main\\java\\com\\bankrupted\\tradein\\script\\python\\Amazon.py");
        System.out.println("---------------------------------------------------------");
        System.out.println("here2");
        PyFunction func=(PyFunction)interpreter.get("getEvaluatePoint",PyFunction.class);
        System.out.println("---------------------------------------------------------");
        System.out.println("here3");

        PyObject pyobj=func.__call__(new PyString(title),new PyString(platform));
        System.out.println("---------------------------------------------------------");
        System.out.println("here4");

        System.out.println(pyobj.toString());
        return pyobj.toString();
    }
}
