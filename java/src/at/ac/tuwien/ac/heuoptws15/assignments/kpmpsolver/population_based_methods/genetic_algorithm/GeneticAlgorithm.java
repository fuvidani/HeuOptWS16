package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.IGASlaveCallable;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.Slave;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * <h4>About this class</h4>
 * <p>
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.0.1
 * @since 04.12.2016
 */
public class GeneticAlgorithm {

    final static int ELITISM_K = 12;
    final static int POP_SIZE = 10000 + ELITISM_K;  // population size
    //final static int MAX_ITER = 2000;             // max number of iterations
    final static double MUTATION_RATE = 0.8;     // probability of mutation
    final static double CROSSOVER_RATE = 0.7;     // probability of crossover

    private KPMPInstance instance;
    private List<KPMPSolutionWriter.PageEntry> originalEdgePartitioning;

    public GeneticAlgorithm(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartitioning) {
        this.instance = instance;
        this.originalEdgePartitioning = originalEdgePartitioning;
    }

    public KPMPSolution improve() {
        Population pop = new Population(POP_SIZE);
        pop.generatePopulation(instance,originalEdgePartitioning);
        System.out.println("Population generated");
        pop.evaluate();

        List<Individual> nextGeneration;

        // current population
        Individual bestIndividual = new Individual(pop.findBestIndividual());
        System.out.print("Total Fitness = " + pop.getTotalFitness());
        System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue());

        int cores = Runtime.getRuntime().availableProcessors();
        int distribution = (POP_SIZE - ELITISM_K) / cores;
        List<IGASlaveCallable> callableList = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            IGASlaveCallable slave = new Slave(CROSSOVER_RATE, MUTATION_RATE, ELITISM_K);
            slave.setAmountOfIndividualsToCareFor(distribution);
            callableList.add(slave);
        }

        // main loop
        int count;
        ExecutorService executorService;
        for (int iter = 0; iter < Main.iterationMultiplier && ((System.nanoTime() - Main.START) / 1000000) < (Main.secondsBeforeStop * 1000); iter++) {
            nextGeneration = new ArrayList<>(POP_SIZE);
            count = 0;

            // Elitism
            for (int i = 0; i < ELITISM_K; ++i) {
                nextGeneration.add(new Individual(pop.findBestIndividual()));
                count++;
            }
            executorService = Executors.newFixedThreadPool(cores);

            int index = 0;
            List<FutureTask<List<Individual>>> slaves = new ArrayList<>();
            for (IGASlaveCallable callable : callableList) {
                callable.setAmountOfIndividualsToCareFor(distribution);
                Population population = new Population(distribution);
                List<Individual> individuals = new ArrayList<>(distribution);
                for (int j = index; j < index + distribution; j++) {
                    individuals.add(pop.getPopulation().get(j));
                }
                population.setPopulation(individuals);
                index += distribution;
                callable.setCurrentPopulation(population);
                slaves.add(new FutureTask<>(callable));
            }

            for (FutureTask<List<Individual>> slave : slaves) {
                executorService.execute(slave);
            }
            for (FutureTask<List<Individual>> slave : slaves) {
                try {
                    nextGeneration.addAll(slave.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            pop.setPopulation(nextGeneration);
            executorService.shutdown();
            Individual bestIndividualOfPopulation = pop.findBestIndividual();
            if (bestIndividual.getFitnessValue() < bestIndividualOfPopulation.getFitnessValue()) {
                bestIndividual = new Individual(bestIndividualOfPopulation);
                System.out.print("Total Fitness = " + pop.getTotalFitness());
                System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue() + " ; Crossings: " + bestIndividual.getNumberOfCrossings());
                if (bestIndividual.getNumberOfCrossings() <= Main.lowerBound) {
                    break;
                }
            }


        }

        return bestIndividual.getGenes();
    }
}
