package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public interface KPMPEdgePartitionHeuristic {

    /**
     * Optimizes the edge distribution on a given
     * spine of vertices along a number of pages
     * defined in the problem instance.
     *
     * The algorithm has a 13 minute-limit i.e.
     * the heuristic automatically returns the last
     * solution if the runtime exceeds 13 minutes.
     *
     * @param instance a K-page minimization problem
     *                 instance
     * @param spineOrder a valid order of vertices being
     *                   the spine of the graph
     * @return an edge partitioning with much less crossings
     * than the original one; best-case: partitioning with
     * the minimum number of crossings
     */
    List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder);
}
