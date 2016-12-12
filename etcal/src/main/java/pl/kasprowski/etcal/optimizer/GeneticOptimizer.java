package pl.kasprowski.etcal.optimizer;

public interface GeneticOptimizer {
	public int getGenPopulation();
	public void setGenPopulation(Integer popSize);
	public int getGenIterations();
	public void setGenIterations(Integer iterations);

}
