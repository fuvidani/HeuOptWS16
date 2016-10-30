package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.FirstImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
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
public abstract class AbstractKPMPLocalSearch implements KPMPLocalSearch {

    protected KPMPSolution initialSolution;
    protected KPMPSolution bestSolution;
    private StepFunction stepFunction;

    @Override
    public KPMPSolution improveSolution(KPMPSolution currentSolution, StepFunction stepFunction) {
        initialSolution = currentSolution;
        bestSolution = initialSolution;
        this.stepFunction = stepFunction;
        return doTheHarlemShake();
    }

    protected KPMPSolution doTheHarlemShake() {
        KPMPSolution neighbourSolution;
        do {
            neighbourSolution = nextNeighbour(stepFunction);
        } while (!stoppingCriteriaSatisfied(neighbourSolution));
        return bestSolution;
    }

    private KPMPSolution nextNeighbour(StepFunction stepFunction) {
        if (stepFunction instanceof RandomStepFunction) {
            return nextNeighbour((RandomStepFunction) stepFunction);
        } else if (stepFunction instanceof FirstImprovementStepFunction) {
            return nextNeighbour((FirstImprovementStepFunction) stepFunction);
        } else {
            return nextNeighbour((BestImprovementStepFunction) stepFunction);
        }
    }

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     * The neighbour is picked randomly.
     *
     * @param stepFunction random step function
     *
     * @return next solution in the neighbourhood
     */
    protected abstract KPMPSolution nextNeighbour(RandomStepFunction stepFunction);

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
    protected abstract KPMPSolution nextNeighbour(FirstImprovementStepFunction stepFunction);

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
    protected abstract KPMPSolution nextNeighbour(BestImprovementStepFunction stepFunction);

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
    protected abstract boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution);
}
