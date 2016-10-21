package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 18.10.16
 */
public class KPMPSpineOrderRandomDFSHeuristic extends AbstractKPMPSpineOrderHeuristic {

    @Override
    protected List<Integer> calculateSpineOrder() {
        Random random = new Random(Double.doubleToLongBits(Math.random()));
        discoveredNodes = new ArrayList<>();
        spineOrder = new ArrayList<>();
        discoveredNodes.addAll(verticesWithoutNeighbours);
        spineOrder.addAll(verticesWithoutNeighbours);
        do {
            this.rootNodeIndex = random.nextInt(instance.getNumVertices());
        }while (verticesWithoutNeighbours.contains(rootNodeIndex));
        DFS(rootNodeIndex);
        return spineOrder;
    }


}
