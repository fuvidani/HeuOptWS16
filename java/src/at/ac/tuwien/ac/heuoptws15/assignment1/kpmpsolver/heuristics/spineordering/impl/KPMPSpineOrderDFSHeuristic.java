package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSpineOrderDFSHeuristic extends AbstractKPMPSpineOrderHeuristic {

    @Override
    protected List<Integer> calculateSpineOrder() {
        List<List<Integer>> adjacencyList = instance.getAdjacencyList().stream().collect(toCollection(ArrayList::new));
        for (int index = 0; index < adjacencyList.size(); index++){
            if (adjacencyList.get(index).isEmpty()){
                spineOrder.add(index);
                discoveredNodes.add(index);
            }
        }
        this.rootNodeIndex = instance.getNumVertices()/2;
        DFS(rootNodeIndex);
        return spineOrder;
    }

    /**
     * Deterministic depth-first search.
     * Neighbour vertices are sorted in descending order
     * and the next unvisited one is picked.
     * @param nodeIndex index of the current node/vertex
     */
    private void DFS(int nodeIndex) {
        discoveredNodes.add(nodeIndex);
        spineOrder.add(nodeIndex);

        List<Integer> sortedNeighbours = instance.getAdjacencyList().get(nodeIndex);
        sortedNeighbours.sort(((o1, o2) -> o1 - o2));

        for (Integer neighbourNode: sortedNeighbours) {
            if (!discoveredNodes.contains(neighbourNode)) {
                DFS(neighbourNode);
            }
        }
    }
}
