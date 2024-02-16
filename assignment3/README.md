============================================
README.txt for ML CS7641 Fall 2021
Project: Unsupervised Learning and Dimensionality Reduction

In the code repository, you will find code for both java and python
* Python code in several Jupiter notebooks
* Python code in python files used for data prep and NN learning curves


Running the code
---------------------
1. make sure you have a python environment with python 3 and you have installed the required libraries using the `pip install -r requirements.txt
2. cd the the ./code directory and run the data preprocessor 
     `PYTHONPATH=..:. python Preprocess.py`
3. from the same directory, run the learning curve and final analysis Driver
    `PYTHONPATH=..:. python driver_learning.py`
4. Now, cd back to the notebooks directory and start a jupyternotebook kernel
2. Navigate into each of the notebooks listed here are run each cell in all
   - 00A_clustering.ipynb
   - 0123_em_analysis.ipynb
   - 0123_kmean_analysis.ipynb
   - 045_mlp.ipynb

Results:
------------------
Results from running the previous steps will result in png/csv files delivered to two different directries

1.  ./code/output/*.png - Learning curve plots for all alogorithms
2.  ./notebooks/plots/*.png - Output directory for the various png graphs for use in the pdf paper
3.  ./notebooks/plots/*.csv - Output directory the NN run results

Main Libraries used:
-------------------
jupyterlab          3.1.11
matplotlib          3.4.3
numpy               1.21.2
pandas              1.3.2
scikit-learn        0.24.2

See requirements.txt for extensive list of the entire python environment
