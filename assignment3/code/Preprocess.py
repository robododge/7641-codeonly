import numpy as np

from helper import init_preprocessed, sanity_test_data

if __name__ == "__main__":  		  	   		   	 			  		 			     			  	  		 	  	 		 			  		  			
    np.random.seed(5)

    print("Initializing the preprocessed data now, saving to disk")
    init_preprocessed()
    sanity_test_data()

    