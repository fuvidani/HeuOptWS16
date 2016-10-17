package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl.KPMPInstance;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public interface KPMPSpineOrderHeuristic {
    List<Integer> calculateSpineOrder(KPMPInstance instance);
}
