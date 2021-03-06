package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.SingleEdgeMove;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.BestImprovementStepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.*;

import java.io.File;
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

    public static final int secondsBeforeStop = 1080;   // 840 ~ 14 minutes, 1500 ~ 25 minutes, 1080 ~ 18 minutes
    public static long START;
    public static int iterationMultiplier;
    public static int localSearchIterationLimit = Integer.MAX_VALUE;
    public static int crossingsBeforeLocalSearch;
    public static final HeuristicStrategy heuristicStrategy = HeuristicStrategy.HYBRID;
    public static int maxCrossingNumber;
    public static double lowerBound = 0;
    public static double GVNS_time_limit_seconds = 30;
    public static double localSearchNodeSwap_iterationLimit = Integer.MAX_VALUE;
    public static double constructionMultiplier = 100;

    //private static String inputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/instances/";
    //private static String outputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/solutions/";
    private static String inputPath = "E:\\HeuOptWS16\\instances\\";
    private static String outputPath = "E:\\HeuOptWS16\\solutions\\";
    //private static String inputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\instances\\";
    //private static String outputPath = "C:\\Development\\workspaces\\TU\\HOT\\assignment1\\HeuOptWS16\\solutions\\";
    private static int testRuns = 0;

    public static void main(String[] args) {
        /*int instanceIndex = 10;
        calculateAvgRuntime(instanceIndex);
        calculateAvgCrossings(instanceIndex);
        calculateAvgCrossingsBeforeLocalSearch(instanceIndex);*/
        try {
            while (testRuns < 1) {
                int instanceCounter = 10;
                while (instanceCounter != 11) {
                    lowerBound = 0;
                    if (instanceCounter == 1) {
                        lowerBound = 9;
                    }
                    if (instanceCounter < 6) {
                        localSearchIterationLimit = 2000;
                        iterationMultiplier = 4000;
                    } else if (instanceCounter == 6) {
                        constructionMultiplier = 2;
                        iterationMultiplier = 500;
                        localSearchIterationLimit = 1000;
                    } else {
                        constructionMultiplier = 8;
                        iterationMultiplier = 1000;
                        localSearchIterationLimit = 3000;
                        localSearchNodeSwap_iterationLimit = 100;
                    }
                    KPMPInstance instance = KPMPInstance.readInstance(inputPath + "automatic-" + instanceCounter + ".txt");
                    System.out.println("Test Instance " + instanceCounter + " - K: " + instance.getK() + ", Vertices: " + instance.getNumVertices());

                    List<List<Integer>> adjacencyList = instance.getAdjacencyList();
                    List<KPMPSolutionWriter.PageEntry> edgePart = new ArrayList<>();
                    for (int row = 0; row < adjacencyList.size(); row++) {
                        for (int j = 0; j < adjacencyList.get(row).size(); j++) {
                            if (adjacencyList.get(row).get(j) > row) {
                                edgePart.add(new KPMPSolutionWriter.PageEntry(row, adjacencyList.get(row).get(j), 0));
                            }
                        }
                    }

                    List<Integer> spineOrder = new ArrayList<>();
                    for (int i = 0; i < instance.getNumVertices(); i++) {
                        spineOrder.add(i);
                    }
                    KPMPSolution initialSolution = new KPMPSolution(spineOrder, edgePart, instance.getK());
                    maxCrossingNumber = new KPMPSolutionChecker().getCrossingNumber(initialSolution);
                    System.out.println("Upper-bound: " + maxCrossingNumber + " crossings; Lower-bound: " + lowerBound + " crossings.");
                    KPMPSolver kpmpSolver = new KPMPSolver(instance, edgePart, 0);
                    if (heuristicStrategy != HeuristicStrategy.GA) {
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
                        StepFunction stepFunction = new BestImprovementStepFunction();
                        KPMPLocalSearch localSearch = new SingleEdgeMove();
                        kpmpSolver.setHeuristicType(KPMPSolver.HeuristicType.SEPARATED);
                        kpmpSolver.registerLocalSearchImplementation(localSearch);
                        kpmpSolver.registerStepFunction(stepFunction);
                    }

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
                    //writer.write(outputPath + heuristicStrategy.getFolderPath() + "automatic-" + instanceCounter + "_" + localSearch.getAbbreviation() + "_" + stepFunction.getAbbreviation() + "_" + crossingsBeforeLocalSearch + "_" + numberOfCrossings + "_" + millis + "ms.txt");
                    writer.write(outputPath + heuristicStrategy.getFolderPath() + "automatic-" + instanceCounter + "_" + numberOfCrossings + "_" + millis + "ms.txt");
                    instanceCounter++;
                }
                testRuns++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAvgRuntime(int instance) {
        String path = outputPath + heuristicStrategy.getFolderPath();
        String targetInstance = "automatic-" + instance + "_";
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        int sum = 0;
        int amount = 0;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String name = child.getName();
                if (name.contains(targetInstance)) {
                    int lastUnderScore = name.lastIndexOf("_");
                    sum += Integer.parseInt(name.substring(lastUnderScore + 1, name.indexOf("ms")));
                    amount++;
                }
            }
            if (amount == 0) {
                System.out.println("Avg. Runtime for intance-" + instance + ": 0 ms");
            }
            System.out.println("Avg. Runtime for intance-" + instance + ": " + sum / amount + " ms");
        }
    }

    private static void calculateAvgCrossings(int instance) {
        String path = outputPath + heuristicStrategy.getFolderPath();
        String targetInstance = "automatic-" + instance + "_";
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        int sum = 0;
        int amount = 0;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String name = child.getName();
                if (name.contains(targetInstance)) {
                    int stepfunction = name.lastIndexOf("first");
                    if (stepfunction == -1) {
                        stepfunction = name.lastIndexOf("best");
                        if (stepfunction == -1) {
                            stepfunction = name.lastIndexOf("rand");
                        }
                    }
                    int underScore = name.indexOf("_", stepfunction);
                    //int secondUnderScore = name.indexOf("_", underScore+1);
                    int lastUnderScore = name.indexOf("_", underScore + 1);
                    sum += Integer.parseInt(name.substring(lastUnderScore + 1, name.lastIndexOf("_")));
                    amount++;
                }
            }
            if (amount == 0) {
                System.out.println("Avg. Runtime for intance-" + instance + ": 0");
            }
            System.out.println("Avg. Crossings for intance-" + instance + ": " + sum / (double) amount);
        }
    }

    private static void calculateAvgCrossingsBeforeLocalSearch(int instance) {
        String path = outputPath + heuristicStrategy.getFolderPath();
        String targetInstance = "automatic-" + instance + "_";
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        int sum = 0;
        int amount = 0;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String name = child.getName();
                if (name.contains(targetInstance)) {
                    int stepfunction = name.lastIndexOf("first");
                    if (stepfunction == -1) {
                        stepfunction = name.lastIndexOf("best");
                        if (stepfunction == -1) {
                            stepfunction = name.lastIndexOf("rand");
                        }
                    }
                    int underScore = name.indexOf("_", stepfunction);
                    sum += Integer.parseInt(name.substring(underScore + 1, name.indexOf("_", underScore + 1)));
                    amount++;
                }
            }
            if (amount == 0) {
                System.out.println("Avg. Crossings before GVNS for intance-" + instance + ": 0");
            }
            System.out.println("Avg. Crossings before GVNS for intance-" + instance + ": " + sum / amount);
        }
    }
}
