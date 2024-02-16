import numpy as np
import pandas as pd
import os
from sklearn.decomposition import PCA, FastICA
from sklearn.neural_network import MLPClassifier

from helper import load_malicious, load_malicious_test_untouched, DConfig
from sklearn.preprocessing import  MinMaxScaler

from LearningCurve import LearningCurve

RND_STATE=998756

def max_kurtosis_indexes(X_projected, cutoff):
    tmp = pd.DataFrame(X_projected)
    tmp = tmp.kurt(axis=0)
    kurt = tmp.sort_values(ascending=False)
        
    max_k = kurt[0:cutoff]
    print(max_k)
    print(max_k.index)
    return max_k.index

def ica_max_kurtosis(X, cutoff, dim=0):

    if dim == 0:
        dim = X.shape[1]-1
    elif dim < cutoff:
        dim = cutoff

    ica = FastICA(random_state=89143)
    ica.set_params(n_components=dim)
    X_projected = ica.fit_transform(X)

    max_k_idexes = max_kurtosis_indexes(X_projected,cutoff)
    
    X_proj_df = pd.DataFrame(X_projected)
    X_proj_df  = X_proj_df.filter( max_k_idexes, axis=1 )
        
    return X_proj_df


if __name__ == "__main__":  		  	   		   	 			  		 			     			  	  		 	  	 		 			  		  			
    np.random.seed(5)
    os.makedirs('output', exist_ok=True) 
    mal_data = load_malicious()

    _X_mal,y_mal = mal_data


    minMaxNorm = MinMaxScaler()
    X_mal = minMaxNorm.fit_transform(_X_mal)

    _X_test_mal, y_test_mal = load_malicious_test_untouched()
    X_test_mal = minMaxNorm.fit_transform(_X_test_mal)

    X_ica_proj_mal = ica_max_kurtosis(X_mal ,3, dim=12)

    mlp_ICA_target = MLPClassifier(hidden_layer_sizes=(300,10), max_iter=2000, random_state=RND_STATE)
    mlp_RAW = MLPClassifier(hidden_layer_sizes=(199,), max_iter=2000, random_state=RND_STATE)

    dc_save = DConfig(save=True)

    LC_raw = LearningCurve(mlp_RAW, X_mal, y_mal, title='Dataset: Malicious Hacks, Raw Attributes', dataset_id=1)
    # LC_raw.generate_and_save()
    LC_raw.load_and_plot(dconfig=dc_save)

    LC = LearningCurve(mlp_ICA_target, X_ica_proj_mal, y_mal, title='Dataset: Malicious Hacks, ICA Projections', dataset_id=2)
    # LC.generate_and_save()
    LC.load_and_plot(dconfig=dc_save)



