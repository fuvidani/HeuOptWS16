package at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl;

import java.util.List;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolution {
    private List<Integer> spineOrder;
    private List<KPMPSolutionWriter.PageEntry> edgePartition;
    private int numberOfPages;

    public KPMPSolution() {

    }

    public KPMPSolution(List<Integer> spineOrder, List<KPMPSolutionWriter.PageEntry> edgePartition, int numberOfPages) {
        this.spineOrder = spineOrder;
        this.edgePartition = edgePartition;
        this.numberOfPages = numberOfPages;
    }

    public List<Integer> getSpineOrder() {
        return spineOrder;
    }

    public void setSpineOrder(List<Integer> spineOrder) {
        this.spineOrder = spineOrder;
    }

    public List<KPMPSolutionWriter.PageEntry> getEdgePartition() {
        return edgePartition;
    }

    public void setEdgePartition(List<KPMPSolutionWriter.PageEntry> edgePartition) {
        this.edgePartition = edgePartition;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
}
