package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

/**
 * <h4>Abstract Spine Order Heuristic for the Interface</h4>
 *
 * @author Daniel Fuevesi
 * @author David Molnar
 * @version 1.0.0
 * @since 18.10.16
 */
public abstract class AbstractKPMPSpineOrderHeuristic implements KPMPSpineOrderHeuristic {

    protected KPMPInstance instance;
    protected List<Integer> spineOrder = new ArrayList<>();
    protected int rootNodeIndex;
    protected List<Integer> discoveredNodes = new ArrayList<>();
    private List<KPMPSolutionWriter.PageEntry> originalEdgePartition;
    private int originalNumberOfCrossings;
    private int numberOfCrossingsForNewSpineOrder = -1;
    private boolean forAllPages;
    protected List<Integer> verticesWithoutNeighbours;

    /**
     * Calculates the new spine order depending
     * on the implementation strategy and returns
     * it.
     *
     * @return new spine order
     */
    protected abstract List<Integer> calculateSpineOrder();

    /**
     * (Pseudo-)Random depth-first search.
     * Neighbour vertices are picked randomly
     * and only if they haven't already been visited before.
     *
     * @param nodeIndex index of the current node/vertex
     */
    protected void DFS(int nodeIndex) {
        discoveredNodes.add(nodeIndex);
        spineOrder.add(nodeIndex);

        List<Integer> sortedNeighbours = instance.getAdjacencyList().get(nodeIndex);
        sortedNeighbours.sort((Comparator.comparingInt(o -> o)));

        for (Integer neighbourNode : sortedNeighbours) {
            if (!discoveredNodes.contains(neighbourNode)) {
                DFS(neighbourNode);
            }
        }
    }

    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int originalNumberOfCrossings, boolean forAllPages) {
        this.instance = instance;
        this.originalEdgePartition = originalEdgePartition;
        this.originalNumberOfCrossings = originalNumberOfCrossings;
        this.forAllPages = forAllPages;
        List<List<Integer>> adjacencyList = instance.getAdjacencyList().stream().collect(toCollection(ArrayList::new));
        verticesWithoutNeighbours = new ArrayList<>();
        for (int index = 0; index < adjacencyList.size(); index++) {
            if (adjacencyList.get(index).isEmpty()) {
                verticesWithoutNeighbours.add(index);
            }
        }
        return calculateSpineOrder();
    }

    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int rootIndex) {
        this.instance = instance;
        this.originalEdgePartition = originalEdgePartition;
        List<List<Integer>> adjacencyList = instance.getAdjacencyList().stream().collect(toCollection(ArrayList::new));
        verticesWithoutNeighbours = new ArrayList<>();
        for (int index = 0; index < adjacencyList.size(); index++) {
            if (adjacencyList.get(index).isEmpty()) {
                verticesWithoutNeighbours.add(index);
            }
        }
        discoveredNodes = new ArrayList<>();
        spineOrder = new ArrayList<>();
        discoveredNodes.addAll(verticesWithoutNeighbours);
        spineOrder.addAll(verticesWithoutNeighbours);
        DFS(rootIndex);
        return spineOrder;
    }


    @Override
    public int getNumberOfCrossingsForNewSpineOrder() {
        if (numberOfCrossingsForNewSpineOrder == -1) {
            return new KPMPSolutionChecker().getCrossingNumberOfPage(spineOrder, originalEdgePartition, 0);
        } else {
            return numberOfCrossingsForNewSpineOrder;
        }
    }

}
