package helpers.runner;

import java.util.Set;

public interface RunnerHarness {

    void initialize(ProblemHolder ph, String rootPath);
    void runProblemWithAllOptimizers(int epochCount, Set<String> optimizers);

}
