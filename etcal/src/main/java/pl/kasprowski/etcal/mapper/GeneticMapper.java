package pl.kasprowski.etcal.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.optimizer.GeneticOptimizer;

/**
 * Implementation of Mapper interface that uses genetic algorithm to find the best mapping
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class GeneticMapper implements Mapper,GeneticOptimizer{
	Logger log = Logger.getLogger(GeneticMapper.class);
	
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
	public List<Integer> map(DataUnits dataUnits) throws Exception{
		int size = dataUnits.getDataUnits().size();
	
		Configuration.reset();
		Configuration conf = new DefaultConfiguration();
		FitnessFunction myFunc  = new MappingFittnessFunction(dataUnits);
		conf.setFitnessFunction( myFunc );

		// some magic from:
		// http://stackoverflow.com/questions/10473397/using-jgap-genetic-algorithm-library-and-the-duplicated-chromosomes
		conf.getNaturalSelectors(false).clear();
		BestChromosomesSelector bcs = new BestChromosomesSelector(conf, 1.0d);
		bcs.setDoubletteChromosomesAllowed(false);
		conf.addNaturalSelector(bcs, false);
		//
		
		Gene[] sampleGenes = new Gene[size];	
		for(int i=0;i<size;i++) {
			int max = dataUnits.getDataUnits().get(i).getTargets().size()-1;
			//System.out.println(i+ " > "+max);
			sampleGenes[i] = new IntegerGene(conf,0,max);
		}
		Chromosome sampleChromosome = new Chromosome(conf, sampleGenes );


		conf.setSampleChromosome( sampleChromosome );
		conf.setPopulationSize( genPopulation );

		log.debug("Starting evolution");

		Genotype population = Genotype.randomInitialGenotype( conf );
		
		
		IChromosome bestInitialSolution = population.getFittestChromosome();

		IChromosome currentBestSolution = (IChromosome)bestInitialSolution.clone();
		int iterationsWithNoProgress = 0;
		for(int i=0;i<genIterations;i++) {
			log.trace("Iteration "+i);
			population.evolve();
			IChromosome newBestSolution = population.getFittestChromosome();
			log.trace("New Best solution: "+newBestSolution.getGene(0));
			if(myFunc.getFitnessValue(newBestSolution)<=myFunc.getFitnessValue(currentBestSolution)) {
				log.trace("Current solution doesn't improve the previous one" );
				iterationsWithNoProgress++;
			}
			else {
				iterationsWithNoProgress = 0;
				currentBestSolution = (IChromosome)newBestSolution.clone();
			}
			if(iterationsWithNoProgress>numIterationsWithNoProgress) {
				log.trace("No progress for "+iterationsWithNoProgress+ " iterations - aborting" );
				break;
			}
			
			
			log.trace("Iteration "+i+ " result\t"+ 
					1/myFunc.getFitnessValue(currentBestSolution)+ " "+
					chromosomeToMapping(currentBestSolution)
					);
		}

		
		log.debug("Evolution finished, best: "+
				1/myFunc.getFitnessValue(currentBestSolution)+" "+
				chromosomeToMapping(currentBestSolution)
				);

		return chromosomeToMapping(currentBestSolution);
		
	}
	

	public static List<Integer> chromosomeToMapping(IChromosome chromosome) {
		List<Integer> mapping = new ArrayList<Integer>();
		for(int i=0;i<chromosome.getGenes().length;i++) {
			Gene gene = chromosome.getGene(i);
			mapping.add((Integer)gene.getAllele());
		}
		return mapping;
	}
}
