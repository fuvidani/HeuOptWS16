package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch;

import at.ac.tuwien.ac.Main;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.neighbourhoods.*;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 12.11.2016.
 */
public class GeneralVariableNeighbourhoodSearch implements KPMPLocalSearch {

    private List<AbstractKPMPLocalSearch> neighbourhoods_K;
    private List<AbstractKPMPLocalSearch> neighbourhoods_I;

    public GeneralVariableNeighbourhoodSearch() {
        this.neighbourhoods_K = new ArrayList<>();
        this.neighbourhoods_K.add(new SingleEdgeMove());
        this.neighbourhoods_K.add(new NodeEdgeMove());
        this.neighbourhoods_K.add(new DoubleNodeSwap());

        this.neighbourhoods_I = new ArrayList<>();
        this.neighbourhoods_I.add(new SingleEdgeMove());
        this.neighbourhoods_I.add(new NodeSwap());
        this.neighbourhoods_I.add(new DoubleEdgeMove());

    }

    @Override
    public KPMPSolution improveSolution(KPMPSolution initialSolution, StepFunction stepFunction) {
        KPMPSolution bestSolution = initialSolution;
        KPMPSolutionChecker solutionChecker = new KPMPSolutionChecker();
        int crossingNumberOfBestSolution = solutionChecker.getCrossingNumber(bestSolution);
        System.out.println("Number of crossings before local search: " + crossingNumberOfBestSolution);
        int runCounter = 0;
        int runsWithoutImprovement = 0;
        do {
            int index_K = 0;
            runsWithoutImprovement++;
            while (!runTimeLimitExceeded() && index_K < neighbourhoods_K.size()) {
                neighbourhoods_K.get(index_K).initSearch(bestSolution);
                KPMPSolution randomSolution = neighbourhoods_K.get(index_K).randomNextNeighbour();
                int crossingNumberOfRandomSolution = solutionChecker.getCrossingNumber(randomSolution);

                int index_I = 0;
                while (!runTimeLimitExceeded() && index_I < neighbourhoods_I.size()) {
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
                    runsWithoutImprovement = 0;
                } else {
                    index_K++;
                }
            }
            if (crossingNumberOfBestSolution == 0){
                break;
            }
            runCounter++;
        } while (runsWithoutImprovement < 2000 && !runTimeLimitExceeded());

        return bestSolution;
    }

    private KPMPSolution VNDSearch(KPMPSolution solution, AbstractKPMPLocalSearch localSearch, StepFunction stepFunction) {
        return localSearch.improveSolution(solution,stepFunction);
    }

    private boolean runTimeLimitExceeded(){
        return ((System.nanoTime() - Main.START) / 1000000) >= (Main.secondsBeforeStop * 1000);
    }

    @Override
    public String getAbbreviation() {
        return "GVNS";
    }
}
