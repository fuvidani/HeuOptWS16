package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 30.10.16
 */
public interface KPMPLocalSearch {

    /**
     * Improves a previously constructed KPMP solution by
     * exploring neighbour solutions using local search.
     *
     * @param currentSolution the current solution that will
     *                        be optimized
     * @param stepFunction    defines the way the next neighbour is
     *                        selected (e.g. random, first-improvement,
     *                        best-improvement)
     *
     * @return a new KPMP solution that is at least good as the provided one
     */
    KPMPSolution improveSolution(KPMPSolution currentSolution, StepFunction stepFunction);

}
