package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.FirstImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 30.10.16
 */
public class ThirdNeighbourHoodStructure extends AbstractKPMPLocalSearch {


    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     * The neighbour is picked randomly.
     *
     * @param stepFunction random step function
     *
     * @return next solution in the neighbourhood
     */
    @Override
    protected KPMPSolution nextNeighbour(RandomStepFunction stepFunction) {
        return bestSolution;
    }

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     * The neighbour is picked according to the implementation's
     * strategy for first-improvement.
     *
     * @param stepFunction first-improvement step function
     *
     * @return next solution in the neighbourhood
     */
    @Override
    protected KPMPSolution nextNeighbour(FirstImprovementStepFunction stepFunction) {
        return bestSolution;
    }

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     * The neighbour is picked according to the implementation's
     * strategy for best-improvement.
     *
     * @param stepFunction best-improvement step function
     *
     * @return next solution in the neighbourhood
     */
    @Override
    protected KPMPSolution nextNeighbour(BestImprovementStepFunction stepFunction) {
        return bestSolution;
    }

    /**
     * This method is used as the objective function and as the
     * decider whether the local search should continue or not.
     * The provided solution is evaluated and the method returns
     * false if the solution is good enough and no further iterations
     * are needed.
     *
     * @param generatedSolution this is a generated neighbour solution
     *                          as a result of a "move"
     *
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution) {
        return true;
    }
}
