package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

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
public class Individual implements Cloneable {
    //public static final int SIZE = 500;
    private KPMPSolution genes;
    //private int[] genes = new int[SIZE];
    private int numberOfCrossings;
    private int fitnessValue;

    public Individual() {
    }

    public int getFitnessValue() {
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
        this.numberOfCrossings = numberOfCrossings;
        this.fitnessValue = Main.maxCrossingNumber - numberOfCrossings;
    }

    public Individual clone() {
        return new Individual() {{
            setGenes(genes.clone());
            setNumberOfCrossings(numberOfCrossings);
        }};
    }

    public void mutate() {
        // TODO calculate only difference of crossings to avoid performance overhead
        Random rand = new Random();
        int pageIndex = rand.nextInt(genes.getNumberOfPages());
        int edgeIndex = rand.nextInt(genes.getEdgePartition().size());
        List<KPMPSolutionWriter.PageEntry> edgePartition = genes.getEdgePartition();
        KPMPSolutionWriter.PageEntry edgeToMutate = edgePartition.get(edgeIndex);
        edgeToMutate.page = pageIndex;
        edgePartition.set(edgeIndex,edgeToMutate);
        genes.setEdgePartition(edgePartition);
    }

    public int evaluate() {
        int crossings = new KPMPSolutionChecker().getCrossingNumber(genes);
        this.setNumberOfCrossings(crossings);

        return fitnessValue;
    }
}
