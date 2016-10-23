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

    public static final int secondsBeforeStop = 720;   // 720 ~ 12 minutes
    public static long START;
    public static int iterationMultiplier;
    private static final HeuristicStrategy heuristicStrategy = HeuristicStrategy.DETERMINISTIC;
    //private static String inputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/instances/";
    //private static String outputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/solutions/";
    private static String inputPath = "E:\\HeuOptWS16\\instances\\";
    private static String outputPath = "E:\\HeuOptWS16\\solutions\\";
    //private static String inputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\instances\\";
    //private static String outputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\solutions\\";
    private static int testRuns = 0;

    public static void main(String[] args) {
        try {
            while (testRuns < 10) {
                int instanceCounter = 1;
                while (instanceCounter != 2) {
                    if (instanceCounter < 6) {
                        iterationMultiplier = 100;
                    } else if (instanceCounter == 6) {
                        iterationMultiplier = 2;
                    } else {
                        iterationMultiplier = 10;
                    }
                    KPMPInstance instance = KPMPInstance.readInstance(inputPath + "automatic-" + instanceCounter + ".txt");
                    System.out.println("Test Instance " + instanceCounter + " - K: " + instance.getK() + ", Vertices: " + instance.getNumVertices());

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

                    //int originalNumberOfCrossings = new KPMPSolutionChecker().getCrossingNumber(new KPMPSolution(spineOrder, edgePart, instance.getK()));
                    //System.out.println("Number of crossings before: " + NumberFormat.getIntegerInstance().format(originalNumberOfCrossings));

                    KPMPSolver kpmpSolver = new KPMPSolver(instance, edgePart, 0);
                    switch (heuristicStrategy) {
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
                    START = System.nanoTime();
                    KPMPSolution solution = kpmpSolver.solve();
                    long millis = ((System.nanoTime() - START) / 1000000);
                    System.out.println("Runtime: " + millis + " milliseconds");
                    int numberOfCrossings = new KPMPSolutionChecker().getCrossingNumber(solution);
                    System.out.println("Number of crossings after: " + NumberFormat.getIntegerInstance().format(numberOfCrossings) + "\n\n\n");


                    KPMPSolutionWriter writer = new KPMPSolutionWriter(instance.getK());
                    writer.setSpineOrder(solution.getSpineOrder());
                    for (KPMPSolutionWriter.PageEntry pageEntry : solution.getEdgePartition()) {
                        writer.addEdgeOnPage(pageEntry.a, pageEntry.b, pageEntry.page);
                    }
                    writer.write(outputPath + heuristicStrategy.getFolderPath() + "automatic-" + instanceCounter + "_" + numberOfCrossings + "_" + millis + ".txt");
                    instanceCounter++;
                }
                testRuns++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
