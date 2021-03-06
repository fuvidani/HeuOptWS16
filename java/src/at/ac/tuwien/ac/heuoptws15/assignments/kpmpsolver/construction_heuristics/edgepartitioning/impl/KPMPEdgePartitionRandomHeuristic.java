package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.AbstractKPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
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
        HashMap<KPMPSolutionWriter.PageEntry, Integer> sortedEdgeConflictMap = edgeConflictMap;
        List<KPMPSolutionWriter.PageEntry> edgeList = edgeConflictMap.keySet().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));
        long timeOfLastImprovement = 0;
        int iterations = 0;

        while (iterations < sortedEdgeConflictMap.size()) {
            // re-seed generator before each run
            random = new Random(Double.doubleToLongBits(Math.random()));
            KPMPSolutionWriter.PageEntry edge;
            int index = random.nextInt(edgeList.size());
            edge = edgeList.get(index);
            List<KPMPSolutionWriter.PageEntry> newList1 = new ArrayList<>();
            newList1.addAll(edgeConflictMap.keySet());
            int maxValue = solutionChecker.getCrossingNumberOfEdge(spineOrder, newList1, 0, edge);
            int bestKIndex = 0;
            int bestKCrossingNumber = maxValue;
            for (int pageIndex = 0; pageIndex < instance.getK(); pageIndex++) {
                if (edge.page != pageIndex) {
                    edgeConflictMap.remove(edge);
                    edge.page = pageIndex;
                    edgeConflictMap.put(edge, maxValue);

                    List<KPMPSolutionWriter.PageEntry> newList = new ArrayList<>();
                    newList.addAll(edgeConflictMap.keySet());

                    int newNumberOfCrossings = solutionChecker.getCrossingNumberOfEdge(spineOrder, newList, pageIndex, edge);
                    if (newNumberOfCrossings < bestKCrossingNumber) {
                        bestKIndex = pageIndex;
                        bestKCrossingNumber = newNumberOfCrossings;
                        timeOfLastImprovement = 0;
                    }
                    edgeConflictMap.remove(edge);
                    edge.page = 0;
                    edgeConflictMap.put(edge, maxValue);
                }
            }
            if (bestKIndex != 0) {
                edgeConflictMap.remove(edge);
                edge.page = bestKIndex;
                edgeConflictMap.put(edge, bestKCrossingNumber);
            }
            iterations++;
        }
        return new ArrayList<>(edgeConflictMap.keySet());
    }
}
