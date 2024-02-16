import numpy as np
import pandas as pd
import pathlib
import math

from sklearn.metrics import confusion_matrix
from sklearn.preprocessing import LabelEncoder, MinMaxScaler
from sklearn.model_selection import train_test_split


job_train_pkl = 'data/job_train.pkl'
job_test_pkl = 'data/job_test.pkl'
mal_train_pkl = 'data/mal_train.pkl'
mal_test_pkl = 'data/mal_test.pkl'

TEST_PERCENT=0.20
RAW_SIZE_JOB=19158
RAW_SIZE_MAL=23856
TEST_SIZE_JOB = math.ceil(RAW_SIZE_JOB * TEST_PERCENT)
TEST_SIZE_MAL = math.ceil(RAW_SIZE_MAL* TEST_PERCENT)
TRAIN_SIZE_JOB = RAW_SIZE_JOB - TEST_SIZE_JOB
TRAIN_SIZE_MAL = RAW_SIZE_MAL - TEST_SIZE_MAL

def init_preprocessed():
    """
    Initialize a train test split, and save the two 
    into a re-loaded file on disk.  All classifer code
    will then pull these pre-preproccessed dataset
    """
    Xall,yall = load_jobchange_raw()

    # print("yAll jobchange: ")
    # print(yall)

    X_train, X_test, y_train, y_test = train_test_split( Xall, yall, test_size=TEST_PERCENT, stratify=yall,random_state=0)
    job_train_df, job_test_df = pd.DataFrame(X_train), pd.DataFrame(X_test)
    job_train_df['y'] = y_train
    job_test_df['y'] = y_test
    print(f"Saving Job chnage dataset now, Rows Columns - train:{job_train_df.shape}, test:{job_test_df.shape}")
    # print(job_train_df.head())
    job_train_df.to_pickle(job_train_pkl)
    job_test_df.to_pickle(job_test_pkl)

    Xmal,ymal = load_malicious_raw()
    X_train, X_test, y_train, y_test = train_test_split( Xmal, ymal, test_size=TEST_PERCENT, stratify=ymal,random_state=0)
    mal_train_df, mal_test_df = pd.DataFrame(X_train), pd.DataFrame(X_test)
    mal_train_df['y'] = y_train
    mal_test_df['y'] = y_test
    print(f"Saving Malicious Hack dataset now, Rows Columns - train:{mal_train_df.shape}, test:{mal_test_df.shape}")
    mal_train_df.to_pickle(mal_train_pkl)
    mal_test_df.to_pickle(mal_test_pkl)

    

    print("Saving copy of pickle files to Juyper notebook use")
    save_pkls_to_notebooks(mal_train_df, mal_test_df, job_train_df, job_test_df)

def save_assignment2_data():
    Xmal,ymal = load_malicious_raw()
    X_train, X_test, y_train, y_test = train_test_split( Xmal, ymal, test_size=0.30, stratify=ymal,random_state=0)
    # X_val, X_test, y_val, y_test = train_test_split( X_start, y_start, test_size=0.50, stratify=y_start,random_state=10983)

    mal_train_df,  mal_test_df = pd.DataFrame(X_train), pd.DataFrame(X_test)
    mal_train_df['y'] = y_train
    #mal_val_df['y'] = y_val
    mal_test_df['y'] = y_test
    print(f"Saving Malicious Hack for assignment2 now, Rows Columns - train:{mal_train_df.shape}, test:{mal_test_df.shape}")
    save_df_csv(mal_train_df, 'a2_malicious_train')
    # save_df_csv(mal_val_df, 'a2_malicious_validate')
    save_df_csv(mal_test_df, 'a2_malicious_test')


def save_pkls_to_notebooks(m_train_df, m_test_df, j_train_df, j_test_df):
    m_train_df.to_pickle('../notebooks/data/mal_train.pkl')
    m_test_df.to_pickle('../notebooks/data/mal_test.pkl')
    j_train_df.to_pickle('../notebooks/data/job_train.pkl')
    j_test_df.to_pickle('../notebooks/data/job_test.pkl')
    

def sanity_test_data():
    print("Sanity test load job change")
    X, y = load_jobchange()
    assert X.shape ==(TRAIN_SIZE_JOB, 12)
    assert y.shape ==(TRAIN_SIZE_JOB, )

    print ('y type:',type(y))
    print(y)

    x_nan = np.where(np.isnan(X))
    y_nan = np.where(np.isnan(y))
    xn_r, xn_c = x_nan
    assert xn_r.shape == (0,) and xn_c.shape == (0,)
    assert y_nan[0].shape == (0,) 
    print(" ... job change train data is good!")

    X, y = load_jobchange_test_untouched()
    assert X.shape ==(TEST_SIZE_JOB, 12)
    assert y.shape ==(TEST_SIZE_JOB, )
    print(" ... job change test data is good!")
    
    print("Sanity test load malicious hack")
    X,y = load_malicious()
    assert X.shape ==(TRAIN_SIZE_MAL, 15)
    assert y.shape ==(TRAIN_SIZE_MAL, )
    print(" ... malicious hack train data is good!")
    X,y = load_malicious_test_untouched()
    assert X.shape ==(TEST_SIZE_MAL, 15)
    assert y.shape ==(TEST_SIZE_MAL, )
    print(" ... malicious hack test data is good!")

def load_jobchange():
    unpickled_job_train_df = pd.read_pickle(job_train_pkl)
    assert unpickled_job_train_df.shape == (TRAIN_SIZE_JOB, 13) , f"ouch expected job size: {TRAIN_SIZE_JOB} actual:{unpickled_job_train_df.shape}"
    X = unpickled_job_train_df.drop(columns=['y']).values
    y = unpickled_job_train_df['y'].values
    assert X.shape ==(TRAIN_SIZE_JOB, 12)
    assert y.shape ==(TRAIN_SIZE_JOB, )
    return X, y

def load_jobchange_test_untouched():
    unpickled_job_test_df = pd.read_pickle(job_test_pkl)
    assert unpickled_job_test_df.shape == (TEST_SIZE_JOB, 13)
    X = unpickled_job_test_df.drop(columns=['y']).values
    y = unpickled_job_test_df['y'].values
    assert X.shape ==(TEST_SIZE_JOB, 12)
    assert y.shape ==(TEST_SIZE_JOB, )
    return X, y
    


def load_malicious():
    unpickled_mal_train_df = pd.read_pickle(mal_train_pkl)
    assert unpickled_mal_train_df.shape == (TRAIN_SIZE_MAL, 16)
    X = unpickled_mal_train_df.drop(columns=['y']).values
    y = unpickled_mal_train_df['y'].values
    assert X.shape ==(TRAIN_SIZE_MAL, 15)
    assert y.shape ==(TRAIN_SIZE_MAL, )
    return X, y

def load_malicious_test_untouched():
    unpickled_mal_test_df = pd.read_pickle(mal_test_pkl)
    assert unpickled_mal_test_df.shape == (TEST_SIZE_MAL, 16)
    X = unpickled_mal_test_df.drop(columns=['y']).values
    y = unpickled_mal_test_df['y'].values
    assert X.shape ==(TEST_SIZE_MAL, 15)
    assert y.shape ==(TEST_SIZE_MAL, )
    return X, y

def load_jobchange_raw():
    df = pd.read_csv("data/job_change_data_scientist.csv")
    df_enc_0 = df.apply(LabelEncoder().fit_transform)
    df_enc_drop = df_enc_0.drop(columns=["enrollee_id","target"])
    # minMaxNorm = MinMaxScaler()
    # df_norm = minMaxNorm.fit_transform(df_enc_drop)

    # X = df_norm
    X = df_enc_drop.values
    y = df_enc_0["target"].values.astype("int32")
    r,c = X.shape
    print(f"Pre processed job change data, rows {r}, columns {c}")
    return X, y


def load_malicious_raw():
    df = pd.read_csv("data/malicious_01.csv")
    Xdf = df.drop(columns=["INCIDENT_ID","DATE", "MALICIOUS_OFFENSE"])
    y_mal = df["MALICIOUS_OFFENSE"]
    Xdf['X_12'] = np.nan_to_num(Xdf['X_12'])
    _X, _y = Xdf.values, y_mal.values

    # minMaxNorm = MinMaxScaler()
    # X = minMaxNorm.fit_transform(_X)

    # labelEncoder = LabelEncoder()
    # y = labelEncoder.fit_transform(_y)
    # y_pre = labelEncoder.fit_transform(_y)
    # y = 1 - y_pre

    X = _X
    y = 1 - _y.astype('int32')
    r,c = X.shape
    ry = y.shape
    print(f"Pre processed malicious change data, rows {r}, columns {c}, y rows:{ry}")
    return X, y


def print_cm(cm, labels, hide_zeroes=False, hide_diagonal=False, hide_threshold=None):
    """pretty print for confusion matrixes"""
    columnwidth = max([len(x) for x in labels] + [5])  # 5 is value length
    empty_cell = " " * columnwidth
    # Print header
    print("    " + empty_cell, end=" ")
    for label in labels:
        print("%{0}s".format(columnwidth) % label, end=" ")
    print()
    # Print rows
    for i, label1 in enumerate(labels):
        print("    %{0}s".format(columnwidth) % label1, end=" ")
        for j in range(len(labels)):
            cell = "%{0}d".format(columnwidth) % cm[i, j]
            if hide_zeroes:
                cell = cell if float(cm[i, j]) != 0 else empty_cell
            if hide_diagonal:
                cell = cell if i != j else empty_cell
            if hide_threshold:
                cell = cell if cm[i, j] > hide_threshold else empty_cell
            print(cell, end=" ")
        print()

def save_df_csv(df, name):
    print(".?.? Saving csv", name)
    file_str = "%s.csv" % name
    save_path = pathlib.Path.cwd() / 'output' / file_str
    df.to_csv(save_path)

class DConfig(object):
    def __init__(self, headless=False, save=False, dry_run=False):
        self.headless = headless
        self.save = save
        self.dry_run = dry_run
