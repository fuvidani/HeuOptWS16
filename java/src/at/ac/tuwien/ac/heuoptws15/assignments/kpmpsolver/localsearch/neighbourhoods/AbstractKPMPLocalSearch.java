package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.FirstImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;

import java.util.Random;

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
    protected int bestCrossingNumber;
    protected Random random;
    private StepFunction stepFunction;

    @Override
    public KPMPSolution improveSolution(KPMPSolution currentSolution, StepFunction stepFunction) {
        initialSolution = currentSolution;
        bestSolution = initialSolution;
        bestCrossingNumber = new KPMPSolutionChecker().getCrossingNumber(currentSolution);
        this.stepFunction = stepFunction;
        System.out.println("Crossings before local search: " + bestCrossingNumber);
        return doTheHarlemShake();
    }

    protected KPMPSolution doTheHarlemShake() {
        KPMPSolution neighbourSolution;
        beforeSearch();
        do {
            neighbourSolution = nextNeighbour(stepFunction);
        } while (!stoppingCriteriaSatisfied(neighbourSolution, stepFunction));
        return bestSolution;
    }

    private KPMPSolution nextNeighbour(StepFunction stepFunction) {
        if (stepFunction instanceof RandomStepFunction) {
            return randomNextNeighbour();
        }
        return nextNeighbour();
    }

    private boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, StepFunction stepFunction) {
        if (stepFunction instanceof RandomStepFunction) {
            return stoppingCriteriaSatisfied(generatedSolution, (RandomStepFunction) stepFunction);
        } else if (stepFunction instanceof FirstImprovementStepFunction) {
            return stoppingCriteriaSatisfied(generatedSolution, (FirstImprovementStepFunction) stepFunction);
        } else {
            return stoppingCriteriaSatisfied(generatedSolution, (BestImprovementStepFunction) stepFunction);
        }
    }

    /**
     * This abstract method gives the implementing
     * classes the opportunity to do other operations
     * (initialization, custom data structures, etc.)
     * before beginning with the local search.
     * <p>
     * The implementation can simply be left empty
     * if there is no such need.
     */
    protected abstract void beforeSearch();

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     *
     * @return next solution in the neighbourhood
     */
    protected abstract KPMPSolution nextNeighbour();

    /**
     * Returns the next solution in the neighbourhood by
     * picking a random solution.
     *
     * @return next random solution in the neighbourhood
     */
    protected abstract KPMPSolution randomNextNeighbour();

    /**
     * This method is used as the objective function and as the
     * decider whether the local search should continue or not.
     * The provided solution is evaluated and the method returns
     * false if the solution is good enough and no further iterations
     * are needed.
     *
     * @param generatedSolution this is a generated neighbour solution
     *                          as a result of a "move"
     * @param stepFunction random step function meaning the stopping
     *                     criteria may be a time limit, a number of
     *                     iterations or a known upper or lower bound
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    protected abstract boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, RandomStepFunction stepFunction);

    /**
     * This method is used as the objective function and as the
     * decider whether the local search should continue or not.
     * The provided solution is evaluated and the method returns
     * false if the solution is good enough and no further iterations
     * are needed.
     *
     * @param generatedSolution this is a generated neighbour solution
     *                          as a result of a "move"
     * @param stepFunction      first-improvement step function meaning if
     *                          the generated solution is better than the original
     *                          one true is returned immediately
     *
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    protected abstract boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, FirstImprovementStepFunction stepFunction);

    /**
     * This method is used as the objective function and as the
     * decider whether the local search should continue or not.
     * The provided solution is evaluated and the method returns
     * false if the solution is good enough and no further iterations
     * are needed.
     *
     * @param generatedSolution this is a generated neighbour solution
     *                          as a result of a "move"
     * @param stepFunction      best-improvement step function meaning that
     *                          this will let to have the full neighbourhood
     *                          searched for the local optimum
     *
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    protected abstract boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, BestImprovementStepFunction stepFunction);
}
