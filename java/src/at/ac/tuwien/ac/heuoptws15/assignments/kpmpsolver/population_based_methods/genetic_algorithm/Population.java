package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.population_based_methods.genetic_algorithm;

/**
 * <h4>About this class</h4>
 * <p>
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.0.1
 * @since 04.12.2016
 */

import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.KPMPEdgePartitionHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.edgepartitioning.impl.KPMPEdgePartitionRandomHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.KPMPSpineOrderHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.construction_heuristics.spineordering.impl.KPMPSpineOrderRandomDFSHeuristic;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPInstance;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolution;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

public class Population {
    private static Random m_rand = new Random();  // random-number generator
    private List<Individual> m_population;
    private int totalFitness;
    private int POP_SIZE;

    public Population(int size) {
        m_population = new ArrayList<>();
        POP_SIZE = size;
    }

    public void setPopulation(List<Individual> newPop) {
        this.m_population = newPop.stream().collect(toCollection(ArrayList::new));
    }

    public List<Individual> getPopulation() {
        return this.m_population;
    }

    public int evaluate() {
        this.totalFitness = 0;
        for (int i = 0; i < POP_SIZE; i++) {
            this.totalFitness += m_population.get(i).evaluate();
        }
        return this.totalFitness;
    }

    public Individual rouletteWheelSelection() {
        double randNum = m_rand.nextDouble() * this.totalFitness;
        int idx;
        for (idx = 0; idx < POP_SIZE && randNum > 0; ++idx) {
            randNum -= m_population.get(idx).getFitnessValue();
        }
        return m_population.get(idx - 1);
    }

    public Individual findBestIndividual() {
        int idxMin = 0;
        int currentMin = Integer.MAX_VALUE;

        for (int idx = 0; idx < POP_SIZE; ++idx) {
            int currentVal = m_population.get(idx).getFitnessValue();
            if (currentVal < currentMin) {
                currentMin = currentVal;
                idxMin = idx;
            }
        }

        return m_population.get(idxMin);
    }

    public List<Individual> crossover(Individual mother, Individual father) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();

        // sort edges
        father.getGenes().getEdgePartition().sort(new Comparator<KPMPSolutionWriter.PageEntry>() {
            @Override
            public int compare(KPMPSolutionWriter.PageEntry o1, KPMPSolutionWriter.PageEntry o2) {
                return o1.a - o2.a;
            }
        });
        father.getGenes().getEdgePartition().sort(new Comparator<KPMPSolutionWriter.PageEntry>() {
            @Override
            public int compare(KPMPSolutionWriter.PageEntry o1, KPMPSolutionWriter.PageEntry o2) {
                return o1.b - o2.b;
            }
        });
        mother.getGenes().getEdgePartition().sort(new Comparator<KPMPSolutionWriter.PageEntry>() {
            @Override
            public int compare(KPMPSolutionWriter.PageEntry o1, KPMPSolutionWriter.PageEntry o2) {
                return o1.a - o2.a;
            }
        });
        mother.getGenes().getEdgePartition().sort(new Comparator<KPMPSolutionWriter.PageEntry>() {
            @Override
            public int compare(KPMPSolutionWriter.PageEntry o1, KPMPSolutionWriter.PageEntry o2) {
                return o1.b - o2.b;
            }
        });

/*        for (int i = 0; i < mother.getGenes().getEdgePartition().size(); i++) {
            System.out.println(mother.getGenes().getEdgePartition().get(i) + "\t" + father.getGenes().getEdgePartition().get(i));
        }*/

        int randPointForSpineOrder = m_rand.nextInt(mother.getGenes().getSpineOrder().size() - 1) + 1;
        int randPointForEdgePartition = m_rand.nextInt(mother.getGenes().getEdgePartition().size() - 1) + 1;

        // child1 spine order
        List<Integer> child1SpineOrder = new ArrayList<>();
        List<Integer> fatherSpineOrderCopy = father.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            child1SpineOrder.add(mother.getGenes().getSpineOrder().get(i));
            fatherSpineOrderCopy.remove(new Integer(i));
        }
        for (int i = 0; i < fatherSpineOrderCopy.size(); i++) {
            child1SpineOrder.add(fatherSpineOrderCopy.get(i));
        }

        // child1 edge partition
        List<KPMPSolutionWriter.PageEntry> child1EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child1EdgePartition.add(mother.getGenes().getEdgePartition().get(i));
        }
        for (int i = randPointForEdgePartition; i < father.getGenes().getEdgePartition().size(); i++) {
            child1EdgePartition.add(father.getGenes().getEdgePartition().get(i));
        }
        child1.setGenes(new KPMPSolution(child1SpineOrder,child1EdgePartition,mother.getGenes().getNumberOfPages()));

        // child2 spine order
        List<Integer> child2SpineOrder = new ArrayList<>();
        List<Integer> motherSpineOrderCopy = mother.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            child2SpineOrder.add(father.getGenes().getSpineOrder().get(i));
            motherSpineOrderCopy.remove(new Integer(i));
        }
        for (int i = 0; i < motherSpineOrderCopy.size(); i++) {
            child2SpineOrder.add(motherSpineOrderCopy.get(i));
        }

        // child2 edge partition
        List<KPMPSolutionWriter.PageEntry> child2EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child2EdgePartition.add(father.getGenes().getEdgePartition().get(i));
        }
        for (int i = randPointForEdgePartition; i < mother.getGenes().getEdgePartition().size(); i++) {
            child2EdgePartition.add(mother.getGenes().getEdgePartition().get(i));
        }
        child2.setGenes(new KPMPSolution(child2SpineOrder,child2EdgePartition,mother.getGenes().getNumberOfPages()));

        List<Individual> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        return children;
    }

    public void generatePopulation(KPMPInstance instance, List<KPMPSolutionWriter.PageEntry> originalEdgePartitioning) {
        KPMPSpineOrderHeuristic spineOrderHeuristic = new KPMPSpineOrderRandomDFSHeuristic();
        KPMPEdgePartitionHeuristic edgePartitionHeuristic = new KPMPEdgePartitionRandomHeuristic();
        for (int i = 0; i < POP_SIZE; i++) {
            KPMPSolution solution = new KPMPSolution();

            List<Integer> calculatedSpineOrder = spineOrderHeuristic.calculateSpineOrder(instance,originalEdgePartitioning,instance.getK(),false);
            solution.setSpineOrder(calculatedSpineOrder);
            solution.setEdgePartition(edgePartitionHeuristic.calculateEdgePartition(instance,calculatedSpineOrder,spineOrderHeuristic.getNumberOfCrossingsForNewSpineOrder()));
            solution.setNumberOfPages(instance.getK());
            solution.setSpineOrder(edgePartitionHeuristic.getSpineOrder());

            Individual individual = new Individual();
            individual.setGenes(solution);
            m_population.add(individual);
        }
    }

    public int getTotalFitness() {
        return totalFitness;
    }
}
