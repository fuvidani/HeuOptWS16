package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <h4>About this class</h4>
 * <p>
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.0.1
 * @since 04.12.2016
 */
public class Individual implements Cloneable {

    private KPMPSolution genes;
    private int numberOfCrossings;
    private double fitnessValue;
    private boolean needsEvaluation = true;

    public Individual() {
    }

    public Individual(Individual individual) {
        this.genes = new KPMPSolution(individual.getGenes());
        this.numberOfCrossings = individual.getNumberOfCrossings();
        this.fitnessValue = individual.getFitnessValue();
        this.needsEvaluation = individual.needsEvaluation();
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public KPMPSolution getGenes() {
        return genes;
    }

    public void setGenes(KPMPSolution genes) {
        this.genes = genes;
    }

    public int getNumberOfCrossings() {
        return numberOfCrossings;
    }

    public void setNumberOfCrossings(int numberOfCrossings) {
        this.needsEvaluation = false;
        this.numberOfCrossings = numberOfCrossings;
        this.fitnessValue = Main.maxCrossingNumber - numberOfCrossings;
    }

    public boolean needsEvaluation() {
        return needsEvaluation;
    }

    public Individual clone() {
        return new Individual(this);
    }

    public void mutate() {
        Random rand = new Random(Double.doubleToLongBits(Math.random()));
        int edgeIndex = rand.nextInt(genes.getEdgePartition().size());
        List<KPMPSolutionWriter.PageEntry> edgePartition = genes.getEdgePartition();
        KPMPSolutionWriter.PageEntry edgeToMutate = edgePartition.get(edgeIndex);
        int pageIndex;
        do {
            pageIndex = rand.nextInt(genes.getNumberOfPages());
        } while (pageIndex == edgeToMutate.page);
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        //      int originalCrossings = checker.getCrossingNumberOfEdge(genes.getSpineOrder(), edgePartition, edgeToMutate.page, edgeToMutate);
        edgeToMutate.page = pageIndex;
        edgePartition.set(edgeIndex,edgeToMutate);
//        int newCrossings = checker.getCrossingNumberOfEdge(genes.getSpineOrder(), edgePartition, edgeToMutate.page, edgeToMutate);
        genes.setEdgePartition(edgePartition);
        List<Integer> originalSpineOrder = genes.getSpineOrder().stream().collect(Collectors.toCollection(ArrayList::new));
        int randomNodeIndex1 = rand.nextInt(originalSpineOrder.size());
        int randomNodeIndex2 = rand.nextInt(originalSpineOrder.size());
        Collections.swap(originalSpineOrder, randomNodeIndex1, randomNodeIndex2);
        genes.setSpineOrder(originalSpineOrder);
        needsEvaluation = true;
        evaluate();
    }

    public double evaluate() {
        if (needsEvaluation) {
            int crossings = new KPMPSolutionChecker().getCrossingNumber(genes);
            this.setNumberOfCrossings(crossings);
        }
        return fitnessValue;
    }
}
