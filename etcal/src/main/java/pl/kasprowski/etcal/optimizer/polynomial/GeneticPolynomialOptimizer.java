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

public class GeneticPolynomialOptimizer extends PolynomialOptimizer implements GeneticOptimizer{
	Logger log = Logger.getLogger(GeneticPolynomialOptimizer.class);

	
	private int genPopulation = 10;
	public int getGenPopulation() {return genPopulation;}
	public void setGenPopulation(Integer popSize) {this.genPopulation = popSize;}

	private int genIterations = 10;
	public int getGenIterations() {return genIterations;}
	public void setGenIterations(Integer iterations) {this.genIterations = iterations;}


	@Override
	public void optimize(DataUnits dataUnits) throws Exception {
//		this.setDataUnits(dataUnits);
		
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
		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		log.trace("BEST: "+ MaskedPolynomialProblem.maskAsString(chromosomeToMask(bestSolutionSoFar)));

		setBest(chromosomeToMask(bestSolutionSoFar));

		IChromosome previousBestSolution = bestSolutionSoFar;
		int iterationsWithNoProgress = 0;
		for(int i=0;i<genIterations;i++) {
			log.trace("Iteration "+i);
			population.evolve();

			bestSolutionSoFar = population.getFittestChromosome();
			setBest(chromosomeToMask(bestSolutionSoFar));

			if(myFunc.getFitnessValue(bestSolutionSoFar)<=myFunc.getFitnessValue(previousBestSolution)) {
				log.trace("Current solution doesn't improve the previous one" );
				iterationsWithNoProgress++;
			}
			else
				iterationsWithNoProgress = 0;
			if(iterationsWithNoProgress>3) {
				log.trace("No progress for "+iterationsWithNoProgress+ " iterations - aborting" );
				break;
			}
			previousBestSolution = bestSolutionSoFar;
			
			log.trace("Iteration "+i+ " result\t"+ MaskedPolynomialProblem.maskAsString(chromosomeToMask(bestSolutionSoFar))+"\t"
					+ 1/myFunc.getFitnessValue(bestSolutionSoFar));
		}

		setBest(chromosomeToMask(population.getFittestChromosome()));
		log.debug("Evolution finished, best: "+MaskedPolynomialProblem.maskAsString(chromosomeToMask(bestSolutionSoFar))+"\t"
				+ 1/myFunc.getFitnessValue(bestSolutionSoFar));

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
