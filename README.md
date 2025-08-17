# AntlerSolver
 Uses antler structures to solve the FVS problem on graphs

## Author
 - Maarten Dankers

## How to build
Build with `mvn clean install`

## How to run
Run with `java -jar -Xss2048m .\target\AntlerSolver-1.0-SNAPSHOT.jar <graphFile/graphDir> <outputDir> <solve/kernel/solve_and_kernel>`
 - `<graphFile/graphDir>` can be any .graph file or directory containing .graph files. Each .graph file consists of lines which are either comments (starting with # or %), or 2 space seperated numbers x y representing an edge between node x and node y. This field can be replaced with `!random=graphDir` for the algorithm to create random graphs that contain specific antler structures and output these graphs as graph files in graphDir
 - `<outputDir>` is the directory where files for the minimum FVS per .graph file will be stored.
 - `<solve/kernel/solve_and_kernel>` is the option to choose wether to use the solver or kernalization or both on the graphs.

## References
 This code is build upon the code from Iwata given in the following 2 papers
 - Yoichi Iwata, Magnus Wahlström, Yuichi Yoshida: Half-integrality, LP-branching, and FPT Algorithms. SIAM J. Comput. 45(4): 1377-1411 (2016)
 - Yoichi Iwata: Linear-Time Kernelization for Feedback Vertex Set. ICALP 2017: 68:1-68:14

 The original code of Iwata can be found at https://github.com/wata-orz/fvs
