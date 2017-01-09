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
 * @since 30.10.16
 */
public class SingleEdgeMove extends AbstractKPMPLocalSearch {

    private int index;
    private int originalPageIndex;
    private int newPageIndex;
    private KPMPSolutionWriter.PageEntry edge;
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
        index = 0;
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
        edge = edgePartition.get(index);
        originalPageIndex = edge.page;
        newPageIndex = pageCounter;
        edgePartition.get(index).page = newPageIndex;
        index++;
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
        int randomIndex = random.nextInt(edgePartition.size());
        edge = edgePartition.get(randomIndex);
        originalPageIndex = edge.page;
        do {
            newPageIndex = random.nextInt(neighbourSolution.getNumberOfPages());
        } while (newPageIndex == originalPageIndex);
        edgePartition.get(randomIndex).page = newPageIndex;
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
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, RandomStepFunction stepFunction) {
        numberOfIterations++;
        numberOfIterationsWithoutImprovement++;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int crossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalPageIndex, edge);
        int crossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newPageIndex, edge);
        if (crossingsOnNewPage < crossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            numberOfIterationsWithoutImprovement = 0;

            crossingNumber = crossingNumber - (crossingsOnOriginalPage - crossingsOnNewPage);
        }
        return numberOfIterations >= bestSolution.getEdgePartition().size() * bestSolution.getNumberOfPages() || numberOfIterationsWithoutImprovement >= Main.iterationMultiplier || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || numberOfIterations >= Main.localSearchIterationLimit;
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
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, FirstImprovementStepFunction stepFunction) {
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int crossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalPageIndex, edge);
        int crossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newPageIndex, edge);
        if (crossingsOnNewPage < crossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            return true;
        }
        if (index == bestSolution.getEdgePartition().size() && pageCounter != bestSolution.getNumberOfPages() - 1) {
            index = 0;
            pageCounter++;
        }
        return ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || index == bestSolution.getEdgePartition().size() && pageCounter == bestSolution.getNumberOfPages() - 1;
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
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, BestImprovementStepFunction stepFunction) {
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int crossingsOnOriginalPage = solutionChecker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalPageIndex, edge);
        int crossingsOnNewPage = solutionChecker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newPageIndex, edge);
        if (crossingsOnNewPage < crossingsOnOriginalPage) {
            bestSolution = generatedSolution;
            index = 0;
            pageCounter = 0;

            crossingNumber = crossingNumber - (crossingsOnOriginalPage - crossingsOnNewPage);

            //assert(crossingNumber == new KPMPSolutionChecker().getCrossingNumber(bestSolution));
        }
        if (index == bestSolution.getEdgePartition().size() && pageCounter != bestSolution.getNumberOfPages() - 1) {
            index = 0;
            pageCounter++;
        }
        return ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000) || index == bestSolution.getEdgePartition().size() && pageCounter == bestSolution.getNumberOfPages() - 1 || numberOfIterations >= Main.localSearchIterationLimit;
    }

    @Override
    public String getAbbreviation() {
        return "S-E-M";
    }
}
