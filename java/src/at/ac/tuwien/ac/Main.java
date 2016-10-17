package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 11.10.16
 */
public class Main {

    public static void main(String[] args) {
        try {
            KPMPInstance instance = KPMPInstance.readInstance("C:\\Development\\workspaces\\TU\\HOT\\assignment1\\instances\\automatic-8.txt");
            System.out.println("K: " + instance.getK() + "\nVertices: " + instance.getNumVertices());

            /*List<List<Integer>> adjacencyList1 = instance.getAdjacencyList();
            for (List<Integer> list : adjacencyList1) {
                for (int i : list) {
                    System.out.print(i + "\t");
                }
                System.out.println();
            }*/

            List<Integer> spineOrder = new ArrayList<>();
            for (int i = 0; i < instance.getNumVertices(); i++) {
                spineOrder.add(i);
            }

            List<List<Integer>> adjacencyList = instance.getAdjacencyList();
            List<KPMPSolutionWriter.PageEntry> edgePart = new ArrayList<>();
            for (int row = 0; row < adjacencyList.size(); row++) {
                for (int j = 0; j < adjacencyList.get(row).size(); j++) {
                    if (adjacencyList.get(row).get(j) > row) {
                        edgePart.add(new KPMPSolutionWriter.PageEntry(row, adjacencyList.get(row).get(j), 0));
                    }
                }
            }

            //edgePart.get(8).page = 1;


            System.out.println("crossing number: " + new KPMPSolutionChecker().getCrossingNumber(spineOrder, edgePart, instance.getK()));

            KPMPSpineOrderDFSHeuristic heuristic = new KPMPSpineOrderDFSHeuristic(instance);

            List<Integer> newSpineOrder = heuristic.calculateSpineOrder();
            for (Integer i: newSpineOrder) {
                System.out.print(i + " ");
            }

            KPMPEdgePartitionCFLHeuristic edgePartitionCFLHeuristic = new KPMPEdgePartitionCFLHeuristic();

            edgePartitionCFLHeuristic.calculateEdgePartition(instance,newSpineOrder);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
