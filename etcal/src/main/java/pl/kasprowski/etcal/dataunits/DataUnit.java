package pl.kasprowski.etcal.dataunits;

import java.util.ArrayList;
import java.util.List;

public class DataUnit {
	private List<Double> variables = new ArrayList<Double>();
	private List<Target> targets = new ArrayList<Target>();

	public void addVariable(Double x) {
		variables.add(x);
	}
	public void addVariables(double[] x) {
		for(double v:x) addVariable(v);
	}

	public void addVariables(List<Double> x) {
		for(double v:x) addVariable(v);
	}

	public void addTarget(Target t) {
		targets.add(t);
	}
	
	public int getSize() {
		return variables.size();
	}

	public List<Target> getTargets() {
		return targets;
	}
	

	public List<Double> getVariables() {
		return variables;
	}

	public double[] getVariablesAsTable() {
		double[] t = new double[variables.size()];
		int i=0;
		for(Double v:variables) t[i++]=v;
		return t;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Variables:");
		for(Double v: variables) sb.append(v+";");
		sb.append("\tTargets:" );
		for(Target v: targets) sb.append(v);
		return sb.toString();
	}
}
