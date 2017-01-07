package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic.impl;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic.HybridAlgorithm;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Population;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.evolution.IGASlaveCallable;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.evolution.Slave;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
 * @since 07.01.2017
 */
public class MemeticAlgorithm implements HybridAlgorithm {
    private KPMPLocalSearch localSearch;
    private Random random;

    private final static int ELITISM_K = 0;
    private final static int POP_SIZE = 8 + ELITISM_K;  // population size
    private final static double MUTATION_RATE = 0.5;     // probability of mutation
    private final static double CROSSOVER_RATE = 0.7;     // probability of crossover
    public static final double FAMILY_ELITISM_RATE = 0.6;   // probability to choose only the best 2 individuals of a family
    public static final double NODE_SWAP_RATE = 0.4;    // probability that mutation includes node swap
    public static final double LOCAL_SEARCH_RATE = 0.8;
    private StepFunction stepFunction;

    @Override
    public KPMPSolution improve(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartitioning) {
        this.random = new Random(Double.doubleToLongBits(Math.random()));

        if (localSearch != null) {
            Population pop = new Population(POP_SIZE);
            pop.generatePopulation(instance,originalEdgePartitioning);
            System.out.println("Population generated");

            for (Individual i : pop.getPopulation()) {
                if (random.nextDouble() < LOCAL_SEARCH_RATE) {
                    i.setGenes(localSearch.improveSolution(i.getGenes(),stepFunction));
                    i.setNumberOfCrossings(localSearch.getCrossingNumber());
                }

                localSearch.setCrossingNumber(-1);
            }

            System.out.println("local search done");

            pop.evaluate();
            System.out.println("Individuals evaluated, starting evolution now.");

            List<Individual> nextGeneration;

            // current population
            Individual bestIndividual = new Individual(pop.findBestIndividual());
            System.out.print("Total Fitness = " + pop.getTotalFitness());
            System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue() + "; Crossings: " + bestIndividual.getNumberOfCrossings());

            int cores = Runtime.getRuntime().availableProcessors();
            int distribution = (POP_SIZE - ELITISM_K) / cores;
            List<IGASlaveCallable> callableList = new ArrayList<>(cores);
            for (int i = 0; i < cores; i++) {
                IGASlaveCallable slave = new Slave(CROSSOVER_RATE, MUTATION_RATE, ELITISM_K);
                slave.setAmountOfIndividualsToCareFor(distribution);
                //slave.setNodeSwapMutation(true);
                callableList.add(slave);
            }

            // main loop
            ExecutorService executorService;
            executorService = Executors.newFixedThreadPool(cores);
            for (int iter = 0; iter < Main.iterationMultiplier && ((System.nanoTime() - Main.START) / 1000000) < (Main.secondsBeforeStop * 1000); iter++) {
                nextGeneration = new ArrayList<>(POP_SIZE);

                // Elitism
                for (int i = 0; i < ELITISM_K; ++i) {
                    nextGeneration.add(new Individual(pop.findBestIndividual()));
                }


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
                    population.evaluate();
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

                for (Individual i : nextGeneration) {
                    if (random.nextDouble() < LOCAL_SEARCH_RATE) {
                        i.setGenes(localSearch.improveSolution(i.getGenes(),stepFunction));
                        i.setNumberOfCrossings(localSearch.getCrossingNumber());
                    }

                    localSearch.setCrossingNumber(-1);
                }

                System.out.println("local search done");

                pop.setPopulation(nextGeneration);
                pop.evaluate();
                //executorService.shutdown();
                Individual bestIndividualOfPopulation = pop.findBestIndividual();
                if (bestIndividual.getFitnessValue() <= bestIndividualOfPopulation.getFitnessValue()) {
                    bestIndividual = new Individual(bestIndividualOfPopulation);
                    System.out.print("Total Fitness = " + pop.getTotalFitness());
                    System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue() + " ; Crossings: " + bestIndividual.getNumberOfCrossings());
                    if (bestIndividual.getNumberOfCrossings() <= Main.lowerBound) {
                        break;
                    }
                }


            }
            executorService.shutdown();
            return bestIndividual.getGenes();
        }

        throw new IllegalArgumentException("localsearch null");
    }

    @Override
    public void registerLocalSearch(KPMPLocalSearch localSearch, StepFunction stepFunction) {
        this.localSearch = localSearch;
        this.stepFunction = stepFunction;
    }
}
