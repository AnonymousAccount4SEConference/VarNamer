# This is the online repository of manuscript "Recommending Variable Names for Extract Local Variable Refactorings".

## What is VarNamer?
**VarNamer** is an automated approach to recommending names for the **extract local variable** refactorings by fully leveraging their contexts.

## What is included by this replication package?
*/Baselines:* The implementation of three selected baselines and a README to reproduce them. In addition, **Incoder** requires an input with all occurrences of variable names replaced with a sentinel. This dataset can be found in */BaseLines/Incoder/data/IncoderInputs.zip*

*/Code:* The implementation of **VarNamer**, including the leveraged libraries.  

*/Data:* The involved repositories, metadata of each refactoring, and the related methods and java files (before and after the refactoring). 

*/Results:* The variable names recommended by **VarNamer** and three selected baselines on both *TestingDataSet* and *CommonDataSet*. The mining results are also included by this directory.

## How to reproduce the performance of VarNamer?
1. Clone this repository 

    `git clone https://github.com/AnonymousAccount4SEConference/VarNamer`.

2. Decompress the dataset and configure the data path.

3. Import the project into IDE, or construct a new maven project.

4. Make sure that the libraries (jar files) in */Code/VarNamer/lib/* are included by the project.

5. Run */Code/VarNamer/src/main/java/VarNamer.java*
