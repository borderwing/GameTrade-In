package com.bankrupted.tradein.script;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by homepppp on 2017/7/7.
 */
@Component
public class pythonGetEvaluatePoint {
    public String getPoints(String title,String platform){
        Properties props=new Properties();
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site","false");

        Properties preprops=System.getProperties();
        PythonInterpreter.initialize(preprops,props,new String[0]);

        PythonInterpreter interpreter=new PythonInterpreter();

        // commented out Zhang's path
//        interpreter.exec("import sys");
//        interpreter.exec("sys.path.append('F:\\python2\\Lib')");
//        interpreter.exec("sys.path.append('F:\\python2\\Lib\\site-packages')");
//        interpreter.exec("from bs4 import BeautifulSoup");
//        interpreter.execfile("F:\\Git\\Trade-In\\Code\\Server\\src\\main\\java\\com\\bankrupted\\tradein\\script\\python\\Amazon.py");

        interpreter.exec("import sys");
        interpreter.exec("sys.path.append('C:\\Python27\\Lib')");
        interpreter.exec("sys.path.append('C:\\Python27\\Lib\\site-packages')");
        interpreter.exec("from bs4 import BeautifulSoup");
        interpreter.execfile("C:\\Ghost\\ProjectGameDealer\\Trade-In\\Code\\Server\\src\\main\\java\\com\\bankrupted\\tradein\\script\\python\\Amazon.py");

        PyFunction func=(PyFunction)interpreter.get("getEvaluatePoint",PyFunction.class);

        PyObject pyobj=func.__call__(new PyString(title),new PyString(platform));

        System.out.println(pyobj.toString());
        return pyobj.toString();
    }
}
