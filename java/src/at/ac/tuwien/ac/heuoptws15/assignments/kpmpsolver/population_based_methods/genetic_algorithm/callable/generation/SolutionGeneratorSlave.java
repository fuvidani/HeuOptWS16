package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.generation;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 07.12.2016
 */
public class SolutionGeneratorSlave implements SolutionGenerationCallable {

    private int solutionsToGenerate;
    private KPMPInstance instance;
    private List<KPMPSolutionWriter.PageEntry> edgePartitioning;

    @Override
    public void setNumberOfSolutionsToGenerate(int solutionsToGenerate) {
        this.solutionsToGenerate = solutionsToGenerate;
    }

    @Override
    public void setBaseInstance(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> edgePartitioning) {
        this.instance = instance;
        this.edgePartitioning = edgePartitioning;
    }

    @Override
    public List<Individual> call() throws Exception {
        KPMPSpineOrderHeuristic spineOrderHeuristic;
        KPMPEdgePartitionHeuristic edgePartitionHeuristic;
        List<Individual> result = new ArrayList<>(solutionsToGenerate);
        for (int i = 0; i < solutionsToGenerate; i++) {
            spineOrderHeuristic = new KPMPSpineOrderRandomDFSHeuristic();
            edgePartitionHeuristic = new KPMPEdgePartitionCFLHeuristic();
            KPMPSolution solution = new KPMPSolution();
            KPMPInstance instance1 = new KPMPInstance(instance);
            List<KPMPSolutionWriter.PageEntry> edgePartitioning1 = edgePartitioning.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(Collectors.toCollection(ArrayList::new));
            List<Integer> calculatedSpineOrder = spineOrderHeuristic.calculateSpineOrder(instance1, edgePartitioning1, instance1.getK(), false);
            solution.setSpineOrder(calculatedSpineOrder);
            solution.setEdgePartition(edgePartitionHeuristic.calculateEdgePartition(instance1, calculatedSpineOrder, spineOrderHeuristic.getNumberOfCrossingsForNewSpineOrder()));
            solution.setNumberOfPages(instance1.getK());
            solution.setSpineOrder(edgePartitionHeuristic.getSpineOrder());

            Individual individual = new Individual();
            individual.setGenes(solution);
            result.add(individual);
        }
        return result;
    }


}
