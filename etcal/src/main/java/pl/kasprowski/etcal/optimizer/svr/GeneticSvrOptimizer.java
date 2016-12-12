package pl.kasprowski.etcal.optimizer.svr;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.optimizer.GeneticOptimizer;

public class GeneticSvrOptimizer extends SvrOptimizer implements GeneticOptimizer{
	Logger log = Logger.getLogger(GeneticSvrOptimizer.class);

	private int genPopulation = 10;
	public int getGenPopulation() {return genPopulation;}
	public void setGenPopulation(Integer popSize) {this.genPopulation = popSize;}

	private int genIterations = 10;
	public int getGenIterations() {return genIterations;}
	public void setGenIterations(Integer iterations) {this.genIterations = iterations;}


	
	public void optimize(DataUnits dataUnits) throws Exception {
		this.setDataUnits(dataUnits);
		
		Configuration.reset();
		Configuration conf = new DefaultConfiguration();
		FitnessFunction myFunc  = new SvrFittnessFunction(dataUnits,getCvFolds(),getCvType());
		conf.setFitnessFunction( myFunc );

		log.debug("Starting SVR optimization");

		Gene[] sampleGenes = new Gene[2];	
		sampleGenes[0] = new IntegerGene(conf,minExpCost,maxExpCost); //2^cost 
		sampleGenes[1] = new IntegerGene(conf,minExpGamma,maxExpGamma); //2^gamma

		Chromosome sampleChromosome = new Chromosome(conf, sampleGenes );


		conf.setSampleChromosome( sampleChromosome );
		conf.setPopulationSize( getGenPopulation() );


		Genotype population = Genotype.randomInitialGenotype( conf );
		population.evolve();
		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		System.out.println("BEST: "+ show(chromosomeToParams(bestSolutionSoFar)));
		
		setBest(chromosomeToParams(bestSolutionSoFar));

		int iterationsWithNoProgress = 0;
		IChromosome previousBestSolution = bestSolutionSoFar;
		for(int i=0;i<getGenIterations();i++) {
			log.trace("Iteration "+i);
			population.evolve();
			bestSolutionSoFar = population.getFittestChromosome();
			setBest(chromosomeToParams(bestSolutionSoFar));

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
			
			log.trace(i+ "\t"+ show(chromosomeToParams(bestSolutionSoFar))+"\t"
					+ 1/myFunc.getFitnessValue(bestSolutionSoFar));
		}

		setBest(chromosomeToParams(population.getFittestChromosome()));
		
	}
	double[] intToDouble(int[] x) {
		double[] y = new double[x.length];
		for(int i=0;i<x.length;i++) y[i] = x[i];
		return y;
	}




	int[] chromosomeToParams(IChromosome chromosome) {
		int[] res = new int[3];
		for(int i=0;i<chromosome.getGenes().length;i++) {
			Gene gene = chromosome.getGene(i);
			//Double v = (Double)gene.getAllele();
			res[i] = (int)gene.getAllele();
		}

		return res;
	}

	String show(int[] x) {
		return x[0]+" "+x[1]+" "+x[2];
	}

	@Override
	public String toString() {
		return super.toString()+" genPoluplations="+getGenPopulation()+" genIterations="+getGenIterations();
	}

}
