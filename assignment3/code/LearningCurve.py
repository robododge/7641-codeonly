
import numpy as np
import pandas as pd

import pathlib
import matplotlib.pylab as plt
from sklearn.model_selection import  StratifiedKFold
from sklearn.metrics import precision_recall_fscore_support, make_scorer, recall_score, f1_score, confusion_matrix, precision_score, balanced_accuracy_score
         
from helper import DConfig

from sklearn.model_selection import learning_curve

class LearningCurve(object):

    def __init__(self, estimator, X, y, train_sizes=np.linspace(0.05,1,30), scorer=make_scorer(recall_score),
          metric_name='recall', classifer_name='MLP', title="dataset-02: Job Change",
          cv = StratifiedKFold(5, shuffle=True, random_state=0),
          dataset_id=2, parameters=None):
        self.estimator = estimator
        self.X = X
        self.y = y
        self.train_sizes = train_sizes
        self.scorer = scorer
        self.metric_name = metric_name
        self.title = title
        self.classifier_name = classifer_name
        self.cv = cv
        self.dataset_id = dataset_id
        self.parameters = parameters

        hold = np.zeros((1,))
        self.train_sizes_post = hold; self.train_scores=hold; self.validation_scores=hold

    def generate_and_save(self):
        self.generate()
        self.save_curr()

    def generate(self):
        print("Generating learning curve")
        self.train_sizes_post, self.train_scores, self.validation_scores = learning_curve(
            estimator = self.estimator,
            X =self.X,
            y =self.y, 
            train_sizes = self.train_sizes, 
            cv = self.cv,
            scoring = self.scorer,
            n_jobs=-1)

    def _get_filepaths(self):
        file_strs = [ "lc-%s-%s_%s.npy" % (self.classifier_name, self.dataset_id, letter) for letter in ['a', 'b', 'c'] ]
        save_path = pathlib.Path.cwd() / 'output' 
        return save_path / file_strs[0], save_path / file_strs[1] , save_path / file_strs[2]

    def _get_filepath(self):
        file_str = "lc-%s-%s.png" % (self.classifier_name, self.dataset_id)
        save_path = pathlib.Path.cwd() / 'output' / file_str
        return save_path

    def save_curr(self):
        a, b, c = self._get_filepaths()
        np.save(a,self.train_sizes_post)
        np.save(b,self.train_scores)
        np.save(c,self.validation_scores)


    def load_and_plot(self, dconfig=DConfig()):
        self._load_curr()
        self.plot(dconfig.save, dconfig.headless) 

    def _load_curr(self):
        paths = self._get_filepaths()
        self.train_sizes_post, self.train_scores, self.validation_scores = np.load(paths[0]), np.load(paths[1]), np.load(paths[2])

        
    def plot(self, save=False, headless=False):
        axs = plt.gca()
        train_scores_mean = self.train_scores.mean(axis = 1)
        validation_scores_mean = self.validation_scores.mean(axis =1 )
        axs.plot(self.train_sizes_post, train_scores_mean, label='Train', marker='.', linewidth=0.75, color='r')
        axs.plot(self.train_sizes_post, validation_scores_mean, label='Validate', marker='.', linewidth=0.75, color='g')
        axs.legend(); axs.set_ylabel(f"Metric: {self.metric_name}"); axs.set_xlabel("Training sizes")
        axs.set_title(f"{self.classifier_name} Learning curve\n{self.title}")

        fig = plt.gcf()
        if self.parameters:
            # txt ='param1 = 999\nparam2=8898\n'
            txt = make_param_txt(self.parameters)
            axs.text(1.02, 0.9, txt, verticalalignment='top', horizontalalignment='left', 
             transform=axs.transAxes,  fontsize=8 , rotation=-90)
            plt.subplots_adjust(right=0.8)

        if not headless:
            plt.show()
        if save:
            full_P = self._get_filepath().as_posix()
            print (f"Saving file: {full_P}")
            fig.savefig(full_P)
        plt.close()

def make_param_txt(p_dict):
    out = ''
    for p,v in p_dict.items():
        out += f"{p}: {v}\n"
    return out
