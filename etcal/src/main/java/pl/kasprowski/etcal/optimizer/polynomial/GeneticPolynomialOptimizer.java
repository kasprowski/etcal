package pl.kasprowski.etcal.optimizer.polynomial;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;

import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.optimizer.GeneticOptimizer;

/**
 * For the given dataUnits tries to find the best masks (set of terms 
 * for 3rd degree polynomial) - uses genetic algorithm.
 * getBest() method returns the best calibrator found so far.
 *
 * @author pawel@kasprowski.pl
 *
 */
public class GeneticPolynomialOptimizer extends PolynomialOptimizer implements GeneticOptimizer{
	Logger log = Logger.getLogger(GeneticPolynomialOptimizer.class);

	
	private int genPopulation = 10;
	public int getGenPopulation() {return genPopulation;}
	public void setGenPopulation(Integer popSize) {this.genPopulation = popSize;}

	private int genIterations = 10;
	public int getGenIterations() {return genIterations;}
	public void setGenIterations(Integer iterations) {this.genIterations = iterations;}

	private int numIterationsWithNoProgress = 4;
	public int getNumIterationsWithNoProgress() {return numIterationsWithNoProgress;}
	public void setNumIterationsWithNoProgress(Integer numIterationsWithNoProgress) {this.numIterationsWithNoProgress = numIterationsWithNoProgress;}


	@Override
	public void optimize(DataUnits dataUnits) throws Exception {
		this.setDataUnits(dataUnits);
		
		//RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		
		//		setOptimizing(true);
		Configuration.reset();
		Configuration conf = new DefaultConfiguration();
		FitnessFunction myFunc  = new PolynomialFittnessFunction(dataUnits,getCvFolds(),getCvType());
		conf.setFitnessFunction( myFunc );


		int size = MaskedPolynomialProblem.termsNum(dataUnits.xNum());
		Gene[] sampleGenes = new Gene[size];	
		for(int i=0;i<size;i++)
			sampleGenes[i] = new BooleanGene(conf);
		Chromosome sampleChromosome = new Chromosome(conf, sampleGenes );


		conf.setSampleChromosome( sampleChromosome );
		conf.setPopulationSize( genPopulation );

		log.debug("Starting evolution");
		Genotype population = Genotype.randomInitialGenotype( conf );
		population.evolve();
		IChromosome bestInitialSolution = population.getFittestChromosome();
		log.trace("BEST: "+ MaskedPolynomialProblem.maskAsString(chromosomeToMask(bestInitialSolution)));

		setBest(chromosomeToMask(bestInitialSolution));

		IChromosome currentBestSolution = (IChromosome)bestInitialSolution.clone();
		int iterationsWithNoProgress = 0;
		for(int i=0;i<genIterations;i++) {
			log.trace("Iteration "+i);
			population.evolve();

			IChromosome newBestSolution = population.getFittestChromosome();
	
			if(myFunc.getFitnessValue(newBestSolution)<=myFunc.getFitnessValue(currentBestSolution)) {
				log.trace("Current solution doesn't improve the previous one" );
				iterationsWithNoProgress++;
			}
			else {
				iterationsWithNoProgress = 0;
				currentBestSolution = (IChromosome)newBestSolution.clone();
				setBest(chromosomeToMask(currentBestSolution));
			}
			if(iterationsWithNoProgress>numIterationsWithNoProgress) {
				log.trace("No progress for "+iterationsWithNoProgress+ " iterations - aborting" );
				break;
			}
			
	

			log.trace("Iteration "+i+ " result\t"+ MaskedPolynomialProblem.maskAsString(chromosomeToMask(currentBestSolution))+"\t"
					+ 1/myFunc.getFitnessValue(currentBestSolution));
		}
		
		setBest(chromosomeToMask(currentBestSolution));
		log.debug("Evolution finished, best: "+MaskedPolynomialProblem.maskAsString(chromosomeToMask(currentBestSolution))+"\t"
				+ 1/myFunc.getFitnessValue(currentBestSolution));

//		setOptimizing(false);
		//	return chromosomeToMask(population.getFittestChromosome());
	}


	boolean[] chromosomeToMask(IChromosome chromosome) {
		boolean[] mask = new boolean[chromosome.getGenes().length];
		for(int i=0;i<chromosome.getGenes().length;i++) {
			Gene gene = chromosome.getGene(i);
			//Double v = (Double)gene.getAllele();
			mask[i] = (boolean)gene.getAllele();
		}
		return mask;
	}
	@Override
	public String toString() {
		return super.toString()+" genPopulations="+getGenPopulation()+" genIterations="+getGenIterations();
	}

}
