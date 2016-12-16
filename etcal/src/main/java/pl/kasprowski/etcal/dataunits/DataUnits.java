package pl.kasprowski.etcal.dataunits;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Collection od DataUnit objects.
 * Additionally helper methods for JSON conversions
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class DataUnits implements Cloneable{
	private List<DataUnit> dataUnits = new ArrayList<DataUnit>();

	public int size() {
		return dataUnits.size();
	}
	
	/**
	 * Returns number of variables
	 * @return 
	 */
	public int xNum() {
		return dataUnits.get(0).getSize();
	}
	
	@Override
	public String toString() {
		return dataUnits.toString();
	}

	public void add(DataUnit du) {
		dataUnits.add(du);
	}

	public List<DataUnit> getDataUnits() {
		return dataUnits;
	}

	/**
	 * Saves object in file in JSON format
	 * @param fname
	 */
	public void save(String fname) {
		try (Writer writer = new FileWriter(fname)) {
			save(writer);
		} catch(IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException("File "+fname+" not saved!");
		}

	}

	/**
	 * Converts object to JSON format and sends it to writer
	 * @param writer
	 */
	public void save(Writer writer) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(this, writer);
	}

	/**
	 * Loads DataUnis object from JSON document 
	 * @param fname
	 * @return
	 */
	public static DataUnits load(String fname) {

		try(BufferedReader reader = new BufferedReader(new FileReader(new File(fname)))) {
			return load(reader);
		}catch(IOException ie) {
			ie.printStackTrace();
			throw new RuntimeException("File "+fname+" not loaded!");
		}
	}

	/**
	 * Loads DataUnis object from JSON reader
	 * @param reader
	 * @return
	 */
	public static DataUnits load(BufferedReader reader) {
		Gson gson = new GsonBuilder().create();
		DataUnits du = gson.fromJson(reader, DataUnits.class);
		return du;     
	}
	
	/**
	 * Adds one DataUnits object to another
	 * @param dus
	 */
	public void add(DataUnits dus) {
		for(DataUnit du:dus.getDataUnits())
			getDataUnits().add(du);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DataUnits newDu = new DataUnits();
		for(DataUnit u:dataUnits) {
			DataUnit newd = new DataUnit();
			newd.addVariables(u.getVariables());
			for(Target t:u.getTargets())
				newd.addTarget(new Target(t.getX(),t.getY(),t.getW()));
			newDu.add(newd);
		}
		return newDu;
	}
}
