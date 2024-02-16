# README.txt for ML CS7641 Fall 2021
Project: Markov Decsion Process


Check out the framework code that was developed and made public in 2021.  
- [https://github.com/robododge/omscs_ml_a4_burlap](https://github.com/robododge/omscs_ml_a4_burlap)

In the code repository, you will find code for both java and python
* Java code based on the BURLAP RL library and using gradle for builds
* Python code in several Jupiter notebooks

Generating new results
---------------------
1. Make sure you have java installed, preferable Java JDK 15+
2. run these gradle commands from the omscs_ml_a4_burlap directory
   - ./gradlew build
   - ./gradlew publishToMavenLocal
3. run these gradle commands from the cs7641_mdp/experiments directory  
   - ./gradlew MyLrgBDIterVIPI
   _ ./gradlew MyLrgGWGammaVIPI
   - ./gradlew MySmBDGammaVIPI
   - ./gradlew MySmGWGammaVIPI
   - ./gradlew QonlyDBAlpha
   - ./gradlew QonlyGWlpha


Run the plotting
---------------------
1. make sure you have a python environment with python 3 and you have installed the required libraries using the `pip install -r requirements.txt
2. Navigate into each of the notebooks in the notebooks directory listed here are run each cell in all
   plottvipi.ipynb
   q-plott.ipynb

Results:
------------------
Results from running the previous steps will result in png/csv files delivered to two different directries

1.  ./experiments/output/*.csv - CSV files generated from Burlap containing results or RL algorithms
2.  ./notebooks/plots/*.png - Output directory for the various png graphs for use in the pdf paper

Main Libraries used:
-------------------
- burlap		    3.0.1
- java		    11+
