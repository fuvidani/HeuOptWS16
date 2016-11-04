package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.FirstImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 30.10.16
 */
public class NodeSwap extends AbstractKPMPLocalSearch {

    private int firstIndex;
    private int secondIndex;
    private int localBestCrossingNumber;
    private int numberOfIterations;

    /**
     * This abstract method gives the implementing
     * classes the opportunity to do other operations
     * (initialization, custom data structures, etc.)
     * before beginning with the local search.
     * <p>
     * The implementation can simply be left empty
     * if there is no such need.
     */
    @Override
    protected void beforeSearch() {
        localBestCrossingNumber = bestCrossingNumber;
        numberOfIterations = 0;
        firstIndex = 0;
        secondIndex = 1;
    }

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     *
     * @return next solution in the neighbourhood
     */
    @Override
    protected KPMPSolution nextNeighbour() {
        List<Integer> currentSpineOrder = bestSolution.getSpineOrder().stream().collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(currentSpineOrder, bestSolution.getEdgePartition(), bestSolution.getNumberOfPages());
        if (secondIndex < currentSpineOrder.size()) {
            Collections.swap(currentSpineOrder, firstIndex, secondIndex);
            secondIndex++;
        } else {
            firstIndex++;
            secondIndex = firstIndex + 1;
            Collections.swap(currentSpineOrder, firstIndex, secondIndex);
        }
        neighbourSolution.setSpineOrder(currentSpineOrder);
        return neighbourSolution;
    }

    /**
     * Returns the next solution in the neighbourhood by
     * picking a random solution.
     *
     * @return next random solution in the neighbourhood
     */
    @Override
    protected KPMPSolution randomNextNeighbour() {
        random = new Random(Double.doubleToLongBits(Math.random()));

        /* Copy spine order of best solution */
        List<Integer> currentSpineOrder = bestSolution.getSpineOrder().stream().collect(toCollection(ArrayList::new));

        /* Instantiate neighbour solution */
        KPMPSolution neighbourSolution = new KPMPSolution(currentSpineOrder, bestSolution.getEdgePartition(), bestSolution.getNumberOfPages());

        /* Pick 2 different random indices */
        int firstRandom = random.nextInt(currentSpineOrder.size());
        int secondRandom;
        do {
            secondRandom = random.nextInt(currentSpineOrder.size());
        } while (firstRandom == secondRandom);

        /* Swap the 2 vertices on those random indices */
        Collections.swap(currentSpineOrder, firstRandom, secondRandom);

        /* Set the new spine order and return neighbour solution*/
        neighbourSolution.setSpineOrder(currentSpineOrder);
        return neighbourSolution;
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
     * @param stepFunction      random step function meaning the stopping
     *                          criteria may be a time limit, a number of
     *                          iterations or a known upper or lower bound
     *
     * @return true if another iteration should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, RandomStepFunction stepFunction) {
        numberOfIterations++;
        // TODO incremental evaluation instead of checker
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            System.out.println("Improvement (" + crossingNumber + ")");
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
        }
        return numberOfIterations >= (generatedSolution.getSpineOrder().size() * Main.iterationMultiplier) || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
     * @param stepFunction      first-improvement step function meaning if
     *                          the generated solution is better than the original
     *                          one true is returned immediately
     *
     * @return true if another iteration should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, FirstImprovementStepFunction stepFunction) {
        // TODO incremental evaluation instead of checker
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            System.out.println("Improvement (" + crossingNumber + ")");
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            return true;
        }
        return (firstIndex == generatedSolution.getSpineOrder().size() - 2 && secondIndex == generatedSolution.getSpineOrder().size() - 1) || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
     * @param stepFunction      best-improvement step function meaning that
     *                          this will let to have the full neighbourhood
     *                          searched for the local optimum
     *
     * @return true if another iteration should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, BestImprovementStepFunction stepFunction) {
        // TODO incremental evaluation instead of checker
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            System.out.println("Improvement (" + crossingNumber + ")");
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            firstIndex = 0;
            secondIndex = 1;
        }
        return (firstIndex == generatedSolution.getSpineOrder().size() - 2 && secondIndex == generatedSolution.getSpineOrder().size() - 1) || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
    }
}
