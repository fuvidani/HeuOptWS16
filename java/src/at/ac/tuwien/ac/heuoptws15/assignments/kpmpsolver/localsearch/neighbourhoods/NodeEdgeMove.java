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
public class NodeEdgeMove extends AbstractKPMPLocalSearch {

    private int nodeIndex;
    private int pageIndex;
    private int randomPageIndex;
    private int numberOfIterations;
    private ArrayList<KPMPSolutionWriter.PageEntry> originalEdges;
    private ArrayList<KPMPSolutionWriter.PageEntry> newEdges;

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
        nodeIndex = 0;
        numberOfIterations = 0;
        pageIndex = 0;
        random = new Random(Double.doubleToLongBits(Math.random()));
        randomPageIndex = random.nextInt(bestSolution.getNumberOfPages());
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
        List<KPMPSolutionWriter.PageEntry> edges = neighbourSolution.getEdgePartition();
        originalEdges = new ArrayList<>();
        newEdges = new ArrayList<>();

        for (int i = 0; i < edges.size(); i++) {
            if ((edges.get(i).a == nodeIndex || edges.get(i).b == nodeIndex) && edges.get(i).page != pageIndex) {
                originalEdges.add(edges.get(i).clone());
                edges.get(i).page = pageIndex;
                newEdges.add(edges.get(i).clone());
            }
        }
        nodeIndex++;
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
        List<KPMPSolutionWriter.PageEntry> edgePartition = bestSolution.getEdgePartition().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        KPMPSolution neighbourSolution = new KPMPSolution(bestSolution.getSpineOrder(), edgePartition, bestSolution.getNumberOfPages());
        List<KPMPSolutionWriter.PageEntry> edges = neighbourSolution.getEdgePartition();
        originalEdges = new ArrayList<>();
        newEdges = new ArrayList<>();
        int randomNodeIndex = random.nextInt(neighbourSolution.getSpineOrder().size());
        for (int i = 0; i < edges.size(); i++) {
            if ((edges.get(i).a == randomNodeIndex || edges.get(i).b == randomNodeIndex) && edges.get(i).page != randomPageIndex) {
                originalEdges.add(edges.get(i).clone());
                edges.get(i).page = randomPageIndex;
                newEdges.add(edges.get(i).clone());
            }
        }
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
     * @return true if another iterations should be made, false if
     * a satisfying solution has been reached or for any other reason
     * whatsoever (e.g. timeout)
     */
    @Override
    protected boolean stoppingCriteriaSatisfied(KPMPSolution generatedSolution, RandomStepFunction stepFunction) {
        int originalCrossings = 0;
        int newCrossings = 0;
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        for (int i = 0; i < originalEdges.size(); i++) {
            originalCrossings += checker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalEdges.get(i).page, originalEdges.get(i));
            newCrossings += checker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newEdges.get(i).page, newEdges.get(i));
        }
        if (newCrossings < originalCrossings) {
            //System.out.println("Improvement (from " + originalCrossings + " to " + newCrossings + ") - " + numberOfIterations + ". iteration");
            bestSolution = generatedSolution;
        }
        randomPageIndex = random.nextInt(bestSolution.getNumberOfPages());
        return numberOfIterations >= (generatedSolution.getEdgePartition().size() * Main.iterationMultiplier) || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
        int originalCrossings = 0;
        int newCrossings = 0;
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        for (int i = 0; i < originalEdges.size(); i++) {
            originalCrossings += checker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalEdges.get(i).page, originalEdges.get(i));
            newCrossings += checker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newEdges.get(i).page, newEdges.get(i));
        }
        if (newCrossings < originalCrossings) {
           // System.out.println("Improvement (from " + originalCrossings + " to " + newCrossings + ") - " + numberOfIterations + ". iteration");
            bestSolution = generatedSolution;
            nodeIndex = 0;
            pageIndex = 0;
            return true;
        }
        if (nodeIndex == bestSolution.getSpineOrder().size() && pageIndex < bestSolution.getNumberOfPages() - 1) {
            nodeIndex = 0;
            pageIndex++;
        }
        return nodeIndex == bestSolution.getSpineOrder().size() && pageIndex == bestSolution.getNumberOfPages() - 1 || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
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
        int originalCrossings = 0;
        int newCrossings = 0;
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        for (int i = 0; i < originalEdges.size(); i++) {
            originalCrossings += checker.getCrossingNumberOfEdge(bestSolution.getSpineOrder(), bestSolution.getEdgePartition(), originalEdges.get(i).page, originalEdges.get(i));
            newCrossings += checker.getCrossingNumberOfEdge(generatedSolution.getSpineOrder(), generatedSolution.getEdgePartition(), newEdges.get(i).page, newEdges.get(i));
        }
        if (newCrossings < originalCrossings) {
            //System.out.println("Improvement (from " + originalCrossings + " to " + newCrossings + ") - " + numberOfIterations + ". iteration");
            bestSolution = generatedSolution;
            nodeIndex = 0;
            pageIndex = 0;
        }
        if (nodeIndex == bestSolution.getSpineOrder().size() && pageIndex < bestSolution.getNumberOfPages() - 1) {
            nodeIndex = 0;
            pageIndex++;
        }
        return nodeIndex == bestSolution.getSpineOrder().size() && pageIndex == bestSolution.getNumberOfPages() - 1 || ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
    }

    @Override
    public String getAbbreviation() {
        return "N-E-M";
    }
}
