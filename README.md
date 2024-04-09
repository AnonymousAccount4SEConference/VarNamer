# This is the online repository of manuscript "Recommending Variable Names for Extract Local Variable Refactorings".

## What is VarNamer?
**VarNamer** is an automated approach to recommending names for the **extract local variable** refactorings by fully leveraging their contexts.

## What is included by this replication package?
*/Baselines:* The implementation of three selected baselines and a README to reproduce them. 
- */EvalEclipse*, the implementation of extract local variable refactorings of *Eclipse*.
- */EvalIdea*, the implementation of extract local variable refactorings of *IDEA*.
- */Incoder*, the code for LLM *Incoder*.
- */BaseLines/Incoder/data/IncoderInputs.zip* provides the inputs of *Incoder*.

*/Code:* The implementation of **VarNamer**, including the leveraged libraries.  

*/Data:* Two curated datasets comprising real-world extract local variable refactorings.
- */JavaDataset*, containing 32,039 instances from Java programs where 27,158 used for evaluation (*TestingDataSet*) and 4,881 used for empirical study (*EmpiricalDataSet*).
- */C++Dataset*, containing 50 instances from C++ programs. 
- The involved repositories, metadata, e.g., Java file path, line number, of each refactoring are also included. 

*/Results:* The evaluation results presented in our research questions.
- */RecommendedNames*, the variable names recommended by **VarNamer** and the selected baselines.
    - */Java*, the recommended names in *TestingDataSet*.
    - */C++*, the recommended names in *C++Dataset*
- */UserStudy*, the experiment results of the conducted user study.  
- */MiningResults*, the mining results discovered by the FP-growth algorithm.

## How to reproduce the performance of VarNamer?
1. Clone this repository 

    `git clone https://github.com/AnonymousAccount4SEConference/VarNamer`.

2. Decompress the dataset and configure the data path.

3. Import the project into IDE, or construct a new maven project.

4. Make sure that the libraries (jar files) in */Code/VarNamer/lib/* are included by the project.

5. Run */Code/VarNamer/src/main/java/VarNamer.java*
