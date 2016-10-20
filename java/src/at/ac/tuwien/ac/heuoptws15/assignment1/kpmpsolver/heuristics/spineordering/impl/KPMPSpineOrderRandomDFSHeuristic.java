package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

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
        List<List<Integer>> adjacencyList = instance.getAdjacencyList().stream().collect(toCollection(ArrayList::new));
        List<Integer> verticesWithoutNeighbours = new ArrayList<>();
        for (int index = 0; index < adjacencyList.size(); index++){
            if (adjacencyList.get(index).isEmpty()){
                verticesWithoutNeighbours.add(index);
            }
        }
        List<Integer> bestSpineOrder = null;
        int bestNumberOfCrossings = originalNumberOfCrossings;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int time = forAllPages ? 60 : 30;
        long start = System.nanoTime();
        while (((System.nanoTime()-start)/1000000000) < time){
            this.random = new Random(System.currentTimeMillis());
            discoveredNodes = new ArrayList<>();
            spineOrder = new ArrayList<>();
            discoveredNodes.addAll(verticesWithoutNeighbours);
            spineOrder.addAll(verticesWithoutNeighbours);
            do {
                this.rootNodeIndex = random.nextInt(instance.getNumVertices());
            }while (verticesWithoutNeighbours.contains(rootNodeIndex));
            DFS(rootNodeIndex);
            if (bestSpineOrder == null)bestSpineOrder = spineOrder;
            int numberOfCrossings;
            if (forAllPages){
                numberOfCrossings = solutionChecker.getCrossingNumber(new KPMPSolution(spineOrder,originalEdgePartition,instance.getK()));
            }else {
                numberOfCrossings = solutionChecker.getCrossingNumberOfPage(spineOrder, originalEdgePartition, 0);
            }
            if (numberOfCrossings < bestNumberOfCrossings){
                bestNumberOfCrossings = numberOfCrossings;
                numberOfCrossingsForNewSpineOrder = numberOfCrossings;
                bestSpineOrder = spineOrder;
            }
        }
        if (bestNumberOfCrossings == originalNumberOfCrossings){
            numberOfCrossingsForNewSpineOrder = originalNumberOfCrossings;
        }
        if (forAllPages){
            System.out.println("Number of crossings across " + instance.getK() + " pages after spine order calculation: " + numberOfCrossingsForNewSpineOrder);
        }else {
            System.out.println("Number of crossings on 1 page after spine order calculation: " + numberOfCrossingsForNewSpineOrder);
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
