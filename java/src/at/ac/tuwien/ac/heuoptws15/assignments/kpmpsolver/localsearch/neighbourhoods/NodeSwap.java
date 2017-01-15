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
    private int maxNumberOfIterations;
    private KPMPSolution bestInNeighbourhood;

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
        maxNumberOfIterations = (bestSolution.getSpineOrder().size() * (bestSolution.getSpineOrder().size() - 1)) / 2;
        List<Integer> currentSpineOrder = bestSolution.getSpineOrder().stream().collect(toCollection(ArrayList::new));
        bestInNeighbourhood = new KPMPSolution(currentSpineOrder, bestSolution.getEdgePartition(), bestSolution.getNumberOfPages());
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
        if (secondIndex >= currentSpineOrder.size()) {
            firstIndex++;
            secondIndex = firstIndex + 1;
        }
        Collections.swap(currentSpineOrder, firstIndex, secondIndex);
        secondIndex++;
        numberOfIterations++;
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
    public KPMPSolution randomNextNeighbour() {
        random = new Random(Double.doubleToLongBits(Math.random()));
        /* 1. Copy spine order of best solution
         * 2. Instantiate neighbour solution
         * 3. Pick 2 different random indices
         * 4. Swap the 2 vertices on those random indices
         * 5. Set the new spine order and return neighbour solution */
        List<Integer> currentSpineOrder = bestSolution.getSpineOrder().stream().collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(currentSpineOrder, bestSolution.getEdgePartition(), bestSolution.getNumberOfPages());
        int firstRandom = random.nextInt(currentSpineOrder.size());
        int secondRandom;
        do {
            secondRandom = random.nextInt(currentSpineOrder.size());
        } while (firstRandom == secondRandom);
        Collections.swap(currentSpineOrder, firstRandom, secondRandom);
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
        // TODO incremental evaluation instead of checker
        numberOfIterations++;
        numberOfIterationsWithoutImprovement++;
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            numberOfIterationsWithoutImprovement = 0;
        }
        return numberOfIterations >= Main.localSearchIterationLimit || numberOfIterationsWithoutImprovement >= Main.iterationMultiplier || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            return true;
        }
        return numberOfIterations == maxNumberOfIterations || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
            localBestCrossingNumber = crossingNumber;
            super.crossingNumber = localBestCrossingNumber;
            bestSolution = generatedSolution;
            firstIndex = 0;
            secondIndex = 1;
            numberOfIterations = 0;
        }
        return numberOfIterations == maxNumberOfIterations || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || numberOfIterations >= Main.localSearchIterationLimit;

        /*int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber){
            bestInNeighbourhood = generatedSolution;
            localBestCrossingNumber = crossingNumber;

        }
        if ((firstIndex == generatedSolution.getSpineOrder().size()-2 && secondIndex == generatedSolution.getSpineOrder().size())){
            if (localBestCrossingNumber < bestCrossingNumber) {
                bestSolution = bestInNeighbourhood;
                bestCrossingNumber = localBestCrossingNumber;
                firstIndex = 0;
                secondIndex = 1;
                return false;
            }else {
                return true;
            }
        }
        return false;*/
    }

    @Override
    public String getAbbreviation() {
        return "NS";
    }
}
