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

		//initialize with the correct gene
//		IChromosome[] initialChromosomes = new IChromosome[genPopulation];
//		Gene[] genes0 = new Gene[size];
//		for(int i=0;i<size;i++) {
//			int max = dataUnits.getDataUnits().get(i).getTargets().size()-1;
//			genes0[i] = new IntegerGene(conf, 0, max);
//			genes0[i].setAllele(i);
//		}
//		initialChromosomes[0] = new Chromosome(conf, genes0 ); 
//		
//		for(int j=1;j<genPopulation;j++) {
//			Gene[] genes = new Gene[size];
//			for(int i=0;i<size;i++) {
//				int max = dataUnits.getDataUnits().get(i).getTargets().size()-1;
//				genes[i] = new IntegerGene(conf, 0, max);
//				int x = new Random().nextInt(max+1);
//				genes[i].setAllele(x);
//			}
//			initialChromosomes[j] = new Chromosome(conf, genes ); 
//		}
//		Genotype population = new Genotype(conf,new Population(conf,initialChromosomes));
		Genotype population = Genotype.randomInitialGenotype( conf );
		
		
		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		log.trace("BEST: "+ bestSolutionSoFar);

//		setBest(new CalibratorPolynomial(data, chromosomeToMask(bestSolutionSoFar)),
//				chromosomeToMask(bestSolutionSoFar));

		IChromosome previousBestSolution = bestSolutionSoFar;
		int iterationsWithNoProgress = 0;
		for(int i=0;i<genIterations;i++) {
			log.trace("Iteration "+i);
			population.evolve();

			bestSolutionSoFar = population.getFittestChromosome();
//			setBest(new CalibratorPolynomial(data, chromosomeToMask(bestSolutionSoFar)),
//					chromosomeToMask(bestSolutionSoFar));

			if(myFunc.getFitnessValue(bestSolutionSoFar)<=myFunc.getFitnessValue(previousBestSolution)) {
				log.trace("Current solution doesn't improve the previous one" );
				iterationsWithNoProgress++;
			}
			else
				iterationsWithNoProgress = 0;
			if(iterationsWithNoProgress>numIterationsWithNoProgress) {
				log.trace("No progress for "+iterationsWithNoProgress+ " iterations - aborting" );
				break;
			}
			previousBestSolution = bestSolutionSoFar;
			
			log.trace("Iteration "+i+ " result\t"+ 
					1/myFunc.getFitnessValue(bestSolutionSoFar)+ " "+
					chromosomeToMapping(bestSolutionSoFar)
					);
		}

		
//		setBest(new CalibratorPolynomial(data, chromosomeToMask(population.getFittestChromosome())),
//				chromosomeToMask(population.getFittestChromosome()));
		log.debug("Evolution finished, best: "+
				1/myFunc.getFitnessValue(bestSolutionSoFar)+" "+
				chromosomeToMapping(bestSolutionSoFar)
				);

//		setOptimizing(false);
		//	return chromosomeToMask(population.getFittestChromosome());
		
		
		return chromosomeToMapping(bestSolutionSoFar);
		
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
