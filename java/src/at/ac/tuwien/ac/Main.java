package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl.KPMPSpineOrderDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignment1.kpmpsolver.utils.*;

import java.io.IOException;
import java.text.NumberFormat;
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

    public static final int secondsBeforeStop = 720;  // 720 ~ 12 minutes

    private static final HeuristicStrategy heuristicStrategy = HeuristicStrategy.DETERMINISTIC;
    private static String inputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/instances/";
    private static String outputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/solutions/";
    //private static String inputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\instances\\";
    //private static String outputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\solutions\\";


    public static void main(String[] args) {
        try {
            int instanceCounter = 7;
            while (instanceCounter != 11) {
                KPMPInstance instance = KPMPInstance.readInstance(inputPath +"automatic-"+instanceCounter+".txt");
                System.out.println("Test Instance "+instanceCounter+ " - K: " + instance.getK() + ", Vertices: " + instance.getNumVertices());

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

                int originalNumberOfCrossings = new KPMPSolutionChecker().getCrossingNumber(new KPMPSolution(spineOrder, edgePart, instance.getK()));
                System.out.println("Number of crossings before: " + NumberFormat.getIntegerInstance().format(originalNumberOfCrossings));

                KPMPSolver kpmpSolver = new KPMPSolver(instance, edgePart, originalNumberOfCrossings);
                switch (heuristicStrategy){
                    case DETERMINISTIC:
                        kpmpSolver.registerSpineOrderHeuristic(new KPMPSpineOrderDFSHeuristic());
                        kpmpSolver.registerEdgePartitionHeuristic(new KPMPEdgePartitionCFLHeuristic());
                        break;
                    case SEMI_RANDOM:
                        kpmpSolver.registerSpineOrderHeuristic(new KPMPSpineOrderRandomDFSHeuristic());
                        kpmpSolver.registerEdgePartitionHeuristic(new KPMPEdgePartitionRandomCFLHeuristic());
                        break;
                    default: // RANDOM
                        kpmpSolver.registerSpineOrderHeuristic(new KPMPSpineOrderRandomDFSHeuristic());
                        kpmpSolver.registerEdgePartitionHeuristic(new KPMPEdgePartitionRandomHeuristic());
                        break;
                }
                kpmpSolver.setHeuristicType(KPMPSolver.HeuristicType.SEPARATED);
                long start = System.nanoTime();
                KPMPSolution solution = kpmpSolver.solve();
                System.out.println("Runtime: " + (System.nanoTime() - start) / 1000000000 + " seconds");
                int numberOfCrossings = new KPMPSolutionChecker().getCrossingNumber(solution);
                System.out.println("Number of crossings after: " + NumberFormat.getIntegerInstance().format(numberOfCrossings) + "\n\n\n");


                KPMPSolutionWriter writer = new KPMPSolutionWriter(instance.getK());
                writer.setSpineOrder(solution.getSpineOrder());
                for (KPMPSolutionWriter.PageEntry pageEntry: solution.getEdgePartition()){
                    writer.addEdgeOnPage(pageEntry.a,pageEntry.b,pageEntry.page);
                }
                writer.write(outputPath+heuristicStrategy.getFolderPath()+"automatic-"+instanceCounter+"_"+numberOfCrossings+".txt");
                instanceCounter++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
