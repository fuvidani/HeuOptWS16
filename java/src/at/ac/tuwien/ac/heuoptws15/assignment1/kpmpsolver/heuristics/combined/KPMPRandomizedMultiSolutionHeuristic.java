package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.combined;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 21.10.16
 */
public class KPMPRandomizedMultiSolutionHeuristic implements KPMPCombinedHeuristic{

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
        int numberOfSolutions;
        if (instance.getNumVertices() < 100){
            numberOfSolutions = 50;
        }else if (instance.getNumVertices() == 200){
            numberOfSolutions = 10;
        }else {
            numberOfSolutions = 10;
        }
        for (int i = 0; i < numberOfSolutions; i++){
            List<Integer> spineOrder = spineOrderHeuristic.calculateSpineOrder(instance,originalEdgePartition,originalNumberOfCrossings,forAllPages);
            spineOrders.add(spineOrder);
        }
        System.out.println(numberOfSolutions + " Spine orders calculated.");
        return spineOrders.get(0);
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
        HashMap<List<KPMPSolutionWriter.PageEntry>,List<Integer>> edgePartitions = new HashMap<>();
        int remainingSolutions = spineOrders.size();
        long start = System.nanoTime();
        for (List<Integer> spine: spineOrders){
            if (((System.nanoTime() - start) / 1000000000) < Main.secondsBeforeStop) {
                List<KPMPSolutionWriter.PageEntry> edgePartition = edgePartitionHeuristic.calculateEdgePartition(instance, spine, currentNumberOfCrossings);
                edgePartitions.put(edgePartition, spine);
                remainingSolutions--;
            } else {
                System.out.println("Skipping " + remainingSolutions + " solutions due to timeout");
                break;
            }
        }
        List<KPMPSolutionWriter.PageEntry> bestSolution = new ArrayList<>();
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        int bestNumberOfCrossings = Integer.MAX_VALUE;
        for (List<KPMPSolutionWriter.PageEntry> edgePartition: edgePartitions.keySet()){
            List<Integer> spine = edgePartitions.get(edgePartition);
            int crossings = checker.getCrossingNumber(new KPMPSolution(spine,edgePartition,instance.getK()));
            if (crossings < bestNumberOfCrossings){
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
