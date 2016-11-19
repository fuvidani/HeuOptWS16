package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils;

import java.io.File;

/**
 * <h4>About this class</h4>
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 19.10.16
 */
public enum HeuristicStrategy {

    DETERMINISTIC("deterministic_gvns" + File.separator),
    SEMI_RANDOM("semi_random" + File.separator),
    RANDOM("random_gvns" + File.separator);

    private String folderPath;

    HeuristicStrategy(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }
}
