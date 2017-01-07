package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.List;

/**
 * <h4>About this class</h4>
 * <p>
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.0.1
 * @since 07.01.2017
 */
public interface HybridAlgorithm {

    KPMPSolution improve(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartitioning);

    void registerLocalSearch(KPMPLocalSearch localSearch, StepFunction stepFunction);

}
