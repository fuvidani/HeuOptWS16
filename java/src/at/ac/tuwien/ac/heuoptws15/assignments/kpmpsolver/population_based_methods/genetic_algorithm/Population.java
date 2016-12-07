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
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionChecker;
import at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils.KPMPSolutionWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

public class Population {
    private Random m_rand = new Random();  // random-number generator
    private List<Individual> m_population;
    private int totalFitness;
    private int POP_SIZE;

    public Population(int size) {
        m_population = new ArrayList<>();
        POP_SIZE = size;
    }

    public void setPopulation(List<Individual> newPop) {
        this.m_population = newPop.stream().map(Individual::clone).collect(toCollection(ArrayList::new));
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
        /*
        * fs = [fitness(i) for i in population]
        sum_fs = sum(fs)
        max_fs = max(fs)
        min_fs = min(fs)
        p = random()*sum_fs
        t = max_fs + min_fs
        chosen = population[0]
        for i in population:
            if MAXIMIZATION:
                 p -= fitness(i)
            elseif MINIMIZATION:
                p -= (t - fitness(i))
            f p < 0:
                chosen = i
            break
        return chosen */
        /*int max = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < POP_SIZE;i++){
            int value = m_population.get(i).getFitnessValue();
            if (value > max){
                max = value;
            }
            if (value < min){
                min = value;
            }
        }
        int t = max + min;

        final int maxF = max;
        List<Integer> inverseInts = new ArrayList<>(m_population.size());
        int inversTotalFitness = 0;
        for (Individual individual : m_population){
            int fitness = maxF - individual.getFitnessValue();
            inverseInts.add(fitness);
            inversTotalFitness += fitness;
        }*/
        m_rand = new Random(Double.doubleToLongBits(Math.random()));
        double randNum = m_rand.nextDouble() * totalFitness;
        int idx;
        for (idx = 0; idx < POP_SIZE && randNum > 0; ++idx) {
            randNum -= -m_population.get(idx).getFitnessValue();
        }
        return m_population.get(idx - 1);

    }

    public Individual tournamentSelection() {
        m_rand = new Random(Double.doubleToLongBits(Math.random()));
        int numberOfParticipants = (int) Math.ceil(POP_SIZE * 0.5);
        List<Individual> participants = new ArrayList<>(numberOfParticipants);
        for (int i = 0; i < numberOfParticipants; i++) {
            int index = m_rand.nextInt(m_population.size());
            participants.add(m_population.get(index));
        }
        participants.sort(Comparator.comparingDouble(Individual::getNumberOfCrossings));
        return participants.get(0);
    }

    public Individual findBestIndividual() {
        int idxMin = 0;
        int currentMin = Integer.MAX_VALUE;

        for (int idx = 0; idx < POP_SIZE; ++idx) {
            int currentVal = m_population.get(idx).getNumberOfCrossings();
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
        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o4 -> o4.a));
        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o3 -> o3.b));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o2 -> o2.a));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o -> o.b));

        int randPointForSpineOrder = m_rand.nextInt(mother.getGenes().getSpineOrder().size() - 1) + 1;
        int randPointForEdgePartition = m_rand.nextInt(mother.getGenes().getEdgePartition().size() - 1) + 1;

        // child1 spine order
        List<Integer> child1SpineOrder = new ArrayList<>();
        List<Integer> fatherSpineOrderCopy = father.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int value = mother.getGenes().getSpineOrder().get(i);
            child1SpineOrder.add(value);
            fatherSpineOrderCopy.remove(new Integer(value));
        }
        for (int i = 0; i < fatherSpineOrderCopy.size(); i++) {
            child1SpineOrder.add(fatherSpineOrderCopy.get(i));
        }

        // child1 edge partition
        List<KPMPSolutionWriter.PageEntry> child1EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child1EdgePartition.add(mother.getGenes().getEdgePartition().get(i).clone());
        }
        for (int i = randPointForEdgePartition; i < father.getGenes().getEdgePartition().size(); i++) {
            child1EdgePartition.add(father.getGenes().getEdgePartition().get(i).clone());
        }
        child1.setGenes(new KPMPSolution(child1SpineOrder,child1EdgePartition,mother.getGenes().getNumberOfPages()));

        // child2 spine order
        List<Integer> child2SpineOrder = new ArrayList<>();
        List<Integer> motherSpineOrderCopy = mother.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int vertex = father.getGenes().getSpineOrder().get(i);
            child2SpineOrder.add(vertex);
            motherSpineOrderCopy.remove(new Integer(vertex));
        }
        for (int i = 0; i < motherSpineOrderCopy.size(); i++) {
            child2SpineOrder.add(motherSpineOrderCopy.get(i));
        }

        // child2 edge partition
        List<KPMPSolutionWriter.PageEntry> child2EdgePartition = new ArrayList<>();
        for (int i = 0; i < randPointForEdgePartition; i++) {
            child2EdgePartition.add(father.getGenes().getEdgePartition().get(i).clone());
        }
        for (int i = randPointForEdgePartition; i < mother.getGenes().getEdgePartition().size(); i++) {
            child2EdgePartition.add(mother.getGenes().getEdgePartition().get(i).clone());
        }
        child2.setGenes(new KPMPSolution(child2SpineOrder,child2EdgePartition,mother.getGenes().getNumberOfPages()));

        List<Individual> children = new ArrayList<>();
        child1.evaluate();
        child2.evaluate();
        children.add(child1);
        children.add(child2);

        return children;
    }

    public List<Individual> recombination(Individual mother, Individual father) {
        Individual child1 = new Individual();
        Individual child2 = new Individual();

        //List<Integer> newSpineOrderChild1 = father.getGenes().getSpineOrder().stream().collect(Collectors.toCollection(ArrayList::new));
        //List<Integer> newSpineOrderChild2 = mother.getGenes().getSpineOrder().stream().collect(Collectors.toCollection(ArrayList::new));

        int randPointForSpineOrder = m_rand.nextInt(mother.getGenes().getSpineOrder().size() - 1) + 1;

        // child1 spine order
        List<Integer> newSpineOrderChild1 = new ArrayList<>();
        List<Integer> fatherSpineOrderCopy = father.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int value = mother.getGenes().getSpineOrder().get(i);
            newSpineOrderChild1.add(value);
            fatherSpineOrderCopy.remove(new Integer(value));
        }
        for (int i = 0; i < fatherSpineOrderCopy.size(); i++) {
            newSpineOrderChild1.add(fatherSpineOrderCopy.get(i));
        }

        // child2 spine order
        List<Integer> newSpineOrderChild2 = new ArrayList<>();
        List<Integer> motherSpineOrderCopy = mother.getGenes().getSpineOrder().stream().collect(toCollection(ArrayList::new));
        for (int i = 0; i < randPointForSpineOrder; i++) {
            final int vertex = father.getGenes().getSpineOrder().get(i);
            newSpineOrderChild2.add(vertex);
            motherSpineOrderCopy.remove(new Integer(vertex));
        }
        for (int i = 0; i < motherSpineOrderCopy.size(); i++) {
            newSpineOrderChild2.add(motherSpineOrderCopy.get(i));
        }


        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o4 -> o4.a));
        father.getGenes().getEdgePartition().sort(Comparator.comparingInt(o3 -> o3.b));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o2 -> o2.a));
        mother.getGenes().getEdgePartition().sort(Comparator.comparingInt(o -> o.b));

        List<KPMPSolutionWriter.PageEntry> edgePartitionChild1 = new ArrayList<>();
        List<KPMPSolutionWriter.PageEntry> edgePartitionChild2 = new ArrayList<>();

        int totalCrossingChild1 = 0;
        int totalCrossingChild2 = 0;
        KPMPSolutionChecker checker = new KPMPSolutionChecker();
        for (int i = 0; i < father.getGenes().getEdgePartition().size(); i++) {
            KPMPSolutionWriter.PageEntry motherEdge = mother.getGenes().getEdgePartition().get(i);
            KPMPSolutionWriter.PageEntry fatherEdge = father.getGenes().getEdgePartition().get(i);
            edgePartitionChild1.add(i, motherEdge.clone());
            edgePartitionChild2.add(i, motherEdge.clone());
            int motherCrossings = checker.getCrossingNumberOfEdge(newSpineOrderChild1, edgePartitionChild1, motherEdge.page, motherEdge);
            int motherCrossings2 = checker.getCrossingNumberOfEdge(newSpineOrderChild2, edgePartitionChild2, motherEdge.page, motherEdge);
            edgePartitionChild1.remove(i);
            edgePartitionChild2.remove(i);
            edgePartitionChild1.add(i, fatherEdge.clone());
            edgePartitionChild2.add(i, fatherEdge.clone());
            int fatherCrossings = checker.getCrossingNumberOfEdge(newSpineOrderChild1, edgePartitionChild1, fatherEdge.page, fatherEdge);
            int fatherCrossings2 = checker.getCrossingNumberOfEdge(newSpineOrderChild2, edgePartitionChild2, fatherEdge.page, fatherEdge);
            if (motherCrossings < fatherCrossings) {
                edgePartitionChild1.remove(i);
                edgePartitionChild1.add(i, motherEdge.clone());
                totalCrossingChild1 += motherCrossings;
            } else {
                totalCrossingChild1 += fatherCrossings;
            }
            if (motherCrossings2 < fatherCrossings2) {
                edgePartitionChild2.remove(i);
                edgePartitionChild2.add(i, motherEdge.clone());
                totalCrossingChild2 += motherCrossings;
            } else {
                totalCrossingChild2 += fatherCrossings;
            }

        }
        child1.setGenes(new KPMPSolution(newSpineOrderChild1, edgePartitionChild1, father.getGenes().getNumberOfPages()));
        child2.setGenes(new KPMPSolution(newSpineOrderChild2, edgePartitionChild2, father.getGenes().getNumberOfPages()));
        child1.setNumberOfCrossings(totalCrossingChild1);
        child2.setNumberOfCrossings(totalCrossingChild2);
        //assert (checker.getCrossingNumber(child1.getGenes()) == totalCrossingChild1);

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
