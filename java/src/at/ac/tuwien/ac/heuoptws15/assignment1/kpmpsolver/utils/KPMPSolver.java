package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.combined.KPMPCombinedHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.combined.KPMPRandomizedMultiSolutionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl.KPMPSpineOrderDFSHeuristic;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolver {

    private KPMPInstance instance;
    private KPMPSpineOrderHeuristic spineOrderHeuristic;
    private KPMPEdgePartitionHeuristic edgePartitionHeuristic;
    private List<KPMPSolutionWriter.PageEntry> originalEdgePartition;
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

    public KPMPSolution solve() {
        KPMPSolution solution = new KPMPSolution();

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

        return solution;
    }
}
