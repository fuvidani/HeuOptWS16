package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.AbstractKPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPEdgePartitionCFLHeuristic extends AbstractKPMPEdgePartitionHeuristic{


    protected List<KPMPSolutionWriter.PageEntry> moveEdges() {
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        List<KPMPSolutionWriter.PageEntry> discoveredEdges = new ArrayList<>();
        List<KPMPSolutionWriter.PageEntry> sortDiscoveredEdges = new ArrayList<>();

        List<KPMPSolutionWriter.PageEntry> edgeList = new ArrayList<>();
        edgeList.addAll(edgeConflictMap.keySet());

        HashMap<Integer,Integer> pageCrossingsMap = new HashMap<>();
        for (int i = 0; i < instance.getK(); i++) {
            pageCrossingsMap.put(i,solutionChecker.getCrossingNumberOfPage(spineOrder,edgeList,i));
        }

        HashMap<KPMPSolutionWriter.PageEntry, Integer> sortedEdgeConflictMap = edgeConflictMap;
        sortedEdgeConflictMap = removeZeroes(sortedEdgeConflictMap);
        sortedEdgeConflictMap = sortByValue(sortedEdgeConflictMap);

        int counter = 0;
        Iterator<KPMPSolutionWriter.PageEntry> iterator =sortedEdgeConflictMap.keySet().iterator();
        long start = System.nanoTime();
        while (counter < sortedEdgeConflictMap.size() && ((System.nanoTime()-start)/1000000000) < 780){
            KPMPSolutionWriter.PageEntry currentEdge = iterator.next();
            int maxValue = edgeConflictMap.get(currentEdge);
            discoveredEdges.add(currentEdge);
            for (int pageIndex = 0; pageIndex < instance.getK(); pageIndex++) {
                if (currentEdge.page != pageIndex) {
                    edgeConflictMap.remove(currentEdge);
                    currentEdge.page = pageIndex;
                    edgeConflictMap.put(currentEdge, maxValue);

                    List<KPMPSolutionWriter.PageEntry> newList = new ArrayList<>();
                    newList.addAll(edgeConflictMap.keySet());

                    int newNumberOfCrossings = solutionChecker.getCrossingNumberOfPage(spineOrder, newList, pageIndex);
                    //int newNumberOfCrossings = solutionChecker.getCrossingNumberOfEdge(spineOrder, newList, pageIndex, currentEdge);
                    if (newNumberOfCrossings - pageCrossingsMap.get(pageIndex) < maxValue) {
                    //if (newNumberOfCrossings < maxValue) {
                        pageCrossingsMap.put(pageIndex, newNumberOfCrossings);
                        //pageCrossingsMap.put(pageIndex, pageCrossingsMap.get(pageIndex) + newNumberOfCrossings);
                        pageCrossingsMap.put(0, pageCrossingsMap.get(0) - maxValue);

                        break;
                    } else if (pageIndex == instance.getK() - 1) {
                        edgeConflictMap.remove(currentEdge);
                        currentEdge.page = 0;
                        edgeConflictMap.put(currentEdge, maxValue);
                    }
                }
            }
            counter++;
        }
        return new ArrayList<>(edgeConflictMap.keySet());
    }
}
