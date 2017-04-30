# LinKernighanTSP
A java implementation of the -famous- Lin-Kernighan heuristics algorithm implemented for graphic (symmetric) TSP.

## Characteristics
It implements exactly the same features described by Shen Lin and Brian Kernighan in their original paper "An Effective Heuristic Algorithm for the Traveling-Salesman Problem"

In addition, this implementation reads *.tsp* files to form a representation of the TSP problem; however, the format of the *.tsp* file is not yet TSPLIB compliant, but it would be a nice and easy issue to fix.

## How to contribute
We want to keep contributions as simple as posible, so the rules are simple: you contribute changes, I review them, if I agree with the changes I will pull it down.

That being said, there are a few recommendations for the quality of code to contribute:
* Efficiency: make the code as efficient as possible, efficiency is one of the key aspects of this kind of algorithms.
* Readability: document all the new functions by adding comments, and if there are tricky pieces of code, comment them as well.
* Reliability: ensure the code works!

Finally, review the TODO list in this page.

## How to run
Currently, the code is ran by compiling all the files and running the Main class.

## Resources

- Original Paper by S. Lin and B. Kernighan
Lin, Shen; Kernighan, B. W. (1973). "An Effective Heuristic Algorithm for the Traveling-Salesman Problem". *Operations Research*. **21** (2): 498–516. doi:[10.1287/opre.21.2.498](https://eng.ucmerced.edu/people/yzhang/papers/Heuristic/Lin_Kernighan).
- Waterloo TSP test data
[link](http://www.math.uwaterloo.ca/tsp/data/)

**NOTE:** we are currently using a different format for our test data.
