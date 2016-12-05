package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
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
    private Random m_rand = new Random();  // random-number generator

    final static int ELITISM_K = 5;
    final static int POP_SIZE = 200 + ELITISM_K;  // population size
    //final static int MAX_ITER = 2000;             // max number of iterations
    final static double MUTATION_RATE = 0.2;     // probability of mutation
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
        pop.evaluate();

        List<Individual> nextGeneration;

        // current population
        System.out.print("Total Fitness = " + pop.getTotalFitness());
        System.out.println(" ; Best Fitness = " +
                pop.findBestIndividual().getFitnessValue());

        // main loop
        int count;
        for (int iter = 0; iter < Main.iterationMultiplier; iter++) {
            m_rand = new Random(Double.doubleToLongBits(Math.random()));
            nextGeneration = new ArrayList<>(POP_SIZE);
            count = 0;

            // Elitism
            //TODO spare re-calculation of crossings
            for (int i = 0; i < ELITISM_K; ++i) {
                nextGeneration.add(pop.findBestIndividual());
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
                    children = pop.crossover(mother, father);
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
            System.out.print("Total Fitness = " + pop.getTotalFitness());
            System.out.println(" ; Best Fitness = " +
                    pop.findBestIndividual().getFitnessValue());
        }

        // best indiv
        Individual bestIndiv = pop.findBestIndividual();

        return bestIndiv.getGenes();
    }
}
