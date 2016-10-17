package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.KPMPSpineOrderHeuristic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSpineOrderDFSHeuristic implements KPMPSpineOrderHeuristic {
    private KPMPInstance instance;
    private List<Integer> spineOrder = new ArrayList<>();
    private int rootNodeIndex;
    private List<Integer> discoveredNodes = new ArrayList<>();

    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance) {
        this.instance = instance;
        this.rootNodeIndex = instance.getNumVertices()/2;

        DFS(rootNodeIndex);

        return spineOrder;
    }

    private void DFS(int nodeIndex) {
        discoveredNodes.add(nodeIndex);
        spineOrder.add(nodeIndex);

        List<Integer> sortedNeighbours = instance.getAdjacencyList().get(nodeIndex);
        sortedNeighbours.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        for (Integer neighbourNode: sortedNeighbours) {
            if (!discoveredNodes.contains(neighbourNode)) {
                DFS(neighbourNode);
            }
        }
    }
}
