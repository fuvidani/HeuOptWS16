package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSpineOrderDFSHeuristic extends AbstractKPMPSpineOrderHeuristic {

    @Override
    protected List<Integer> calculateSpineOrder() {
        //coverBasedVertexOrdering();
        spineOrder.addAll(verticesWithoutNeighbours);
        discoveredNodes.addAll(verticesWithoutNeighbours);
        this.rootNodeIndex = instance.getNumVertices()/2;
        DFS(rootNodeIndex);
        return spineOrder;
    }

    private void coverBasedVertexOrdering(){
        HashMap<Integer, Integer> sortedVertices = new HashMap<>(instance.getNumVertices());
        for (int i = 0; i < instance.getNumVertices(); i++){
            sortedVertices.put(i,instance.getAdjacencyList().get(i).size());
        }

        sortedVertices = sortByValue(sortedVertices);
        spineOrder = sortedVertices.keySet().stream().collect(toCollection(ArrayList::new));
    }

    private HashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> map) {
        return map.entrySet()
                       .stream()
                       .sorted(Map.Entry.comparingByValue())
                       .collect(Collectors.toMap(
                               Map.Entry::getKey,
                               Map.Entry::getValue,
                               (e1, e2) -> e1,
                               LinkedHashMap::new
                       ));
    }
}
