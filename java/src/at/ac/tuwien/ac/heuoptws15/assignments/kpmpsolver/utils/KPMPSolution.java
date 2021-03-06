package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by David on 16.10.2016.
 */
public class KPMPSolution implements Cloneable {
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

    public KPMPSolution(KPMPSolution solution) {
        this.spineOrder = solution.getSpineOrder().stream().collect(Collectors.toCollection(ArrayList::new));
        this.edgePartition = solution.getEdgePartition().stream().map(KPMPSolutionWriter.PageEntry::clone).collect(Collectors.toCollection(ArrayList::new));
        this.numberOfPages = solution.getNumberOfPages();
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

    public KPMPSolution clone() {
        return new KPMPSolution(spineOrder.stream().collect(Collectors.toList()), edgePartition.stream().map(KPMPSolutionWriter.PageEntry::clone).collect(Collectors.toList()), numberOfPages);
    }
}
