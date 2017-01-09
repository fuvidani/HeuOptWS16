package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.hybrid.memetic;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.KPMPLocalSearch;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.localsearch.stepfunction.StepFunction;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 09.01.2017
 */
public interface LocalSearchCallable extends Callable<List<Individual>> {

    void registerLocalSearch(final KPMPLocalSearch localSearch, final StepFunction stepFunction);

    void setIndividualsToOptimize(final List<Individual> individuals);
}
