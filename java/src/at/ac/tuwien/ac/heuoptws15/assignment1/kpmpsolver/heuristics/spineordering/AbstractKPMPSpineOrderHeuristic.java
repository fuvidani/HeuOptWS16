package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    protected List<KPMPSolutionWriter.PageEntry> originalEdgePartition;
    protected int originalNumberOfCrossings;
    protected final Logger LOGGER = Logger.getLogger(AbstractKPMPSpineOrderHeuristic.class.getName());

    /**
     * Calculates the new spine order depending
     * on the implementation strategy and returns
     * it.
     *
     * @return new spine order
     */
    protected abstract List<Integer> calculateSpineOrder();

    @Override
    public List<Integer> calculateSpineOrder(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartition, int originalNumberOfCrossings) {
        this.instance = instance;
        this.originalEdgePartition = originalEdgePartition;
        this.originalNumberOfCrossings = originalNumberOfCrossings;
        List<Integer> result = calculateSpineOrder();
        LOGGER.info("Spine Order calculated.");
        return result;
    }


    @Override
    public int getNumberOfCrossingsForNewSpineOrder() {
        return new KPMPSolutionChecker().getCrossingNumberOfPage(spineOrder,originalEdgePartition,0);
    }

}
