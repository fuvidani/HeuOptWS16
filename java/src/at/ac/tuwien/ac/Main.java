package at.ac.tuwien.ac;

import at.ac.tuwien.ac.heuoptws15.KPMPInstance;

import java.io.FileNotFoundException;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 11.10.16
 */
public class Main {

    public static void main(String[] args) {
        try {
            KPMPInstance instance = KPMPInstance.readInstance("/Users/daniefuvesi/University/Masterstudium/1. Semester/Heuristic Optimization Techniques/Assignment 1/instances/automatic-1.txt");
            System.out.println("K:" + instance.getK() + "\nVertices:" + instance.getNumVertices());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
