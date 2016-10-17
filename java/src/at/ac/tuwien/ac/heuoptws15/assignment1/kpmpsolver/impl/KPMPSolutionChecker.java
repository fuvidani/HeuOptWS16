package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolutionChecker {
    public int getCrossingNumber(List<Integer> spineOrder, List<KPMPSolutionWriter.PageEntry> edgePartitioning, int K) {
        int result = 0;

        for (int i = 0; i < K; i++) {
            List<KPMPSolutionWriter.PageEntry> edges = new ArrayList<>();

            for (KPMPSolutionWriter.PageEntry pe: edgePartitioning) {
                if (pe.page == i) {
                    edges.add(pe);
                }
            }

            for (Integer index: spineOrder) {
                for (KPMPSolutionWriter.PageEntry edge1: edges) {
                    if (edge1.a == index) {
                        for (KPMPSolutionWriter.PageEntry edge2: edges) {
                            int indexOfEdge1A = spineOrder.indexOf(edge1.a);
                            int indexOfEdge1B = spineOrder.indexOf(edge1.b);
                            int indexOfEdge2A = spineOrder.indexOf(edge2.a);
                            int indexOfEdge2B = spineOrder.indexOf(edge2.b);
                            if (indexOfEdge2A > indexOfEdge1A) {
                                //if (edge1.a < edge2.a && edge1.a < edge1.b && edge1.a < edge2.b && edge2.a < edge1.b && edge2.a < edge2.b && edge1.b < edge2.b){
                                if (indexOfEdge1A < indexOfEdge2A && indexOfEdge1A < indexOfEdge1B && indexOfEdge1A < indexOfEdge2B && indexOfEdge2A < indexOfEdge1B && indexOfEdge2A < indexOfEdge2B && indexOfEdge1B < indexOfEdge2B){
                                    result++;
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
