package script;

import javax.script.*;

import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 * Created by homepppp on 2017/7/7.
 */
public class pythonGetEvaluatePoint {
    public static String getPoints(String title){
        PythonInterpreter interpreter=new PythonInterpreter();
        interpreter.execfile("Amazon.py");
        PyFunction func=(PyFunction)interpreter.get("getEvaluatePoint",PyFunction.class);

        PyObject pyobj=func.__call__(new PyString(title));
        return pyobj.toString();
    }
}
