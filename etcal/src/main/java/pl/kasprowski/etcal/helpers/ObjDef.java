package pl.kasprowski.etcal.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generic definition of any object
 * @author pawel@kasprowski.pl
 *
 */
public class ObjDef {
	/**
	 * Class name for the object
	 */
	public String type;
	/**
	 * Parameters to be used (every key should have a corresponding setter method)
	 */
	public Map<String,String> params = new HashMap<String,String>();
	
	/**
	 * Loads ObjDef from JSON file
	 * @param fname
	 * @return
	 */
	public static ObjDef load(String fname) {
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(fname)))) {
			return load(reader);
		}catch(IOException ie) {
			ie.printStackTrace();
			throw new RuntimeException("File "+fname+" not loaded!");
		}
	}

	/**
	 * Loads ObjDef from JSON redaer
	 * @param reader
	 * @return
	 */
	public static ObjDef load(BufferedReader reader) {
		Gson gson = new GsonBuilder().create();
		ObjDef  cp = gson.fromJson(reader, ObjDef.class);
		return cp;     
	}

	/**
	 * Saves ObjDef to JSON file
	 * @param param
	 * @param fname
	 */
	public static void save(ObjDef param, String fname) {
		try (Writer writer = new FileWriter(fname)) {
			save(param,writer);
		} catch(IOException ex) {ex.printStackTrace();}
		
	}
	
	/**
	 * Saves ObjDef to JSON writer
	 * @param param
	 * @param writer
	 */
	public static void save(ObjDef param, Writer writer) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    gson.toJson(param, writer);
	}

}
