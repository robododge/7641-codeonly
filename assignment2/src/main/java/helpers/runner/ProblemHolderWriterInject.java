package helpers.runner;

import helpers.CSVWriterGeneric;

public interface ProblemHolderWriterInject extends ProblemHolder {

    void runRHC(CSVWriterGeneric csvWriter);
    void runSA(CSVWriterGeneric csvWriter);
    void runGA(CSVWriterGeneric csvWriter);
    void runMIMIC(CSVWriterGeneric csvWriter);
}
