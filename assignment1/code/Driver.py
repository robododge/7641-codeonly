import numpy as np
import pandas as pd
import time
import matplotlib.pylab as plt
from scipy.sparse.construct import rand
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV, train_test_split, StratifiedKFold, cross_validate

from sklearn.neural_network import MLPClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC, LinearSVC
from sklearn.ensemble import AdaBoostClassifier


from sklearn.metrics import precision_recall_fscore_support, make_scorer, recall_score, f1_score, confusion_matrix, precision_score, balanced_accuracy_score
from sklearn.metrics import fbeta_score, ConfusionMatrixDisplay, classification_report

from sklearn.preprocessing import LabelEncoder, MinMaxScaler

from helper import load_jobchange, load_jobchange_test_untouched, load_malicious, DConfig, load_malicious_test_untouched, print_cm, save_df_csv

from LearningCurve import LearningCurve

# class DConfig():
#     def __init__(self, headless=False, save=False):
#         self.headless = headless
#         self.save = save

def get_scorers():
    scorer_j= make_scorer(f1_score)
    scorer_m= make_scorer(recall_score)
    return scorer_j, scorer_m


def loop_estimators(config, run_data):
    for estimator, Xy, scorer, metric_name, classifier_name, title, id, parameters in run_data:
        
        print(f"Estimator {classifier_name} id:{id}\n {estimator.get_params()}")
        X, y = Xy
        learning_curv = LearningCurve(estimator, X, y, scorer=scorer, metric_name=metric_name, 
             classifer_name=classifier_name, title=title, dataset_id=id , parameters=parameters)
        if not config.dry_run:
            learning_curv.generate_and_save()
            learning_curv.load_and_plot(dconfig=config)

def mlp_cls_mal():
    #Dataset -01
    mal_params = {'hidden_layer_sizes':(172,), 'activation':'logistic'}
    mlpMal = MLPClassifier(hidden_layer_sizes=mal_params['hidden_layer_sizes'], activation=mal_params['activation'],  max_iter=500, random_state=0)
    return mlpMal, mal_params

def mlp_cls_job():
    #Dataset -02 - normal
    job_params = {'neuron_layer1':7}
    mlpJob = MLPClassifier(hidden_layer_sizes=job_params['neuron_layer1'], max_iter=200, random_state=0)
    return mlpJob, job_params

def run_mlp(config):
    scorer_j, scorer_m = get_scorers()
    Xj,yj = load_jobchange()
    Xm,ym = load_malicious()
    
    mlpMal, mal_params = mlp_cls_mal()
    mlpJob, job_params = mlp_cls_job()

    #Dataset -02 - SGD
    job_params_sgd = { 'solver':'sgd', 'momentum':0.6, 'learning_rate_init':0.0015, 'alpha':0.0016444444444444447, 'neuron_layer1':7}
    mlpJob_sgd =  MLPClassifier(hidden_layer_sizes=(job_params_sgd['neuron_layer1'],), max_iter=200, solver=job_params_sgd['solver'], learning_rate='invscaling',
                        momentum=job_params_sgd['momentum'], learning_rate_init=job_params_sgd['learning_rate_init'], alpha=job_params_sgd['alpha'], random_state=0 )
  
    run_data = [] 

    #Warning - this first MLP takes over 15 minutes
    run_data.append((mlpMal, (Xm,ym), scorer_m, 'Recall', 'MLP', 'dataset-01: Malicious Server Hack', 1, mal_params))
    run_data.append((mlpJob, (Xj,yj), scorer_j, 'F1 Score', 'MLP', 'dataset-02: Job Change', 2, job_params))
    run_data.append((mlpJob_sgd, (Xj,yj), scorer_j, 'F1 Score', 'MLP', 'dataset-02: Job Change - SGD comparison', '2sgd', job_params_sgd))

    loop_estimators(config, run_data)

def knn_cls_mal():
    mal_params_u = {'k':1, 'weights':'uniform'}
    knnM_u = KNeighborsClassifier( mal_params_u['k'], weights=mal_params_u['weights'] )
    return knnM_u, mal_params_u

def knn_cls_job():
    job_params_u = {'k':9, 'weights':'uniform'}
    knnJ_u = KNeighborsClassifier( job_params_u['k'], weights=job_params_u['weights'] )
    return knnJ_u, job_params_u


def run_knn(config):
    knnM_u, mal_params_u  = knn_cls_mal()
    knnJ_u, job_params_u = knn_cls_job()

    mal_params_d = {'k':1, 'weights':'distance', 'metric':'manhattan'}
    job_params_d = {'k':9, 'weights':'distance', 'metric':'manhattan'}
    knnM_d = KNeighborsClassifier( mal_params_d['k'], weights=mal_params_d['weights'], metric=mal_params_d['metric'] )
    knnJ_d = KNeighborsClassifier( job_params_d['k'], weights=job_params_d['weights'], metric=job_params_d['metric'] )
    Xj,yj = load_jobchange()
    Xm,ym = load_malicious()
    scorer_j, scorer_m = get_scorers()

    # mal_params_d = {'k':15, 'weights':'distance', 'metric':'manhattan'}

    run_data = [] 
    run_data.append((knnM_u, (Xm,ym), scorer_m, 'Recall', 'KNN', 'dataset-01: Malicious Server Hack', '1u', mal_params_u))
    run_data.append((knnM_d, (Xm,ym), scorer_m, 'Recall', 'KNN', 'dataset-01: Malicious Server Hack', '1d', mal_params_d))
    run_data.append((knnJ_u, (Xj,yj), scorer_j, 'F1 Score', 'KNN', 'dataset-02: Job Change', '2u', job_params_u))
    run_data.append((knnJ_d, (Xj,yj), scorer_j, 'F1 Score', 'KNN', 'dataset-02: Job Change', '2d', job_params_d))
    loop_estimators(config, run_data)


def dt_cls_mal():
    mal_params = {'ccp_alpha':0.00018, 'min_samples_leaf':1}
    dtM = DecisionTreeClassifier(ccp_alpha=mal_params['ccp_alpha'], min_samples_leaf=mal_params['min_samples_leaf'])
    return dtM, mal_params

def dt_cls_job():
    job_params = {'max_depth':6,'min_samples_leaf':220}
    dtJ = DecisionTreeClassifier(max_depth=job_params['max_depth'], min_samples_leaf=job_params['min_samples_leaf'])
    return dtJ, job_params

def run_dt(config):
    Xj,yj = load_jobchange()
    Xm,ym = load_malicious()
    scorer_j, scorer_m = get_scorers()

    dtM, mal_params = dt_cls_mal() 
    dtJ, job_params = dt_cls_job()
    run_data = [] 
    run_data.append((dtM, (Xm,ym), scorer_m, 'Recall', 'DT', 'dataset-01: Malicious Server Hack', '1', mal_params))
    run_data.append((dtJ, (Xj,yj), scorer_j, 'F1 Score', 'DT', 'dataset-02: Job Change', '2', job_params))
    loop_estimators(config, run_data)

def svm_cls_mal():
    mal_params = { 'kernel':'poly', 'degree':6, 'class_weight':'balanced', 'max_iter':7000} 
    svcM = SVC(max_iter=mal_params['max_iter'], kernel=mal_params['kernel'], degree=mal_params['degree'], class_weight=mal_params['class_weight'], random_state=0 )
    return svcM, mal_params

def svm_cls_job():
    job_params = { 'kernel':'rbf', 'gamma':0.0036, 'class_weight':'balanced','max_iter':30000 }
    svcJ = SVC(max_iter=job_params['max_iter'], kernel=job_params['kernel'], class_weight=job_params['class_weight'], gamma=job_params['gamma'], random_state=0)
    return svcJ, job_params

def run_svm(config):
    Xj,yj = load_jobchange()
    Xm,ym = load_malicious()
    scorer_j, scorer_m = get_scorers()

    svcM, mal_params = svm_cls_mal()
    svcJ, job_params = svm_cls_job()

    run_data = []
    run_data.append((svcM, (Xm,ym), scorer_m, 'Recall', 'SVM', 'dataset-01: Malicious Server Hack', '1', mal_params))
    run_data.append((svcJ, (Xj,yj), scorer_j, 'F1 Score', 'SVM', 'dataset-02: Job Change', '2', job_params))
    loop_estimators(config, run_data)


def boost_csl_mal():
    mal_params = { 'n_estimators':50, 'learning_rate':4.11 } 
    adaM = AdaBoostClassifier( n_estimators=mal_params['n_estimators'], learning_rate=mal_params['learning_rate'],random_state=0)
    return adaM, mal_params

def boost_csl_job():
    job_params = { 'n_estimators':5, 'learning_rate':1.4} 
    adaJ = AdaBoostClassifier( n_estimators=job_params['n_estimators'], learning_rate=job_params['learning_rate'],random_state=0)
    return adaJ, job_params

def run_boost(config):
    Xj,yj = load_jobchange()
    Xm,ym = load_malicious()
    scorer_j, scorer_m = get_scorers()

    adaM, mal_params = boost_csl_mal()
    adaJ, job_params = boost_csl_job()

    run_data = []
    run_data.append((adaM, (Xm,ym), scorer_m, 'Recall', 'AdaBoost', 'dataset-01: Malicious Server Hack', '1', mal_params))
    run_data.append((adaJ, (Xj,yj), scorer_j, 'F1 Score', 'AdaBoost', 'dataset-02: Job Change', '2', job_params))
    loop_estimators(config, run_data)


def fit_analyze_mal():

    Xtrain, ytrain =  load_malicious()
    Xtest, ytest =  load_malicious_test_untouched()

    analyze_mal =[]
    analyze_mal.append(('DT-Malicious', 'Decision Tree', dt_cls_mal(), -1))
    analyze_mal.append(('MLP-Malicious', 'MLP', mlp_cls_mal(), -1))
    analyze_mal.append(('KNN-Malicious', 'KNN', knn_cls_mal(), -1))
    analyze_mal.append(('SVM-Malicious', 'SVM', svm_cls_mal() ,8000))
    analyze_mal.append(('AdaBoost-Malicious', 'Boost', boost_csl_mal(), 8000))

    df = pd.DataFrame(columns=["classifier", "recall score", "train size", "test size","train time", "parameters"])

    i=0
    for r_tup in analyze_mal:
        label, classifer_name, (estimator, params ), cnt = r_tup
        _X, _y = Xtrain, ytrain
        if cnt > 0:
            _X, _, _y, _ = train_test_split( Xtrain, ytrain, train_size=cnt, stratify=ytrain,random_state=0)
        tic = time.perf_counter()
        estimator.fit(_X,_y)
        toc = time.perf_counter()
        fit_time = f"{toc-tic:0.4f}s"
        recall_score, _ = stats_pac(label, estimator, Xtest,ytest)
        df.loc[i] = [classifer_name, recall_score, len(_y), len(ytest), fit_time, params]
        i = i+1
    print( df.sort_values(by=['recall score'],ascending=False) )
    return df

def fit_analyze_job():

    Xtrain, ytrain =  load_jobchange()
    Xtest, ytest =  load_jobchange_test_untouched()

    analyze_mal =[]
    analyze_mal.append(('DT-Jobchange', 'Decision Tree', dt_cls_job(), -1))
    analyze_mal.append(('MLP-Jobchange', 'MLP', mlp_cls_job(), -1))
    analyze_mal.append(('KNN-Jobchnage', 'KNN', knn_cls_job(), -1))
    analyze_mal.append(('SVM-Jobchange', 'SVM', svm_cls_job() ,-1))
    analyze_mal.append(('AdaBoost-Jobchange', 'Boost', boost_csl_job(), -1))

    df = pd.DataFrame(columns=["classifier", "F1 score", "train size", "test size","train time", "parameters"])

    i=0
    for r_tup in analyze_mal:
        label, classifer_name, (estimator, params ), cnt = r_tup
        _X, _y = Xtrain, ytrain
        if cnt > 0:
            _X, _, _y, _ = train_test_split( Xtrain, ytrain, train_size=cnt, stratify=ytrain,random_state=0)
        tic = time.perf_counter()
        estimator.fit(_X,_y)
        toc = time.perf_counter()
        fit_time = f"{toc-tic:0.4f}s"
        _, f1_score = stats_pac(label, estimator, Xtest,ytest)
        df.loc[i] = [classifer_name, f1_score, len(_y), len(ytest), fit_time, params]
        i = i+1
    print( df.sort_values(by=['F1 score'],ascending=False) )
    return df

def stats_pac(label, estimator, X_data, y_data ):
    predicted = estimator.predict(X_data)
    print ('****** %s ******' % label)
    cm = confusion_matrix(y_data, predicted)
    print ('Confusion matrix')
    print_cm(cm, labels=['0','1'])
    
    recall_out =recall_score(y_data, predicted) 
    print ('Recall - %0.4f'% recall_out)
    print ('Recall - label 0: %0.4f'% recall_score(y_data, predicted, pos_label=0))
    print ('Recall - label 1: %0.4f'% recall_score(y_data, predicted))

    print ('Precsion - label 0: %0.4f'% precision_score(y_data, predicted, pos_label=0))
    print ('Precsion - label 1: %0.4f'% precision_score(y_data, predicted))
    
    f1_out =  f1_score(y_data, predicted)
    print ('F1 Score : %0.4f'%f1_out)
    print ('F1 Score - label 0 : %0.4f'% f1_score(y_data, predicted, pos_label=0 ))
    print ('F1 Score - label 1 : %0.4f'% f1_score(y_data, predicted, pos_label=1 ))
    print ('F1 Score - weighted avg : %0.4f'% f1_score(y_data, predicted, average='weighted' ))
    print ('F1 Beta - b=2 weighted avg : %0.4f'% fbeta_score(y_data,predicted, average='weighted', beta=2))
    
    print (classification_report(y_data, predicted, labels=[1,0]))
    print ()
    return recall_out, f1_out

if __name__ == "__main__":  		  	   		   	 			  		 			     			  	  		 	  	 		 			  		  			
    np.random.seed(5)
    config = DConfig(headless=True, save=True,dry_run=False)
    run_dt(config)
    run_mlp(config=config)
    run_knn(config=config)
    run_svm(config)
    run_boost(config)

    final_run = True
    if final_run:
        df_mal = fit_analyze_mal()
        df_job = fit_analyze_job()
        # df.sort_values(by=['col1'])
        save_df_csv(df_mal.sort_values(by=['recall score'],ascending=False), 'analysis_malicious')
        save_df_csv(df_job.sort_values(by=['F1 score'],ascending=False), 'analysis_jobchnage')