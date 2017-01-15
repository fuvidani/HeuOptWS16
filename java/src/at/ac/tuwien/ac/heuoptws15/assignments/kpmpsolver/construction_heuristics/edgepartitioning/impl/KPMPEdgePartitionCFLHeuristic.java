package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.AbstractKPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPEdgePartitionCFLHeuristic extends AbstractKPMPEdgePartitionHeuristic {


    protected List<KPMPSolutionWriter.PageEntry> moveEdges() {
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        List<KPMPSolutionWriter.PageEntry> edgeList = edgeConflictMap.keySet().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(toCollection(ArrayList::new));

        HashMap<Integer, Integer> pageCrossingsMap = new HashMap<>();
        for (int i = 0; i < instance.getK(); i++) {
            pageCrossingsMap.put(i, solutionChecker.getCrossingNumberOfPage(spineOrder, edgeList, i));
        }

        HashMap<KPMPSolutionWriter.PageEntry, Integer> sortedEdgeConflictMap = edgeConflictMap;
        sortedEdgeConflictMap = removeZeroes(sortedEdgeConflictMap);
        sortedEdgeConflictMap = sortByValue(sortedEdgeConflictMap);

        int counter = 0;
        Iterator<KPMPSolutionWriter.PageEntry> iterator = sortedEdgeConflictMap.keySet().iterator();
        while (counter < sortedEdgeConflictMap.size() * Main.constructionMultiplier && ((System.nanoTime() - Main.START) / 1000000000) < Main.secondsBeforeStop) {
            if (counter > 0 && counter % sortedEdgeConflictMap.keySet().size() == 0) {
                iterator = sortedEdgeConflictMap.keySet().iterator();
            }
            KPMPSolutionWriter.PageEntry currentEdge = iterator.next();
            List<KPMPSolutionWriter.PageEntry> newList1 = new ArrayList<>();
            newList1.addAll(edgeConflictMap.keySet());
            int maxValue = solutionChecker.getCrossingNumberOfEdge(spineOrder, newList1, 0, currentEdge);
            int bestKIndex = 0;
            int bestKCrossingNumber = maxValue;
            for (int pageIndex = 0; pageIndex < instance.getK(); pageIndex++) {
                if (currentEdge.page != pageIndex) {
                    edgeConflictMap.remove(currentEdge);
                    currentEdge.page = pageIndex;
                    edgeConflictMap.put(currentEdge, maxValue);

                    List<KPMPSolutionWriter.PageEntry> newList = new ArrayList<>();
                    newList.addAll(edgeConflictMap.keySet());

                    int newNumberOfCrossings = solutionChecker.getCrossingNumberOfEdge(spineOrder, newList, pageIndex, currentEdge);
                    if (newNumberOfCrossings < bestKCrossingNumber) {
                        bestKIndex = pageIndex;
                        bestKCrossingNumber = newNumberOfCrossings;
                    }
                    edgeConflictMap.remove(currentEdge);
                    currentEdge.page = 0;
                    edgeConflictMap.put(currentEdge, maxValue);
                }
            }
            if (bestKIndex != 0) {
                pageCrossingsMap.put(bestKIndex, pageCrossingsMap.get(bestKIndex) + bestKCrossingNumber);
                pageCrossingsMap.put(0, pageCrossingsMap.get(0) - maxValue);

                edgeConflictMap.remove(currentEdge);
                currentEdge.page = bestKIndex;
                edgeConflictMap.put(currentEdge, bestKCrossingNumber);
            } else {
                pageCrossingsMap.put(bestKIndex, pageCrossingsMap.get(bestKIndex) + bestKCrossingNumber);
            }
            counter++;
        }
        return new ArrayList<>(edgeConflictMap.keySet());
    }
}
