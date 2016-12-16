package pl.kasprowski.etcal.helpers;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Helper with methods that use reflection to build objects
 * from ObjDef definitions objects
 * @author pawel@kasprowski.pl
 * 
 */
public class ObjectBuilder {
	static Logger log = Logger.getLogger(ObjectBuilder.class);
	static {
		log.setLevel(Level.WARN);
	}

	/**
	 * Returns object created using the ObjDef
	 * @param objDef
	 * @return
	 */
	public static Object getObjectFromObjDef(ObjDef objDef) {
		log.trace("Building object "+objDef.type+" with: "+objDef.params);
		Object o = null;
		try {
			o = Class.forName(objDef.type).newInstance();
			ObjectBuilder.runSetters(o, objDef.params);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception while creating class "+objDef.type);
		}
		return o;
	}
	
	/**
	 * For every key in params map tries to run setKey method giving the key's
	 * value as the parameter. Converts String values to proper objects
	 * @param o object with setters
	 * @param params map of properties to set on object
	 * @throws Exception
	 */
	private static void runSetters(Object o, Map<String,String> params) throws Exception{
		for(String key:params.keySet()) {
			String setterName = "set"+ key.substring(0,1).toUpperCase()+key.substring(1);
			Class<?> paramClass = ObjectBuilder.findParamClass(o, setterName);
			if(paramClass==null) {
				log.warn("No setter for parameter "+o.getClass().getName()+"."+setterName+" ");
				continue;
			}
			String txt = params.get(key);
			Object value = ObjectBuilder.convertStringToClass(txt, paramClass);
			try {
				Method mSet = o.getClass().getMethod(setterName,value.getClass());
				mSet.invoke(o, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
	
	/**
	 * Returns class of the first parameter of the given method from the given object
	 * @param obj object with method
	 * @param metName method name
	 * @return class of the first parameter in method
	 */
	private static Class<?> findParamClass(Object obj,String metName) {
		Class<?> c = obj.getClass();
		Method[] methods = c.getMethods();
		for(Method m:methods) {
			if(m.getName().equals(metName))
				return m.getParameterTypes()[0];
		}
		return null;
	}

	/**
	 * Converts String object to an object of a given class
	 * (inversion of toString method)
	 * FOR NOW works only for String, Integer and Double classes! 
	 * @param txt text
	 * @param clazz class
	 * @return object of class clazz built from String txt
	 */
	private static Object convertStringToClass(String txt,Class<?> clazz) {
		if(clazz.equals(String.class))
			return txt;
		if(clazz.equals(Integer.class) || clazz.equals(int.class))
			return Integer.parseInt(txt);
		if(clazz.equals(Double.class) || clazz.equals(double.class))
			return Double.parseDouble(txt);
		return null;
	}

	
}
