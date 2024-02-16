package helpers.runner;

public interface ProblemHolderStringInject extends ProblemHolder{

    void runRHC(String outputFilename);
    void runSA(String outputFilename);
    void runGA(String outputFilename);
    void runMIMIC(String outputFilename);
}
