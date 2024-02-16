package helpers.runner;

public interface ProblemHolder {

    String getProblemName();
    String getProblemMetaData(String runId);



    void resetEvalFunc();
}
