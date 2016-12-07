package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    final static int ELITISM_K = 5;
    final static int POP_SIZE = 100 + ELITISM_K;  // population size
    //final static int MAX_ITER = 2000;             // max number of iterations
    final static double MUTATION_RATE = 0.9;     // probability of mutation
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

        Random m_rand;

        List<Individual> nextGeneration;

        // current population
        Individual bestIndividual = new Individual(pop.findBestIndividual());
        System.out.print("Total Fitness = " + pop.getTotalFitness());
        System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue());

        // main loop
        int count;
        for (int iter = 0; iter < Main.iterationMultiplier && ((System.nanoTime() - Main.START) / 1000000) < (Main.secondsBeforeStop * 1000); iter++) {
            m_rand = new Random(Double.doubleToLongBits(Math.random()));
            nextGeneration = new ArrayList<>(POP_SIZE);
            count = 0;

            // Elitism
            for (int i = 0; i < ELITISM_K; ++i) {
                nextGeneration.add(new Individual(pop.findBestIndividual()));
                count++;
            }

            // build new Population
            while (count < POP_SIZE) {
                // Selection
                Individual mother = pop.tournamentSelection();
                Individual father = pop.tournamentSelection();

                List<Individual> children = new ArrayList<>(2);
                // Crossover
                if (m_rand.nextDouble() < CROSSOVER_RATE) {
                    // return only the 2 best ones of the family
                    children = pop.recombination(mother, father);
                    /*List<Individual> family = new ArrayList<>();
                    family.add(mother);
                    family.add(father);
                    family.add(children.get(0));
                    family.add(children.get(1));
                    family.sort(Comparator.comparingDouble(Individual::getFitnessValue));
                    children = new ArrayList<>(2);
                    children.add(family.get(2));
                    children.add(family.get(3));*/
                } else {
                    children.add(mother);
                    children.add(father);
                }

                // Mutation
                if (m_rand.nextDouble() < MUTATION_RATE) {
                    children.get(0).mutate();
                }
                if (m_rand.nextDouble() < MUTATION_RATE) {
                    children.get(1).mutate();
                }

                // add to new population
                nextGeneration.add(children.get(0));
                nextGeneration.add(children.get(1));
                count += 2;
            }
            pop.setPopulation(nextGeneration);

            // reevaluate current population
            pop.evaluate();

            Individual bestIndividualOfPopulation = pop.findBestIndividual();
            if (bestIndividual.getFitnessValue() < bestIndividualOfPopulation.getFitnessValue()) {
                bestIndividual = new Individual(bestIndividualOfPopulation);
                System.out.print("Total Fitness = " + pop.getTotalFitness());
                System.out.println(" ; Best Fitness = " + bestIndividual.getFitnessValue() + " ; Crossings: " + bestIndividual.getNumberOfCrossings());
                int i = bestIndividual.getNumberOfCrossings();
                int j = new KPMPSolutionChecker().getCrossingNumber(bestIndividual.getGenes());
                //assert (i == j);
                if (bestIndividual.getNumberOfCrossings() <= Main.lowerBound) {
                    break;
                }
            }


        }

        // best indiv
        //Individual bestIndiv = pop.findBestIndividual();

        return bestIndividual.getGenes();
    }
}
