# README.txt for ML CS7641 Fall 2021
Project: Supervised Learning

In the code repository, you will see two top level directories:
* /code - contains python py code files for data preprocessing, building learning curves and final model comparison analysis
* /notebooks - contains juypeter notebooks used for all hyperparameter evaluation and ggeneration of validation curves


 Running the code
---------------------
1. make sure you have a python environment with python 3 and you have installed the required libraries using the `pip install -r requirements.txt
2. cd the the ./code directory and run the data preprocessor 
     `PYTHONPATH=..:. python Preprocess.py`
3. from the same directory, run the learning curve and final analysis Driver
     `PYTHONPATH=..:. python Driver.py`
4. Now, cd back to the notebooks directory and start a jupyternotebook kernel
5. Navigate into each of the notebooks listed here are run each cell in all
   - 01_tree.ipynb
   - 02_mlp.ipynb
   - 01_knn.ipynb
   - 01_svm.ipynb
   - 01_boost.ipynb 

Results:
------------------
Results from running the previous steps will result in png/csv files delivered to two different directries

1.  ./code/output/*.png - Learning curve plots for all alogorithms
2.  ./code/output/*.csv - final alogorithm comparisons
3.  ./notebooks/plots/*.png - Validateion curve plots

Main Libraries used:
-------------------
- jupyterlab          3.1.11
- matplotlib          3.4.3
- numpy               1.21.2
- pandas              1.3.2
- scikit-learn        0.24.2

See requirements.txt for extensive list of the entire python environment
