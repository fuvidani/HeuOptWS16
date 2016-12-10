package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.evolution;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.GeneticAlgorithm;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Population;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 07.12.16
 */
public class Slave implements IGASlaveCallable {

    private Population population;
    private int amountOfIndividualsToCareFor;
    private final double crossOverRate;
    private double mutationRate;
    private Random random;
    private List<Individual> newGenerationSubset;
    private final int elitism;
    private boolean includeNodeSwap;

    public Slave(double crossOverRate, double mutationRate, int elitism) {
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.includeNodeSwap = false;
    }

    @Override
    public void setCurrentPopulation(final Population population) {
        this.population = population;
    }

    @Override
    public void setAmountOfIndividualsToCareFor(final int size) {
        this.amountOfIndividualsToCareFor = size;
        newGenerationSubset = new ArrayList<>(size);
    }

    @Override
    public void adjustMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    @Override
    public void setNodeSwapMutation(boolean includeNodeSwap) {
        this.includeNodeSwap = includeNodeSwap;
    }


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     *
     * @throws Exception if unable to compute a result
     */
    @Override
    public List<Individual> call() throws Exception {
        int count = 0;
        while (count < amountOfIndividualsToCareFor) {
            this.random = new Random(Double.doubleToLongBits(Math.random()));

            // Selection
            //population.getPopulation().sort(Comparator.comparingInt(Individual::getNumberOfCrossings));
            Individual mother = population.tournamentSelection();
            Individual father = population.tournamentSelection();
            List<Individual> children = new ArrayList<>(2);

            // Crossover
            if (random.nextDouble() < crossOverRate) {
                children = crossover(mother, father);
                if (random.nextDouble() < GeneticAlgorithm.FAMILY_ELITISM_RATE) {
                    List<Individual> family = new ArrayList<>(4);
                    family.add(mother);
                    family.add(father);
                    family.addAll(children);
                    family.sort(Comparator.comparingInt(Individual::getNumberOfCrossings));
                    children.add(0, family.get(0));
                    children.add(1, family.get(1));
                }
            } else {
                children.add(mother);
                children.add(father);
            }

            // Mutation
            if (random.nextDouble() < mutationRate) {
                children.get(0).mutate(includeNodeSwap);
            }
            if (random.nextDouble() < mutationRate) {
                children.get(1).mutate(includeNodeSwap);
            }

            // add to new population
            newGenerationSubset.add(children.get(0));
            newGenerationSubset.add(children.get(1));
            count += 2;
        }

        return newGenerationSubset;
    }

    private List<Individual> crossover(Individual mother1, Individual father2) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();

        Individual father = new Individual(father2);
        Individual mother = new Individual(mother1);

        // sort edges
        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o4 -> o4.a));
        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o3 -> o3.b));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o2 -> o2.a));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o -> o.b));

        int randPointForSpineOrder = random.nextInt(mother.getGenes().getSpineOrder().size() - 1) + 1;
        int randPointForEdgePartition = random.nextInt(mother.getGenes().getEdgePartition().size() - 1) + 1;

        // child1 spine order
        List<Integer> child1SpineOrder = new ArrayList<>();
        List<Integer> fatherSpineOrderCopy = father.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int value = mother.getGenes().getSpineOrder().get(i);
            child1SpineOrder.add(value);
            fatherSpineOrderCopy.remove(new Integer(value));
        }
        for (int i = 0; i < fatherSpineOrderCopy.size(); i++) {
            child1SpineOrder.add(fatherSpineOrderCopy.get(i));
        }

        // child1 edge partition
        List<KPMPSolutionWriter.PageEntry> child1EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child1EdgePartition.add(mother.getGenes().getEdgePartition().get(i).clone());
        }
        for (int i = randPointForEdgePartition; i < father.getGenes().getEdgePartition().size(); i++) {
            child1EdgePartition.add(father.getGenes().getEdgePartition().get(i).clone());
        }
        child1.setGenes(new KPMPSolution(child1SpineOrder, child1EdgePartition, mother.getGenes().getNumberOfPages()));

        // child2 spine order
        List<Integer> child2SpineOrder = new ArrayList<>();
        List<Integer> motherSpineOrderCopy = mother.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int vertex = father.getGenes().getSpineOrder().get(i);
            child2SpineOrder.add(vertex);
            motherSpineOrderCopy.remove(new Integer(vertex));
        }
        for (int i = 0; i < motherSpineOrderCopy.size(); i++) {
            child2SpineOrder.add(motherSpineOrderCopy.get(i));
        }

        // child2 edge partition
        List<KPMPSolutionWriter.PageEntry> child2EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child2EdgePartition.add(father.getGenes().getEdgePartition().get(i).clone());
        }
        for (int i = randPointForEdgePartition; i < mother.getGenes().getEdgePartition().size(); i++) {
            child2EdgePartition.add(mother.getGenes().getEdgePartition().get(i).clone());
        }
        child2.setGenes(new KPMPSolution(child2SpineOrder, child2EdgePartition, mother.getGenes().getNumberOfPages()));

        List<Individual> children = new ArrayList<>();
        child1.evaluate();
        child2.evaluate();
        children.add(child1);
        children.add(child2);

        return children;
    }
}
