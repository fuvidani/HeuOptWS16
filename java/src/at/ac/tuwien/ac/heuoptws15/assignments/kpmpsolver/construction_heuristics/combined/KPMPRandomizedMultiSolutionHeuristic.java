package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.combined;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 21.10.16
 */
public class KPMPRandomizedMultiSolutionHeuristic implements KPMPCombinedHeuristic {

    private List<Integer> bestSpineOrder;
    private List<List<Integer>> spineOrders;
    private KPMPSpineOrderHeuristic spineOrderHeuristic;
    private KPMPEdgePartitionHeuristic edgePartitionHeuristic;

    public KPMPRandomizedMultiSolutionHeuristic() {
        this.spineOrderHeuristic = new KPMPSpineOrderRandomDFSHeuristic();
        this.edgePartitionHeuristic = new KPMPEdgePartitionRandomHeuristic();
    }

    /**
     * This method returns a spine order for a
     * given graph in a KPMP instance.
     * The way the resulting spine order is calculated
     * depends on the implementation of this interface.
     * Nevertheless the result is a valid order of
     * the initial vertices.
     *
     * @param instance                  an instance of the K-page minimization
     *                                  problem
     * @param originalEdgePartition     the initial edge partitioning
     *                                  of the graph
     * @param originalNumberOfCrossings the initial number of crossings
     *                                  the graph has
     * @param forAllPages               flag to indicate whether the number of crossings
     *                                  should be calculated only for the first (0) page
     *                                  or across all pages
     *
     * @return valid order of vertices as the spine of the "book"
     */
    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int originalNumberOfCrossings, boolean forAllPages) {
        spineOrders = new ArrayList<>();
        List<Integer> verticesWithoutNeighbours = new ArrayList<>();
        List<List<Integer>> adjacencyList = instance.getAdjacencyList().stream().collect(toCollection(ArrayList::new));
        for (int index = 0; index < adjacencyList.size(); index++) {
            if (adjacencyList.get(index).isEmpty()) {
                verticesWithoutNeighbours.add(index);
            }
        }
        int numberOfSolutions;
        if (instance.getNumVertices() < 100) {
            numberOfSolutions = instance.getNumVertices() - verticesWithoutNeighbours.size();
        } else if (instance.getNumVertices() == 200) {
            numberOfSolutions = 1;
        } else {
            numberOfSolutions = 1;
        }

        List<Integer> uniqueRootIndices = new ArrayList<>(numberOfSolutions);
        Random random = new Random(Double.doubleToLongBits(Math.random()));
        boolean deterministicAdded = false;
        while (uniqueRootIndices.size() != numberOfSolutions) {
            int index;
            if (!deterministicAdded) {
                index = instance.getNumVertices() / 2;
                deterministicAdded = true;
            } else {
                index = random.nextInt(instance.getNumVertices());
            }
            if (!uniqueRootIndices.contains(index) && !verticesWithoutNeighbours.contains(index)) {
                uniqueRootIndices.add(index);
            }
        }
        for (int i = 0; i < numberOfSolutions; i++) {
            List<Integer> spineOrder = spineOrderHeuristic.calculateSpineOrder(instance, originalEdgePartition, uniqueRootIndices.get(i));
            spineOrders.add(spineOrder);
        }
        System.out.println(numberOfSolutions + " Spine orders calculated.");
        return spineOrders.get(0);
    }

    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int rootIndex) {
        return null;
    }

    /**
     * Returns the number of crossings the
     * re-ordered spine induces.
     * <p>
     * If the result is low enough (close to or equal to 0),
     * there is no need to rearrange edges.
     *
     * @return number of crossings (>= 0)
     */
    @Override
    public int getNumberOfCrossingsForNewSpineOrder() {
        return 0;
    }


    /**
     * Optimizes the edge distribution on a given
     * spine of vertices along a number of pages
     * defined in the problem instance.
     * <p>
     * The algorithm has a 13 minute-limit i.e.
     * the heuristic automatically returns the last
     * solution if the runtime exceeds 13 minutes.
     *
     * @param instance                 a K-page minimization problem
     *                                 instance
     * @param spineOrder               a valid order of vertices being
     *                                 the spine of the graph
     * @param currentNumberOfCrossings the number of crossings
     *                                 the current spine order
     *                                 and edge partitioning
     *                                 have
     *
     * @return an edge partitioning with much less crossings
     * than the original one; best-case: partitioning with
     * the minimum number of crossings
     */
    @Override
    public List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder, int currentNumberOfCrossings) {
        HashMap<List<KPMPSolutionWriter.PageEntry>, List<Integer>> edgePartitions = new HashMap<>();
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        int remainingSolutions = spineOrders.size();
        for (List<Integer> spine : spineOrders) {
            if (((System.nanoTime() - Main.START) / 1000000000) < Main.secondsBeforeStop) {
                List<KPMPSolutionWriter.PageEntry> edgePartition = edgePartitionHeuristic.calculateEdgePartition(instance, spine, currentNumberOfCrossings);
                edgePartitions.put(edgePartition, spine);
                remainingSolutions--;
            } else {
                System.out.println("Skipping " + remainingSolutions + " solutions due to timeout");
                break;
            }
        }
        List<KPMPSolutionWriter.PageEntry> bestSolution = new ArrayList<>();
        int bestNumberOfCrossings = Integer.MAX_VALUE;
        for (List<KPMPSolutionWriter.PageEntry> edgePartition : edgePartitions.keySet()) {
            List<Integer> spine = edgePartitions.get(edgePartition);
            int crossings = checker.getCrossingNumber(new KPMPSolution(spine, edgePartition, instance.getK()));
            if (crossings < bestNumberOfCrossings) {
                bestNumberOfCrossings = crossings;
                bestSolution = edgePartition;
            }
        }
        bestSpineOrder = edgePartitions.get(bestSolution);
        return bestSolution;
    }

    /**
     * Returns the spine order of the current solution.
     * <p>
     * The implementation of the algorithm may re-arrange
     * the spine order that, is why the caller needs
     * to be able to obtain the new order. Otherwise
     * it simply remains the same.
     *
     * @return list of integers being the
     * spine of the graph
     */
    @Override
    public List<Integer> getSpineOrder() {
        return bestSpineOrder;
    }

}
