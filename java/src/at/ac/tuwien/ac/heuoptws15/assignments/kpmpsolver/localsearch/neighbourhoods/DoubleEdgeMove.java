package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.FirstImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 17.11.16
 */
public class DoubleEdgeMove extends AbstractKPMPLocalSearch {

    private int firstIndex;
    private int secondIndex;
    private int firstEdgeOriginalPageIndex;
    private int secondEdgeOriginalPageIndex;
    private int firstEdgeNewPageIndex;
    private int secondEdgeNewPageIndex;
    private KPMPSolutionWriter.PageEntry firstEdge;
    private KPMPSolutionWriter.PageEntry secondEdge;
    private int numberOfIterations;
    private int pageCounter;

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
        firstIndex = 0;
        secondIndex = 1;
        numberOfIterations = 0;
        pageCounter = 0;
    }

    /**
     * Returns the next solution in the neighbourhood by
     * applying a "move" operation.
     *
     * @return next solution in the neighbourhood
     */
    @Override
    protected KPMPSolution nextNeighbour() {
        List<KPMPSolutionWriter.PageEntry> edgePartition = bestSolution.getEdgePartition().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(bestSolution.getSpineOrder(), edgePartition, bestSolution.getNumberOfPages());
        firstEdge = edgePartition.get(firstIndex);
        secondEdge = edgePartition.get(secondIndex);
        firstEdgeOriginalPageIndex = firstEdge.page;
        secondEdgeOriginalPageIndex = secondEdge.page;
        firstEdgeNewPageIndex = pageCounter;
        secondEdgeNewPageIndex = pageCounter;
        edgePartition.get(firstIndex).page = firstEdgeNewPageIndex;
        edgePartition.get(secondIndex).page = secondEdgeNewPageIndex;
        firstIndex++;
        secondIndex++;
        numberOfIterations++;
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
        List<KPMPSolutionWriter.PageEntry> edgePartition = bestSolution.getEdgePartition().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(bestSolution.getSpineOrder(), edgePartition, bestSolution.getNumberOfPages());
        int firstRandomIndex = random.nextInt(edgePartition.size());
        int secondRandomIndex;
        do {
            secondRandomIndex = random.nextInt(edgePartition.size());
        } while (firstRandomIndex == secondRandomIndex);
        firstEdge = edgePartition.get(firstRandomIndex);
        secondEdge = edgePartition.get(secondRandomIndex);
        firstEdgeOriginalPageIndex = firstEdge.page;
        secondEdgeOriginalPageIndex = secondEdge.page;
        do {
            firstEdgeNewPageIndex = random.nextInt(neighbourSolution.getNumberOfPages());
        } while (firstEdgeNewPageIndex == firstEdgeOriginalPageIndex);
        do {
            secondEdgeNewPageIndex = random.nextInt(neighbourSolution.getNumberOfPages());
        } while (secondEdgeNewPageIndex == secondEdgeOriginalPageIndex);
        edgePartition.get(firstRandomIndex).page = firstEdgeNewPageIndex;
        edgePartition.get(secondRandomIndex).page = secondEdgeNewPageIndex;
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
        numberOfIterationsWithoutImprovement++;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int firstCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), firstEdgeOriginalPageIndex, firstEdge);
        int secondCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), secondEdgeOriginalPageIndex, secondEdge);
        int firstCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), firstEdgeNewPageIndex, firstEdge);
        int secondCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), secondEdgeNewPageIndex, secondEdge);
        if (firstCrossingsOnNewPage + secondCrossingsOnNewPage < firstCrossingsOnOriginalPage + secondCrossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            numberOfIterationsWithoutImprovement = 0;
        }
        return numberOfIterationsWithoutImprovement >= bestSolution.getEdgePartition().size() || numberOfIterationsWithoutImprovement >=  Main.iterationMultiplier || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int firstCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), firstEdgeOriginalPageIndex, firstEdge);
        int secondCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), secondEdgeOriginalPageIndex, secondEdge);
        int firstCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), firstEdgeNewPageIndex, firstEdge);
        int secondCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), secondEdgeNewPageIndex, secondEdge);

        if (firstCrossingsOnNewPage + secondCrossingsOnNewPage < firstCrossingsOnOriginalPage + secondCrossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            return true;
        }
        if (secondIndex == bestSolution.getEdgePartition().size() && pageCounter != bestSolution.getNumberOfPages() - 1) {
            firstIndex = 0;
            secondIndex = 1;
            pageCounter++;
        }
        return ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || secondIndex == bestSolution.getEdgePartition().size() && pageCounter == bestSolution.getNumberOfPages() - 1;

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
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int firstCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), firstEdgeOriginalPageIndex, firstEdge);
        int secondCrossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), secondEdgeOriginalPageIndex, secondEdge);
        int firstCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), firstEdgeNewPageIndex, firstEdge);
        int secondCrossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), secondEdgeNewPageIndex, secondEdge);

        if (firstCrossingsOnNewPage + secondCrossingsOnNewPage < firstCrossingsOnOriginalPage + secondCrossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            firstIndex = 0;
            secondIndex = 1;
            pageCounter = 0;
        }
        if (secondIndex == bestSolution.getEdgePartition().size() && pageCounter != bestSolution.getNumberOfPages() - 1) {
            firstIndex = 0;
            secondIndex = 1;
            pageCounter++;
        }
        return ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || secondIndex == bestSolution.getEdgePartition().size() && pageCounter == bestSolution.getNumberOfPages() - 1;

    }

    /**
     * Returns the getAbbreviation of the neighbourhood structure that
     * is used to mark solution files with the structure's name.
     *
     * @return getAbbreviation of the local search strategy
     */
    @Override
    public String getAbbreviation() {
        return "D-E-M";
    }
}
