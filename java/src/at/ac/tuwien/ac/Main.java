package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomCFLHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.GeneralVariableNeighbourhoodSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.RandomStepFunction;
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

    public static final int secondsBeforeStop = 840;   // 840 ~ 14 minutes
    public static long START;
    public static int iterationMultiplier;
    public static int crossingsBeforeLocalSearch;
    public static final HeuristicStrategy heuristicStrategy = HeuristicStrategy.GA;
    public static int maxCrossingNumber;
    public static final double lowerBound = 2000;

    private static String inputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/instances/";
    private static String outputPath = "/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/HeuOptWS16/solutions/";
    //private static String inputPath = "E:\\HeuOptWS16\\instances\\";
    //private static String outputPath = "E:\\HeuOptWS16\\solutions\\";
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
                int instanceCounter = 7;
                while (instanceCounter != 8) {
                    if (instanceCounter < 6) {
                        iterationMultiplier = 2000;
                    } else if (instanceCounter == 6) {
                        iterationMultiplier = 500;
                    } else {
                        iterationMultiplier = 1000;
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
                        StepFunction stepFunction = new RandomStepFunction();
                        KPMPLocalSearch localSearch = new GeneralVariableNeighbourhoodSearch();
                        kpmpSolver.setHeuristicType(KPMPSolver.HeuristicType.COMBINED);
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
