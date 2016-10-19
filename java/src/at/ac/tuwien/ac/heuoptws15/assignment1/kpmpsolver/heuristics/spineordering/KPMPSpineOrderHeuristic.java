package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.List;

/**
 * <p>Spine Order Heuristic for K-Page Minimization Problem</p>
 *
 * @author David Molnar
 * @author Daniel Fuevesi
 * @since 16.10.2016
 */
public interface KPMPSpineOrderHeuristic {

    /**
     * This method returns a spine order for a
     * given graph in a KPMP instance.
     * The way the resulting spine order is calculated
     * depends on the implementation of this interface.
     * Nevertheless the result is a valid order of
     * the initial vertices.
     *
     * @param instance an instance of the K-page minimization
     *                 problem
     * @param originalEdgePartition the initial edge partitioning
     *                              of the graph
     * @param originalNumberOfCrossings the initial number of crossings
     *                                  the graph has
     * @return valid order of vertices as the spine of the "book"
     */
    List<Integer> calculateSpineOrder(KPMPInstance instance,  List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int originalNumberOfCrossings);

    /**
     * Returns the number of crossings the
     * re-ordered spine induces.
     *
     * If the result is low enough (close to or equal to 0),
     * there is no need to rearrange edges.
     *
     * @return number of crossings (>= 0)
     */
    int getNumberOfCrossingsForNewSpineOrder();
}
