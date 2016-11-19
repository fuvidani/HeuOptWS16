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
 * @since 15.11.16
 */
public class DoubleNodeSwap extends AbstractKPMPLocalSearch {

    private int firstIndex;
    private int secondIndex;
    private int localBestCrossingNumber;
    private int numberOfIterations;
    private List<List<Integer>> permutations;
    private int A_index1;
    private int A_index2;
    private int B_index1;
    private int B_index2;

    /**
     * Just for experiments
     */
    private void permuteSolutions() {
        firstIndex = 0;
        secondIndex = 1;
        permutations = new ArrayList<>();
        List<Integer> originalSpineOrder = bestSolution.getSpineOrder();
        int numberOfVertices = bestSolution.getSpineOrder().size();
        int maxNumberOfPermutations = (numberOfVertices * (numberOfVertices - 1)) / 2;
        for (int i = 0; i < maxNumberOfPermutations; i++) {
            ArrayList<Integer> newOrder = originalSpineOrder.stream().collect(toCollection(ArrayList::new));
            if (secondIndex >= numberOfVertices) {
                firstIndex++;
                secondIndex = firstIndex + 1;
            }
            Collections.swap(newOrder, firstIndex, secondIndex);
            secondIndex++;
            permutations.add(newOrder);
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
    @Override
    protected void beforeSearch() {
        localBestCrossingNumber = bestCrossingNumber;
        numberOfIterations = 0;
        firstIndex = 0;
        secondIndex = 0;
        A_index1 = 0;
        A_index2 = 1;
        B_index1 = 1;
        B_index2 = 2;
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
        if (A_index1 < currentSpineOrder.size() && B_index2 < currentSpineOrder.size()) {
            Collections.swap(currentSpineOrder, A_index1, A_index2);
            Collections.swap(currentSpineOrder, B_index1, B_index2);
        } else if (B_index2 >= currentSpineOrder.size() && B_index1 < currentSpineOrder.size()) {
            B_index1++;
            B_index2 = B_index1 + 1;
            if (B_index2 >= currentSpineOrder.size() && B_index1 >= currentSpineOrder.size() - 1) {
                A_index2++;
                B_index1 = A_index1 + 1;
                B_index2 = B_index1 + 1;
            }
            Collections.swap(currentSpineOrder, A_index1, A_index2);
            Collections.swap(currentSpineOrder, B_index1, B_index2);
        }
        if (B_index2 >= currentSpineOrder.size() && B_index1 >= currentSpineOrder.size() - 1) {
            A_index2++;
            B_index1 = A_index1 + 1;
            B_index2 = B_index1 + 1;
            Collections.swap(currentSpineOrder, A_index1, A_index2);
            Collections.swap(currentSpineOrder, B_index1, B_index2);
        }
        B_index2++;
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
        List<Integer> currentSpineOrder = bestSolution.getSpineOrder().stream().collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(currentSpineOrder, bestSolution.getEdgePartition(), bestSolution.getNumberOfPages());
        A_index1 = random.nextInt(currentSpineOrder.size());
        do {
            A_index2 = random.nextInt(currentSpineOrder.size());
        } while (A_index1 == A_index2);

        do {
            B_index1 = random.nextInt(currentSpineOrder.size());
        } while (A_index1 == B_index1 || A_index2 == B_index1);

        do {
            B_index2 = random.nextInt(currentSpineOrder.size());
        } while (A_index1 == B_index2 || A_index2 == B_index2 || B_index1 == B_index2);

        Collections.swap(currentSpineOrder, A_index1, A_index2);
        Collections.swap(currentSpineOrder, B_index1, B_index2);
        numberOfIterations++;
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
        numberOfIterationsWithoutImprovement++;
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            numberOfIterationsWithoutImprovement = 0;
        }
        return numberOfIterationsWithoutImprovement >=  Main.iterationMultiplier || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            A_index1 = 0;
            A_index2 = 1;
            B_index1 = 1;
            B_index2 = 2;
            return true;
        }
        return A_index2 >= generatedSolution.getSpineOrder().size() - 1 && B_index2 >= generatedSolution.getSpineOrder().size() || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
        int crossingNumber = new KPMPSolutionChecker().getCrossingNumber(generatedSolution);
        if (crossingNumber < localBestCrossingNumber) {
            localBestCrossingNumber = crossingNumber;
            bestSolution = generatedSolution;
            A_index1 = 0;
            A_index2 = 1;
            B_index1 = 1;
            B_index2 = 2;
            numberOfIterations = 0;
        }
        return A_index2 >= generatedSolution.getSpineOrder().size() - 1 && B_index2 >= generatedSolution.getSpineOrder().size() || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
    }

    /**
     * Returns the getAbbreviation of the neighbourhood structure that
     * is used to mark solution files with the structure's name.
     *
     * @return getAbbreviation of the local search strategy
     */
    @Override
    public String getAbbreviation() {
        return "D-N-S";
    }
}
