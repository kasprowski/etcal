package pl.kasprowski.etcal.optimizer;

/**
 * Interface with common methods for all optimizers using genetic algorithm.
 * 
 * @author pawel@kasprowski.pl
 *
 */
public interface GeneticOptimizer {
	public int getGenPopulation();
	public void setGenPopulation(Integer popSize);
	public int getGenIterations();
	public void setGenIterations(Integer iterations);

}
