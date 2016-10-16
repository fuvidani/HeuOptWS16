package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl.KPMPSolutionWriter;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public interface KPMPEdgePartitionHeuristic {
    List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder);
}
