import numpy as np
import pandas as pd

job_train_pkl = 'data/job_train.pkl'
job_test_pkl = 'data/job_test.pkl'
mal_train_pkl = 'data/mal_train.pkl'
mal_test_pkl = 'data/mal_test.pkl'

def load_jobchange():
    unpickled_job_train_df = pd.read_pickle(job_train_pkl)
    assert unpickled_job_train_df.shape == (14368, 13)
    X = unpickled_job_train_df.drop(columns=['y']).values
    y = unpickled_job_train_df['y'].values
    assert X.shape ==(14368, 12)
    assert y.shape ==(14368, )
    return X, y

def load_jobchange_test_untouched():
    unpickled_job_test_df = pd.read_pickle(job_test_pkl)
    assert unpickled_job_test_df.shape == (4790, 13)
    X = unpickled_job_test_df.drop(columns=['y']).values
    y = unpickled_job_test_df['y'].values
    assert X.shape ==(4790, 12)
    assert y.shape ==(4790, )
    return X, y



def load_malicious():
    unpickled_mal_train_df = pd.read_pickle(mal_train_pkl)
    assert unpickled_mal_train_df.shape == (17892, 16)
    X = unpickled_mal_train_df.drop(columns=['y']).values
    y = unpickled_mal_train_df['y'].values
    assert X.shape ==(17892, 15)
    assert y.shape ==(17892, )
    return X, y

def load_malicious_test_untouched():
    unpickled_mal_test_df = pd.read_pickle(mal_test_pkl)
    assert unpickled_mal_test_df.shape == (5964, 16)
    X = unpickled_mal_test_df.drop(columns=['y']).values
    y = unpickled_mal_test_df['y'].values
    assert X.shape ==(5964, 15)
    assert y.shape ==(5964, )
    return X, y
