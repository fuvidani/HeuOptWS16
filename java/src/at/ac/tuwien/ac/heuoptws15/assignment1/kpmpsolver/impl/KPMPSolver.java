package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.KPMPSpineOrderHeuristic;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolver {
    private KPMPInstance instance;
    private KPMPSpineOrderHeuristic spineOrderHeuristic;
    private KPMPEdgePartitionHeuristic edgePartitionHeuristic;

    public static HeuristicType heuristicType = HeuristicType.SEPARATED;

    private enum HeuristicType {
        SEPARATED, COMBINED
    }

    public KPMPSolver(KPMPInstance instance) {
        this.instance = instance;

        // set default heuristics
        this.spineOrderHeuristic = new KPMPSpineOrderDFSHeuristic();
        this.edgePartitionHeuristic = new KPMPEdgePartitionCFLHeuristic();
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
            List<Integer> calculatedSpineOrder = spineOrderHeuristic.calculateSpineOrder(instance);
            solution.setSpineOrder(calculatedSpineOrder);
            solution.setEdgePartition(edgePartitionHeuristic.calculateEdgePartition(instance,calculatedSpineOrder));
            solution.setNumberOfPages(instance.getK());
        }

        return solution;
    }
}
