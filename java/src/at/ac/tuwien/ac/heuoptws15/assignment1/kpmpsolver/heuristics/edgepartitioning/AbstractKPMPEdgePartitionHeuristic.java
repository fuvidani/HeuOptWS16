package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 19.10.16
 */
public abstract class AbstractKPMPEdgePartitionHeuristic implements KPMPEdgePartitionHeuristic {

    protected HashMap<KPMPSolutionWriter.PageEntry, Integer> edgeConflictMap = new HashMap<>();
    protected KPMPInstance instance;
    protected List<Integer> spineOrder;
    protected int currentNumberOfCrossings;

    @Override
    public List<KPMPSolutionWriter.PageEntry> calculateEdgePartition(KPMPInstance instance, List<Integer> spineOrder,int currentNumberOfCrossings) {
        this.instance = instance;
        this.spineOrder = spineOrder;
        this.currentNumberOfCrossings = currentNumberOfCrossings;
        this.calculateConflicts();
        return moveEdges();
    }

    @Override
    public List<Integer> getSpineOrder(){
        return spineOrder;
    }

    /**
     * Picks an edge and adds it to another
     * page according to the implementation's
     * strategy.
     */
    protected abstract List<KPMPSolutionWriter.PageEntry> moveEdges();

    /**
     * Creates a mapping between an edge and
     * the number of crossings it causes.
     */
    protected void calculateConflicts() {
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

    /**
     * Returns a new Hash map where the entries
     * are sorted by their values. This allows
     * us to prioritize edges that cause the
     * most crossings in the graph.
     *
     * @param map an original mapping between edges and the
     *            number of the crossings they cause
     * @return new map sorted by values
     */
    protected HashMap<KPMPSolutionWriter.PageEntry, Integer> sortByValue(HashMap<KPMPSolutionWriter.PageEntry, Integer> map) {
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

    /**
     * Returns a new Hash map with the same entries
     * as the given one with the exception of those
     * with 0 as a value.
     *
     * @param map an original mapping between edges and the
     *            number of the crossings they cause
     * @return new map without entries containing 0 as value
     */
    protected HashMap<KPMPSolutionWriter.PageEntry, Integer> removeZeroes(HashMap<KPMPSolutionWriter.PageEntry, Integer> map){
        HashMap<KPMPSolutionWriter.PageEntry, Integer> result = new HashMap<>();
        for (KPMPSolutionWriter.PageEntry pageEntry: map.keySet()){
            if (map.get(pageEntry) > 0){
                result.put(pageEntry, map.get(pageEntry));
            }
        }
        return result;
    }

}
