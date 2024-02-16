# README.txt for ML CS7641 Fall 2021
Project: Randomized Optimization

In the code repository, you will find code for both java and python
* Java code uses the ABAGAIL library is is structured using gradle 
* Python code is single Jupiter notebook used for plotting

During this project, I realized the K-Coloring examples in the ABAGAIL library were not really matching how K-Color randomization should work.  So I refactored and submitted this [Pull Request for k-coloring](https://github.com/pushkar/ABAGAIL/pull/95)


Run the plotting
---------------------
1. make sure you have a python environment with python 3 and you have installed the required libraries using the `pip install -r requirements.txt
2. Navigate into each of the notebooks listed here are run each cell in all
   plotter.ipynb
   02_mlp.ipynb
 
Generating new results
---------------------
1. Run `./gradlew assemble`
2. Run individual java commands to stimulate new runs of the ABAGAIL optimization code
   - java -cp build/libs/assingnment2-1.0-SNAPSHOT.jar testruns.FlipFlopProblem
   - java -cp build/libs/assingnment2-1.0-SNAPSHOT.jar testruns.FourPeaksProblem
   - java -cp build/libs/assingnment2-1.0-SNAPSHOT.jar testruns.MaxKColoringProblem
   - java -cp build/libs/assingnment2-1.0-SNAPSHOT.jar testruns.NNOptimizer



Results:
------------------
Results from running the previous steps will result in png/csv files delivered to two different directries

1.  ./code/output/*.png - Learning curve plots for all alogorithms
2.  ./code/output/*.csv - final alogorithm comparisons
3.  ./notebooks/plots/*.png - Output directory for the various png graphs for use in the pdf paper

Main Libraries used:
-------------------
- ABAGAIL
- Java 1.16
- Gradle 7.0

See requirements.txt for extensive list of the entire python environment
