package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.callable.generation;

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm.Individual;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 07.12.2016
 */
public interface SolutionGenerationCallable extends Callable<List<Individual>> {

    void setNumberOfSolutionsToGenerate(int solutionsToGenerate);

    void setBaseInstance(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> edgePartitioning);
}
