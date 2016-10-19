package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.AbstractKPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 18.10.16
 */
public class KPMPEdgePartitionRandomCFLHeuristic extends AbstractKPMPEdgePartitionHeuristic {


    protected List<KPMPSolutionWriter.PageEntry> moveEdges() {
        Random random = new Random(System.currentTimeMillis());
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        List<KPMPSolutionWriter.PageEntry> discoveredEdges = new ArrayList<>();

        List<KPMPSolutionWriter.PageEntry> edgeList = new ArrayList<>();
        edgeList.addAll(edgeConflictMap.keySet());

        HashMap<Integer,Integer> pageCrossingsMap = new HashMap<>();
        for (int i = 0; i < instance.getK(); i++) {
            pageCrossingsMap.put(i,solutionChecker.getCrossingNumberOfPage(spineOrder,edgeList,i));
        }
        HashMap<KPMPSolutionWriter.PageEntry, Integer> sortedEdgeConflictMap = edgeConflictMap;
        sortedEdgeConflictMap = removeZeroes(sortedEdgeConflictMap);
        //sortedEdgeConflictMap = sortByValue(sortedEdgeConflictMap);
        List<KPMPSolutionWriter.PageEntry> keyList = new ArrayList<>(sortedEdgeConflictMap.keySet());
        int size = keyList.size();
        int counter = 0;
        long start = System.nanoTime();
        while (counter < size && ((System.nanoTime()-start)/1000000000) < 780){
            int index = random.nextInt(size-counter);
            KPMPSolutionWriter.PageEntry currentEdge = keyList.get(index);
            keyList.remove(index);
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
                    if (newNumberOfCrossings - pageCrossingsMap.get(pageIndex) < maxValue) {
                        pageCrossingsMap.put(pageIndex, newNumberOfCrossings);
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
