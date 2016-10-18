package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.AbstractKPMPSpineOrderHeuristic;

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

    private Random random;

    public KPMPSpineOrderRandomDFSHeuristic() {
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    protected List<Integer> calculateSpineOrder() {
        List<Integer> bestSpineOrder = null;
        int bestNumberOfCrossings = originalNumberOfCrossings;
        long start = System.nanoTime();
        while (((System.nanoTime()-start)/1000000000) < 30){
            this.random = new Random(System.currentTimeMillis());
            discoveredNodes = new ArrayList<>();
            spineOrder = new ArrayList<>();
            this.rootNodeIndex = random.nextInt(instance.getNumVertices());
            DFS(rootNodeIndex);
            if (bestSpineOrder == null)bestSpineOrder = spineOrder;
            int numberOfCrossings = getNumberOfCrossingsForNewSpineOrder();
            if (numberOfCrossings < bestNumberOfCrossings){
                bestNumberOfCrossings = numberOfCrossings;
                bestSpineOrder = spineOrder;
            }
        }
        return bestSpineOrder;
    }

    /**
     * (Pseudo-)Random depth-first search.
     * Neighbour vertices are picked randomly
     * and only if they haven't already been visited before.
     * @param nodeIndex index of the current node/vertex
     */
    private void DFS(int nodeIndex) {
        discoveredNodes.add(nodeIndex);
        spineOrder.add(nodeIndex);

        while (discoveredNodes.size() != instance.getNumVertices()){
            int randomNeighbour = random.nextInt(instance.getNumVertices());
            if (!discoveredNodes.contains(randomNeighbour)){
                DFS(randomNeighbour);
            }
        }
    }
}
