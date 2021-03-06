package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolutionChecker {
    public int getCrossingNumber(KPMPSolution solution) {
        int result = 0;

        HashMap<Integer, Integer> spineOrderMap = new HashMap<>();

        for (int i = 0; i < solution.getSpineOrder().size(); i++) {
            spineOrderMap.put(solution.getSpineOrder().get(i), i);
        }

        for (int i = 0; i < solution.getNumberOfPages(); i++) {
            List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();

            for (KPMPSolutionWriter.PageEntry pe : solution.getEdgePartition()) {
                if (pe.page == i) {
                    edges.add(pe);
                }
            }

            for (KPMPSolutionWriter.PageEntry edge1 : edges) {
                for (KPMPSolutionWriter.PageEntry edge2 : edges) {
                    int indexOfEdge1A = spineOrderMap.get(edge1.a);
                    int indexOfEdge1B = spineOrderMap.get(edge1.b);
                    int indexOfEdge2A = spineOrderMap.get(edge2.a);
                    int indexOfEdge2B = spineOrderMap.get(edge2.b);
                    if (indexOfEdge1A > indexOfEdge1B) {
                        int temp = indexOfEdge1A;
                        indexOfEdge1A = indexOfEdge1B;
                        indexOfEdge1B = temp;
                    }
                    if (indexOfEdge2A > indexOfEdge2B) {
                        int temp = indexOfEdge2A;
                        indexOfEdge2A = indexOfEdge2B;
                        indexOfEdge2B = temp;
                    }
                    if (indexOfEdge2A > indexOfEdge1A) {
                        //if (edge1.a < edge2.a && edge1.a < edge1.b && edge1.a < edge2.b && edge2.a < edge1.b && edge2.a < edge2.b && edge1.b < edge2.b){
                        if (indexOfEdge1A < indexOfEdge2A && indexOfEdge1A < indexOfEdge1B && indexOfEdge1A < indexOfEdge2B && indexOfEdge2A < indexOfEdge1B && indexOfEdge2A < indexOfEdge2B && indexOfEdge1B < indexOfEdge2B) {
                            result++;
                        }
                    }
                }

            }

        }

        return result;
    }

    public int getCrossingNumberOfPage(List<Integer> spineOrder, List<KPMPSolutionWriter.PageEntry> edgePartitioning, int indexOfPage) {
        int result = 0;

        HashMap<Integer, Integer> spineOrderMap = new HashMap<>();

        for (int i = 0; i < spineOrder.size(); i++) {
            spineOrderMap.put(spineOrder.get(i), i);
        }

        List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();

        for (KPMPSolutionWriter.PageEntry pe : edgePartitioning) {
            if (pe.page == indexOfPage) {
                edges.add(pe);
            }
        }

        for (KPMPSolutionWriter.PageEntry edge1 : edges) {
            for (KPMPSolutionWriter.PageEntry edge2 : edges) {
                int indexOfEdge1A = spineOrderMap.get(edge1.a);
                int indexOfEdge1B = spineOrderMap.get(edge1.b);
                int indexOfEdge2A = spineOrderMap.get(edge2.a);
                int indexOfEdge2B = spineOrderMap.get(edge2.b);
                if (indexOfEdge1A > indexOfEdge1B) {
                    int temp = indexOfEdge1A;
                    indexOfEdge1A = indexOfEdge1B;
                    indexOfEdge1B = temp;
                }
                if (indexOfEdge2A > indexOfEdge2B) {
                    int temp = indexOfEdge2A;
                    indexOfEdge2A = indexOfEdge2B;
                    indexOfEdge2B = temp;
                }
                if (indexOfEdge2A > indexOfEdge1A) {
                    //if (edge1.a < edge2.a && edge1.a < edge1.b && edge1.a < edge2.b && edge2.a < edge1.b && edge2.a < edge2.b && edge1.b < edge2.b){
                    if (indexOfEdge1A < indexOfEdge2A && indexOfEdge1A < indexOfEdge1B && indexOfEdge1A < indexOfEdge2B && indexOfEdge2A < indexOfEdge1B && indexOfEdge2A < indexOfEdge2B && indexOfEdge1B < indexOfEdge2B) {
                        result++;
                    }
                }
            }

        }

        return result;
    }

    public int getCrossingNumberOfEdge(List<Integer> spineOrder, List<KPMPSolutionWriter.PageEntry> edgePartitioning, int indexOfPage, KPMPSolutionWriter.PageEntry edge) {
        int result = 0;

        HashMap<Integer, Integer> spineOrderMap = new HashMap<>();

        for (int i = 0; i < spineOrder.size(); i++) {
            spineOrderMap.put(spineOrder.get(i), i);
        }

        List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();

        for (KPMPSolutionWriter.PageEntry pe : edgePartitioning) {
            if (pe.page == indexOfPage) {
                edges.add(pe);
            }
        }

        int indexOfEdgeA = spineOrderMap.get(edge.a);
        int indexOfEdgeB = spineOrderMap.get(edge.b);
        if (indexOfEdgeA > indexOfEdgeB) {
            int temp = indexOfEdgeA;
            indexOfEdgeA = indexOfEdgeB;
            indexOfEdgeB = temp;
        }

        for (KPMPSolutionWriter.PageEntry edge2 : edges) {
            int indexOfEdge1A = indexOfEdgeA;
            int indexOfEdge1B = indexOfEdgeB;
            int indexOfEdge2A = spineOrderMap.get(edge2.a);
            int indexOfEdge2B = spineOrderMap.get(edge2.b);
            if (indexOfEdge2A > indexOfEdge2B) {
                int temp = indexOfEdge2A;
                indexOfEdge2A = indexOfEdge2B;
                indexOfEdge2B = temp;
            }
            if (indexOfEdge2B <= indexOfEdge1A || indexOfEdge2A >= indexOfEdge1B) {
                // skip edge
            } else {
                if ((indexOfEdge2A < indexOfEdge1A && indexOfEdge2B > indexOfEdge1A && indexOfEdge2B < indexOfEdge1B) || (indexOfEdge2A > indexOfEdge1A && indexOfEdge2A < indexOfEdge1B && indexOfEdge2B > indexOfEdge1B)) {
                    result++;
                }
            }
        }

        return result;
    }
}
