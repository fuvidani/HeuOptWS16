package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.KPMPEdgePartitionHeuristic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 18.10.16
 */
public class KPMPEdgePartitionRandomCFLHeuristic implements KPMPEdgePartitionHeuristic {


    private HashMap<KPMPSolutionWriter.PageEntry, Integer> edgeConflictMap = new HashMap<>();
    private KPMPInstance instance;
    private List<Integer> spineOrder;
    private Random random;

    @Override
    public List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder) {
        this.instance = instance;
        this.spineOrder = spineOrder;
        this.random = new Random(System.currentTimeMillis());

        this.calculateConflicts();
        this.moveEdges();

        List<KPMPSolutionWriter.PageEntry> edgeList = new ArrayList<>();
        edgeList.addAll(edgeConflictMap.keySet());

        return edgeList;
    }

    private void moveEdges() {
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
    }

    private void calculateConflicts() {
        List<List<Integer>> adjacencyList = instance.getAdjacencyList();
        List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();
        for (int row = 0; row < adjacencyList.size(); row++) {
            for (int j = 0; j < adjacencyList.get(row).size(); j++) {
                if (adjacencyList.get(row).get(j) > row) {
                    edges.add(new KPMPSolutionWriter.PageEntry(row, adjacencyList.get(row).get(j), 0));
                }
            }
        }

        for (KPMPSolutionWriter.PageEntry edge: edges) {
            edgeConflictMap.put(edge,0);
        }

        HashMap<Integer, Integer> spineOrderMap = new HashMap<>();

        for (int i = 0; i <  spineOrder.size(); i++) {
            spineOrderMap.put(spineOrder.get(i), i);
        }

        for (Integer index: spineOrder) {
            for (KPMPSolutionWriter.PageEntry edge1: edges) {

                if (edge1.a == index) {
                    for (KPMPSolutionWriter.PageEntry edge2: edges) {
                        int indexOfEdge1A = spineOrderMap.get(edge1.a);
                        int indexOfEdge1B = spineOrderMap.get(edge1.b);
                        int indexOfEdge2A = spineOrderMap.get(edge2.a);
                        int indexOfEdge2B = spineOrderMap.get(edge2.b);
                        if (indexOfEdge1A > indexOfEdge1B){
                            int temp = indexOfEdge1A;
                            indexOfEdge1A = indexOfEdge1B;
                            indexOfEdge1B = temp;
                        }
                        if (indexOfEdge2A > indexOfEdge2B){
                            int temp = indexOfEdge2A;
                            indexOfEdge2A = indexOfEdge2B;
                            indexOfEdge2B = temp;
                        }
                        if (indexOfEdge2A > indexOfEdge1A) {
                            if (indexOfEdge1A < indexOfEdge2A && indexOfEdge1A < indexOfEdge1B && indexOfEdge1A < indexOfEdge2B && indexOfEdge2A < indexOfEdge1B && indexOfEdge2A < indexOfEdge2B && indexOfEdge1B < indexOfEdge2B) {
                                edgeConflictMap.put(edge2, edgeConflictMap.get(edge2) + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public HashMap<KPMPSolutionWriter.PageEntry, Integer> sortByValue(HashMap<KPMPSolutionWriter.PageEntry, Integer> map) {
        return map.entrySet()
                       .stream()
                       .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                       .collect(Collectors.toMap(
                               Map.Entry::getKey,
                               Map.Entry::getValue,
                               (e1, e2) -> e1,
                               LinkedHashMap::new
                       ));
    }

    private HashMap<KPMPSolutionWriter.PageEntry, Integer> removeZeroes(HashMap<KPMPSolutionWriter.PageEntry, Integer> map){
        HashMap<KPMPSolutionWriter.PageEntry, Integer> result = new HashMap<>();
        for (KPMPSolutionWriter.PageEntry pageEntry: map.keySet()){
            if (map.get(pageEntry) > 0){
                result.put(pageEntry, map.get(pageEntry));
            }
        }
        return result;
    }
}