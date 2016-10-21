package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSpineOrderDFSHeuristic extends AbstractKPMPSpineOrderHeuristic {

    @Override
    protected List<Integer> calculateSpineOrder() {
        spineOrder.addAll(verticesWithoutNeighbours);
        discoveredNodes.addAll(verticesWithoutNeighbours);
        this.rootNodeIndex = instance.getNumVertices()/2;
        DFS(rootNodeIndex);
        return spineOrder;
    }
}
