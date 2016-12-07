package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.evolution;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Population;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 07.12.16
 */
public interface IGASlaveCallable extends Callable<List<Individual>> {

    void setCurrentPopulation(final Population population);

    void setAmountOfIndividualsToCareFor(final int size);

    void adjustMutationRate(final double mutationRate);

    void setNodeSwapMutation(boolean includeNodeSwap);

}
