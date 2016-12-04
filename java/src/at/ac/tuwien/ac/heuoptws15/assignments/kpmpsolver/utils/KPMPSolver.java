package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.combined.KPMPCombinedHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.combined.KPMPRandomizedMultiSolutionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.GeneticAlgorithm;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolver {

    private KPMPInstance instance;
    private KPMPSpineOrderHeuristic spineOrderHeuristic;
    private KPMPEdgePartitionHeuristic edgePartitionHeuristic;
    private List<KPMPSolutionWriter.PageEntry> originalEdgePartition;
    private KPMPLocalSearch localSearch;
    private StepFunction stepFunction;
    private int originalNumberOfCrossings;

    private HeuristicType heuristicType = HeuristicType.SEPARATED;

    public enum HeuristicType {
        SEPARATED, COMBINED
    }

    public KPMPSolver(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int originalNumberOfCrossings) {
        this.instance = instance;
        this.originalEdgePartition = originalEdgePartition;
        this.originalNumberOfCrossings = originalNumberOfCrossings;

        // set default heuristics
        this.spineOrderHeuristic = new KPMPSpineOrderDFSHeuristic();
        this.edgePartitionHeuristic = new KPMPEdgePartitionCFLHeuristic();
    }

    public void setHeuristicType(HeuristicType heuristicType) {
        this.heuristicType = heuristicType;
    }

    public void registerSpineOrderHeuristic(KPMPSpineOrderHeuristic spineOrderHeuristic) {
        this.spineOrderHeuristic = spineOrderHeuristic;
    }

    public void registerEdgePartitionHeuristic(KPMPEdgePartitionHeuristic edgePartitionHeuristic) {
        this.edgePartitionHeuristic = edgePartitionHeuristic;
    }

    public void registerLocalSearchImplementation(KPMPLocalSearch localSearch) {
        this.localSearch = localSearch;
    }

    public void registerStepFunction(StepFunction stepFunction) {
        this.stepFunction = stepFunction;
    }

    public KPMPSolution solve() {
        KPMPSolution solution = new KPMPSolution();
        if (Main.heuristicStrategy == HeuristicStrategy.GA) {
            solution = new GeneticAlgorithm(instance,originalEdgePartition).improve();
        } else {
            if (heuristicType == HeuristicType.SEPARATED) {
                List<Integer> calculatedSpineOrder = spineOrderHeuristic.calculateSpineOrder(instance,originalEdgePartition,originalNumberOfCrossings,false);
                solution.setSpineOrder(calculatedSpineOrder);
                solution.setEdgePartition(edgePartitionHeuristic.calculateEdgePartition(instance,calculatedSpineOrder,spineOrderHeuristic.getNumberOfCrossingsForNewSpineOrder()));
                solution.setNumberOfPages(instance.getK());
                solution.setSpineOrder(edgePartitionHeuristic.getSpineOrder());
            }else {
                KPMPCombinedHeuristic combinedHeuristic = new KPMPRandomizedMultiSolutionHeuristic();
                combinedHeuristic.calculateSpineOrder(instance,originalEdgePartition,originalNumberOfCrossings,false);
                solution.setNumberOfPages(instance.getK());
                solution.setEdgePartition(combinedHeuristic.calculateEdgePartition(instance,null,0));
                solution.setSpineOrder(combinedHeuristic.getSpineOrder());
            }
            solution = localSearch.improveSolution(solution, stepFunction);
        }

        return solution;
    }
}
