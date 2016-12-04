package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

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
public class Individual {
    //public static final int SIZE = 500;
    private KPMPSolution genes;
    //private int[] genes = new int[SIZE];
    private int fitnessValue;

    public Individual() {
    }

    public int getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(int fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public KPMPSolution getGenes() {
        return genes;
    }

    public void setGenes(KPMPSolution genes) {
        this.genes = genes;
    }

/*    public void randGenes() {
        Random rand = new Random();
        for (int i = 0; i < SIZE; ++i) {
            this.setGene(i, rand.nextInt(2));
        }
    }*/

    public void mutate() {
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
        int fitness = new KPMPSolutionChecker().getCrossingNumber(genes);
        this.setFitnessValue(fitness);

        return fitness;
    }
}
