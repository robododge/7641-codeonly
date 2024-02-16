import numpy as np
import pandas as pd

job_train_pkl = 'data/job_train.pkl'
job_test_pkl = 'data/job_test.pkl'
mal_train_pkl = 'data/mal_train.pkl'
mal_test_pkl = 'data/mal_test.pkl'

def load_jobchange():
    unpickled_job_train_df = pd.read_pickle(job_train_pkl)
    X = unpickled_job_train_df.drop(columns=['y']).values
    y = unpickled_job_train_df['y'].values
    return X, y

def load_jobchange_test_untouched():
    unpickled_job_test_df = pd.read_pickle(job_test_pkl)
    X = unpickled_job_test_df.drop(columns=['y']).values
    y = unpickled_job_test_df['y'].values
    return X, y



def load_malicious():
    unpickled_mal_train_df = pd.read_pickle(mal_train_pkl)
    X = unpickled_mal_train_df.drop(columns=['y']).values
    y = unpickled_mal_train_df['y'].values
    return X, y

def load_malicious_test_untouched():
    unpickled_mal_test_df = pd.read_pickle(mal_test_pkl)
    X = unpickled_mal_test_df.drop(columns=['y']).values
    y = unpickled_mal_test_df['y'].values
    return X, y

def load_jobchange_w_headers():
    unpickled_job_train_df = pd.read_pickle(job_train_pkl)
    X = unpickled_job_train_df.drop(columns=['y'])
    y = unpickled_job_train_df['y']
    return X, y


def load_malicious_w_headers():
    unpickled_mal_train_df = pd.read_pickle(mal_train_pkl)
    X = unpickled_mal_train_df.drop(columns=['y'])
    y = unpickled_mal_train_df['y']
    return X, y