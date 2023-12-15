### To Evaluate Performance of Eclipse
1. ```git clone https://github.com/eclipse-jdt/eclipse.jdt.ui```
2. Import the eclipse.jdt.ui repository and this repository both into the same workspace of Eclipse.
3. Roll back to specific commit version using:
   ```git reset --hard 1b699b2df9a680df80ddd88f5193bd55d89e244d```
   as four pull requests about improving the recommended names of Extract Local Variable Refactoring have been merged into the master branch of this repository. This commit is the very one commit before the first pull request is merged.
4. Configure the data file path.
5. Run EvalEclipse/src/safeextractor/handlers/SampleHandler.java as an Eclipse application.
6. Click "Replicate" on the top menu, and then click the sub-menu "Extract Variables".


### To Evaluate Performance of IDEA
1. Configure the Intellij plugin development environment (the project EvalIdea is based on Gradle Plugin).
2. Configure the data file path.
3. Run Plugin and a new IDEA application will pop.
4. Click "File" on the top menu, and then click the sub-menu "MyAction".

