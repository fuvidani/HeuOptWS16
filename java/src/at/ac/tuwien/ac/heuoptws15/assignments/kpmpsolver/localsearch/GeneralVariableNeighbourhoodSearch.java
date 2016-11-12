package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.AbstractKPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.NodeEdgeMove;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.NodeSwap;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.SingleEdgeMove;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by David on 12.11.2016.
 */
public class GeneralVariableNeighbourhoodSearch implements KPMPLocalSearch {

    private List<AbstractKPMPLocalSearch> neighbourhoods_K;
    private List<AbstractKPMPLocalSearch> neighbourhoods_I;
    private Random random = new Random(Double.doubleToLongBits(Math.random()));

    public GeneralVariableNeighbourhoodSearch() {
        this.neighbourhoods_K = new ArrayList<>();
        this.neighbourhoods_K.add(new SingleEdgeMove());
        this.neighbourhoods_K.add(new NodeEdgeMove());
        this.neighbourhoods_K.add(new NodeSwap());

        this.neighbourhoods_I = new ArrayList<>();
        this.neighbourhoods_I.add(new SingleEdgeMove());
        this.neighbourhoods_I.add(new NodeEdgeMove());
        this.neighbourhoods_I.add(new NodeSwap());
    }

    @Override
    public KPMPSolution improveSolution(KPMPSolution initialSolution, StepFunction stepFunction) {
        KPMPSolution bestSolution = initialSolution;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int crossingNumberOfBestSolution = solutionChecker.getCrossingNumber(bestSolution);
        int runCounter = 0;
        do {
            int index_K = 0;
            while (index_K < neighbourhoods_K.size()) {
                neighbourhoods_K.get(index_K).initSearch(initialSolution);
                KPMPSolution randomSolution = neighbourhoods_K.get(index_K).randomNextNeighbour();
                int crossingNumberOfRandomSolution = solutionChecker.getCrossingNumber(randomSolution);
                int index_I = 0;
                while (index_I < neighbourhoods_I.size()) {
                    KPMPSolution solution = VNDSearch(randomSolution,neighbourhoods_I.get(index_I),stepFunction);
                    int crossingNumberOfSolution = solutionChecker.getCrossingNumber(solution);
                    if (crossingNumberOfSolution < crossingNumberOfRandomSolution) {
                        randomSolution = solution;
                        crossingNumberOfRandomSolution = crossingNumberOfSolution;
                        index_I = 0;
                    } else {
                        index_I++;
                    }
                }
                if (crossingNumberOfRandomSolution < crossingNumberOfBestSolution) {
                    bestSolution = randomSolution;
                    crossingNumberOfBestSolution = crossingNumberOfRandomSolution;
                    index_K = 0;
                } else {
                    index_K++;
                }
            }
            runCounter++;
        } while (runCounter < 1 && ((System.nanoTime() - Main.START) / 1000000) < (Main.secondsBeforeStop * 1000));

        return bestSolution;
    }

    private KPMPSolution VNDSearch(KPMPSolution solution, AbstractKPMPLocalSearch localSearch, StepFunction stepFunction) {
        return localSearch.improveSolution(solution,stepFunction);
    }

    @Override
    public String getAbbreviation() {
        return "GVNS";
    }
}
