package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.AbstractKPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.AbstractKPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by David on 19.10.2016.
 */
public class KPMPEdgePartitionRandomHeuristic extends AbstractKPMPEdgePartitionHeuristic {

    protected List<KPMPSolutionWriter.PageEntry> moveEdges() {
        Random random;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();

        edgeConflictMap = sortByValue(edgeConflictMap);
        List<KPMPSolutionWriter.PageEntry> edgeList = edgeConflictMap.keySet().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        List<KPMPSolutionWriter.PageEntry> bestSolution = edgeList.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        List<KPMPSolutionWriter.PageEntry> originalList = edgeList.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        int bestNumberOfCrossings = currentNumberOfCrossings;
        boolean makeAnotherRun = true;
        boolean rearrangeSpineOrder = false;
        long timeOfLastImprovement = System.nanoTime();
        long start = System.nanoTime();
        while (makeAnotherRun) {
            // shuffle spine order if there hasn't been any improvement for 90 seconds
            if (rearrangeSpineOrder){
                rearrangeSpineOrder = false;
                AbstractKPMPSpineOrderHeuristic spineOrderHeuristic = new KPMPSpineOrderRandomDFSHeuristic();
                List<Integer> newSpineOrder = spineOrderHeuristic.calculateSpineOrder(instance,bestSolution,bestNumberOfCrossings, true);
                int newSpineOrderCrossings = spineOrderHeuristic.getNumberOfCrossingsForNewSpineOrder();
                if (newSpineOrderCrossings < bestNumberOfCrossings){
                    bestNumberOfCrossings = newSpineOrderCrossings;
                    spineOrder = newSpineOrder.stream().collect(toCollection(ArrayList::new));
                }
                timeOfLastImprovement = System.nanoTime();
            }
            // assign edges randomly
            random = new Random(System.currentTimeMillis());    // re-seed generator before each run
            for (KPMPSolutionWriter.PageEntry edge : edgeList) {
                if (edgeConflictMap.get(edge) > 0) {
                    int index = random.nextInt(instance.getK());
                    edge.page = index;
                }
            }
            // check solution
            int crossings = solutionChecker.getCrossingNumber(new KPMPSolution(spineOrder,edgeList,instance.getK()));
            if (crossings < bestNumberOfCrossings){
                bestSolution = edgeList.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
                bestNumberOfCrossings = crossings;
                timeOfLastImprovement = System.nanoTime();
            }
            long elapsedSeconds = ((System.nanoTime()-start)/1000000000);
            if (elapsedSeconds > 720){
                makeAnotherRun = false;
            }else if(((System.nanoTime()-timeOfLastImprovement)/1000000000) > 90){
                rearrangeSpineOrder = true;
            }
            edgeList = originalList.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        }
        return bestSolution;
    }
}
