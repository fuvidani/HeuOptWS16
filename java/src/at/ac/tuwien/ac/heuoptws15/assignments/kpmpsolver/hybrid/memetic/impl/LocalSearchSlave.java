package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic.impl;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic.LocalSearchCallable;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;

import java.util.List;
import java.util.Random;

import static at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic.impl.MemeticAlgorithm.LOCAL_SEARCH_RATE;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 09.01.2017
 */
public class LocalSearchSlave implements LocalSearchCallable {

    private KPMPLocalSearch localSearch;
    private StepFunction stepFunction;
    private List<Individual> individuals;


    @Override
    public void registerLocalSearch(KPMPLocalSearch localSearch, StepFunction stepFunction) {
        this.localSearch = localSearch;
        this.stepFunction = stepFunction;
    }

    @Override
    public void setIndividualsToOptimize(List<Individual> individuals) {
        this.individuals = individuals;
    }

    @Override
    public List<Individual> call() throws Exception {
        Random random = new Random(Double.doubleToLongBits(Math.random()));
        for (Individual i : individuals) {
            if (random.nextDouble() < LOCAL_SEARCH_RATE) {
                if (!i.needsEvaluation()) {
                    localSearch.setCrossingNumber(i.getNumberOfCrossings());
                }
                i.setGenes(localSearch.improveSolution(i.getGenes(), stepFunction));
                i.setNumberOfCrossings(localSearch.getCrossingNumber());
            }
            localSearch.setCrossingNumber(-1);
        }
        return individuals;
    }
}
