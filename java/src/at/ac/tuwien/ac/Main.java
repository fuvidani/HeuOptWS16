package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.impl.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

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
            KPMPInstance instance = KPMPInstance.readInstance("C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\instances\\automatic-6.txt");
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

            System.out.println("crossing number before: " + new KPMPSolutionChecker().getCrossingNumber(new KPMPSolution(spineOrder, edgePart, instance.getK())));

            long start = System.nanoTime();
            KPMPSolution solution = new KPMPSolver(instance).solve();

            System.out.println("new number of crossings: " + new KPMPSolutionChecker().getCrossingNumber(solution));
            System.out.println((System.nanoTime() - start) / 1000000000 + " seconds runtime");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
