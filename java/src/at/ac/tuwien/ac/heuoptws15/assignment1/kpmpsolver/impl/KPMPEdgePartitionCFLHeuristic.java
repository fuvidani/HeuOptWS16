package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.KPMPEdgePartitionHeuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPEdgePartitionCFLHeuristic implements KPMPEdgePartitionHeuristic {
    private HashMap<KPMPSolutionWriter.PageEntry, Integer> edgeConflictMap = new HashMap<>();
    private KPMPInstance instance;
    private List<Integer> spineOrder;
    private List<Integer> discoveredNodes;

    @Override
    public List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder) {
        this.instance = instance;
        this.spineOrder = spineOrder;
        this.discoveredNodes = new ArrayList<>();

        this.calculateConflicts();



        return null;
    }

    private void moveEdges() {
        int maxValue = 0;
        KPMPSolutionWriter.PageEntry currentEdge = null;
        for (KPMPSolutionWriter.PageEntry edge: edgeConflictMap.keySet()) {
            if (edgeConflictMap.get(edge) > maxValue && !discoveredNodes.contains(edge)) {
                maxValue = edgeConflictMap.get(edge);
                currentEdge = edge;
            }
        }


    }

    private void calculateConflicts() {
        List<List<Integer>> adjacencyList = instance.getAdjacencyList();
        List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();
        for (int row = 0; row < adjacencyList.size(); row++) {
            for (int j = 0; j < adjacencyList.get(row).size(); j++) {
                if (adjacencyList.get(row).get(j) > row) {
                    edges.add(new KPMPSolutionWriter.PageEntry(row, adjacencyList.get(row).get(j), 0));
                }
            }
        }

        for (KPMPSolutionWriter.PageEntry edge: edges) {
            edgeConflictMap.put(edge,0);
        }

        for (Integer index: spineOrder) {
            for (KPMPSolutionWriter.PageEntry edge1: edges) {

                if (edge1.a == index) {
                    for (KPMPSolutionWriter.PageEntry edge2: edges) {
                        if (spineOrder.indexOf(edge2.a) > spineOrder.indexOf(edge1.a)) {
                            if (spineOrder.indexOf(edge1.a) < spineOrder.indexOf(edge2.a) && spineOrder.indexOf(edge1.a) < spineOrder.indexOf(edge1.b) && spineOrder.indexOf(edge1.a) < spineOrder.indexOf(edge2.b) && spineOrder.indexOf(edge2.a) < spineOrder.indexOf(edge1.b) && spineOrder.indexOf(edge2.a) < spineOrder.indexOf(edge2.b) && spineOrder.indexOf(edge1.b) < spineOrder.indexOf(edge2.b)){
                                edgeConflictMap.put(edge2, edgeConflictMap.get(edge2) + 1);
                            }
                        }
                    }
                }
            }
        }

        System.out.println();
    }
}
