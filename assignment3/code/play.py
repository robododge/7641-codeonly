import numpy as np

import timeit
import time

if __name__ == "__main__":  		  	   		   	 			  		 			     			  	  		 	  	 		 			  		  			
    np.random.seed(9191882)

    start_time = timeit.default_timer()
    time.sleep(1)
    end_time = timeit.default_timer()
    print(f" total time {end_time - start_time}")