# Assignment 1: Supervised Learning

## Why?
The purpose of this project is to explore some techniques in supervised learning. It is important to realize that understanding an algorithm or technique requires understanding how it behaves under a variety of circumstances. As such, you will be asked to "implement" some simple learning algorithms (for sufficiently small values of implement, meaning I don't really want you to implement anything at all), and to compare their performance.

## The Means
In this assignment you will go through the process of exploring your chosen datasets, tuning the algorithms you learned about, and writing a thorough analysis of your findings. All that matters is the analysis. It doesn't matter if you implement any learning algorithms yourself (you probably shouldn't) as long as you participate in this journey of exploring, tuning, and analyzing. Concretely, this means you may program in any language that you wish, and you are allowed to use any library you wish as long as it was not written specifically to solve this assignment, and as long as a TA can recreate your experiments on a standard linux machine if necessary (we know how to install a pip package). You might want to look at scikit-learn or Weka, for example. 

Some examples of acceptable libraries:

- Machine learning algorithms: scikit-learn (python), Weka (java), e1071/nnet/random forest(R), ML toolbox (matlab), tensorflow/pytorch (python)
- Scientific computing: numpy/scipy(python), Matlab, R
- Plotting: matplotlib (python), seaborn (python), Matlab, R

You can use other libraries as long as they fulfill the conditions above. If you are unsure, ask a TA (but please use common sense first)! There is no trick here for you to overthink. Again, the key issue is that I don't care that you implement any of the learning algorithms below; however, I care very much about your analysis. 

## The Problems Given to You
You should implement five learning algorithms. They are:

- Decision trees with some form of pruning
- Neural networks
- Boosting
- Support Vector Machines
- k-nearest neighbors

Each algorithm is described in detail in your textbook, the handouts, and all over the web. In fact, instead of implementing the algorithms yourself, you may (and by may I mean should) use software packages that you find elsewhere; however, if you do so you should provide proper attribution. Also, you will note that you have to do some fiddling to get good results, graphs and such, so even if you use another's package, you may need to be able to modify it in various ways.

**Decision Trees**. For the decision tree, you should implement or steal a decision tree algorithm (and by "implement or steal" I mean "steal"). Be sure to use some form of pruning. You are not required to use information gain (for example, there is something called the GINI index that is sometimes used) to split attributes, but you should describe whatever it is that you do use.

**Neural Networks**. For the neural network you should implement or steal your favorite kind of network and training algorithm. You may use networks of nodes with as many layers as you like and any activation function you see fit.

**Boosting**. Implement or steal a boosted version of your decision trees. As before, you will want to use some form of pruning, but presumably because you're using boosting you can afford to be much more aggressive about your pruning.

**Support Vector Machines**. You should implement (for sufficiently loose definitions of implement including "download") SVMs. This should be done in such a way that you can swap out kernel functions. I'd like to see at least two.

**k-Nearest Neighbors**. You should "implement" (the quotes mean I don't mean it: steal the code) kNN. Use different values of k.

**Testing**. In addition to implementing (wink) the algorithms described above, you should design two interesting classification problems. For the purposes of this assignment, a classification problem is just a set of training examples and a set of test examples. I don't care where you get the data. You can download some, take some from your own research, or make some up on your own. Be careful about the data you choose, though. You'll have to explain why they are interesting, use them in later assignments, and come to really care about them.
