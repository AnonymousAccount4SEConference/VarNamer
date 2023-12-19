
import edu.lu.uni.serval.utils.FileHelper;
import org.eclipse.jdt.core.dom.*;
import utils.*;
import visitors.*;

import java.io.*;
import java.util.*;

/*
with type
 */
public class VarNamer {
//        static String runType = "EmpiricalStudy";
    static String runType= "Test";
    static int reuse_single_recommended = 0;
    static int reuse_single_correct = 0;
    static int reuse_multiplyEqual_recommended = 0;
    static int reuse_multiplyEqual_correct = 0;
    static int reuse_multiplyNotEqual_recommended = 0;
    static int reuse_multiplyNotEqual_correct = 0;
    static int reuse_correct = 0;
    static int reuse_eclipse_correct = 0;
    static int reuse_eclipse_recommended = 0;
    static int reuse_recommended = 0;
    static int all_records = 0;
    static Boolean ifReused = false;
    static Boolean ifReuse = true;
    static Boolean ifRecommended=false;
    static int stableHVNNum = 0;
    static int heuristicRuleExpression_recommended = 0;
    static int heuristicRuleExpression_correct = 0;
    static int all_number_homogeneous_variable = 0;
    static int all_number_files_in_projects = 0;
    static long total_run_time = 0;
    public static final String [] KEYWORDS = {
            "abstract","assert","boolean","break","byte","case","catch","char","class","continue","default","do","double","else"
            ,"enum","extends","final","finally","float","for","if","implements","import","int","interface","instanceof",
            "long","native","new","package","private","protected","public","return","short","static","strictfp","super",
            "switch","synchronized","this","throw","throws","transient","try","void","volatile","while","goto","const",
            "true","false","null","non_sealed"
    };
    public static final String[] ALL_KNOWN_METHOD_NAME_PREFIXES = {"get", "is", "to", "new", "create", "load", "find", "list", "next",
            "build", "generate", "prepare", "parse", "current", "read", "resolve", "retrieve", "make", "add", "as", "extract",
            "compute","open","lookup","calculate","determine","construct","fetch"}; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$
    public static void main(String[] args) throws IOException {
//        log2File();
        String testProjectBasePath = "need to be configured";
        String allProjectSortedBasePath = testProjectBasePath+"Records4EachProject/";
        String allRecordsPath = testProjectBasePath + "Records.txt";
        String afterRefactoringMethodPath = testProjectBasePath + "ValidRelatedMethods_AfterCommit_Java/";
        String beforeRefactoringMethodPath = testProjectBasePath + "ValidRelatedMethods_BeforeCommit_Java/";
        String beforeRefactoringJavaPath = testProjectBasePath + "ValidRelatedJavaFiles_BeforeCommit/";
        String allProjectNamesFile = testProjectBasePath + "ValidRepoNames.txt";
        String empiricalStudyIndexFile = testProjectBasePath + "EmpiricalIndex.txt";
        ArrayList<String> allProjectNames = FileHelper.readFileByLines(allProjectNamesFile);
        ArrayList<String> empiricalIndex = FileHelper.readFileByLines(empiricalStudyIndexFile);
        ArrayList<String> allRecords = FileHelper.readFileByLines(allRecordsPath);
        int [] projectNums= {80};
        double [] initializationComplexities= {30};
        double [] fineGrainedContextSims= {0.3};
        for(double fineGrainedContextSimThreshold:fineGrainedContextSims){
            for(double initializationComplexityThreshold:initializationComplexities){
                for(int universalInitializationProjectNumThreshold:projectNums){
                    clearAllRecordNums();
                    for (String projectName : allProjectNames) {
//                        System.out.println("projectName:" + projectName);
                        try {
                            recommend(testProjectBasePath,allRecords,empiricalIndex, allProjectSortedBasePath, beforeRefactoringJavaPath,
                                    beforeRefactoringMethodPath,afterRefactoringMethodPath,
                                    projectName,universalInitializationProjectNumThreshold,
                                    initializationComplexityThreshold,fineGrainedContextSimThreshold);
                        } catch (Exception e) {
                            e.printStackTrace();
//                            System.out.println("Wrong:" + projectName);
                        }
                    }
                    printPerformance("Reuse");
                    printPerformance("Generation");
                    printPerformance("Overall");
                }
            }
        }
        System.out.println("total run time:" + (total_run_time) / 60000 + "min");
        if (runType.equals("EmpiricalStudy"))
            System.out.println("total time" +total_run_time + "ms, average run time:" + (total_run_time)*1.0 / 4881 + "ms");
        else
            System.out.println("total time" +total_run_time + "ms, average run time:" + (total_run_time)*1.0 / 27158 + "ms");
    }

    private static void clearAllRecordNums() {
        reuse_single_recommended = 0;
        reuse_single_correct = 0;
        reuse_multiplyEqual_recommended = 0;
        reuse_multiplyEqual_correct = 0;
        reuse_multiplyNotEqual_recommended = 0;
        reuse_multiplyNotEqual_correct = 0;
        reuse_correct = 0;
        reuse_eclipse_correct = 0;
        reuse_eclipse_recommended = 0;
        reuse_recommended = 0;
        all_records = 0;
        ifReused = false;
        heuristicRuleExpression_recommended = 0;
        heuristicRuleExpression_correct = 0;
        all_number_homogeneous_variable = 0;
        all_number_files_in_projects = 0;
        total_run_time = 0;
    }

    private static void log2File() throws IOException {
        File f = new File("log_VarNamer.txt");
        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
    }

    public static void recommend(String testBasePath,ArrayList<String> allRecords,
                                 ArrayList<String> empiricalIndex, String basePath, String beforeRefactoringJavaPath,
                                 String beforeRefactoringMethodPath, String afterRefactoringMethodPath,  String projectName,
                                 int universalInitializationProjectNumThreshold,
                                 double initializationComplexityThreshold,
                                 double fineGrainedContextSimThreshold)
            throws IOException {
        String recordPath = basePath + projectName + ".txt";
        String universalInitializationsFile=testBasePath+ "MiningResults/UniversalInitializations/UniversalInitializations.txt";
        String universalInitializationsProjectNumFile=testBasePath+"MiningResults/UniversalInitializations/validProjectNum.txt";

        ArrayList<String> variableLines = FileHelper.readFileByLines(recordPath);
//        List<RevCommit> commits = openRepoAndGetAllCommits(projectPath, projectName);
        String currentCommitID = "";
        HashMap<String, ArrayList<String>> stashMap = new HashMap<>();
        HashMap<String, ArrayList<MethodDeclaration>> stashMDMap = new HashMap<>();
        StringBuilder expressionFailureCases=new StringBuilder();
        for (String variableLine : variableLines) {
            ifReuse=true;
            int index = allRecords.indexOf(variableLine);
            if(index == -1) continue;
            if (runType.equals("EmpiricalStudy")) {
                if (!empiricalIndex.contains(String.valueOf(index))) {
                    continue;
                }
            } else {
                if (empiricalIndex.contains(String.valueOf(index))) {
                    continue;
                }
            }
//            if (index != 2) {
//                continue;
//            }
//            System.out.println(variableLine);
            // resolve the value bag
            HashMap<String, String> valueMap = DataProcessUtil.resolveValueBag(variableLine);
            assert valueMap != null;
            String commitID = valueMap.get("commitID");
            String javaFilePath = valueMap.get("javaFilePath");
            String javaFileName = valueMap.get("javaFileName");
            String initializer = valueMap.get("initializer");
            String variableName = valueMap.get("variableName");
            String involvedExpression = valueMap.get("involvedExpression");
            if (!currentCommitID.equals(commitID)) {
                currentCommitID = commitID;
                stashMap.clear();
                stashMDMap.clear();
            }
            /*
            load method after refactoring
             */
            MethodDeclaration methodAfterRefactoring = loadMethodDeclaration(afterRefactoringMethodPath, index);
                        /*
            load method after refactoring
             */
            MethodDeclaration methodBeforeRefactoring = loadMethodDeclaration(beforeRefactoringMethodPath, index);
            /*
            load java after refactoring
             */
            String beforeJavaFilePath = beforeRefactoringJavaPath+index+".java";
            CompilationUnit enclosingCU= DataProcessUtil.getCu(beforeJavaFilePath);
            // record start time after switching to the specific commit.
            long startTime = System.currentTimeMillis();
//            System.out.println(index + "-startTime:" + startTime);

            if (methodBeforeRefactoring == null) {
//                System.out.println("methodBeforeRefactoring == null");
                continue;
            }
//            System.out.println("enclosingMethod:");
//            System.out.println(enclosingMethod);
            /*
            obtain the initializer in AST form
             */
            InitializerVisitor initializerVisitor= new InitializerVisitor(variableName,initializer);
            methodAfterRefactoring.accept(initializerVisitor);
            Expression initializerExpression= initializerVisitor.getInitializer();
            if(initializerExpression==null) {
                continue;
            }
            String astNodeType= initializerExpression.getClass().toString();
            InitializerParentVisitor initializerParentVisitor=new InitializerParentVisitor(initializerExpression,involvedExpression);
            ASTNode initializerParent= null;
            ASTNode initializerInPlace= null;
            try {
                methodBeforeRefactoring.accept(initializerParentVisitor);
            } catch (AbortSearchException e) {
                initializerParent= initializerParentVisitor.getInitializerParent();
                initializerInPlace= initializerParentVisitor.getInitializerInPlace();
            }
            if(initializerParent==null || initializerInPlace == null) {
                continue;
            }


/*
            obtain the body declaration enclosing the to-be-extracted expression.
             */
            ASTNode enclosingBody= obtainEnclosingBodyDeclaration(initializerInPlace);
            if(enclosingBody==null){
//                System.out.println("enclosing body null");
                continue;
            }
           /*
            obtain used local names.
             */
//            System.out.println(methodAfterRefactoring);
            List<SingleVariableDeclaration> parameters = methodBeforeRefactoring.parameters();
            List<String> parameterNames=new ArrayList<>();
            for(SingleVariableDeclaration singleVariableDeclaration: parameters){
                parameterNames.add(singleVariableDeclaration.getName().toString());
            }
//            System.out.println(parameterNames);
            ArrayList<String> usedLocalNames1 = new ArrayList<>(parameterNames);
            UsedLocalVariableNameVisitor localVariableNameInMethodVisitor = new UsedLocalVariableNameVisitor();
            enclosingBody.accept(localVariableNameInMethodVisitor);
            usedLocalNames1.addAll(localVariableNameInMethodVisitor.getSimilarName());
            ArrayList<String> usedLocalNames=new ArrayList<>();
            for(String s:usedLocalNames1){
                if(!s.equalsIgnoreCase(variableName)){
                    usedLocalNames.add(s);
                }
            }
            /*
            load universal initializations
             */
            List<String> universalInitializations = loadUniversalInitializations(universalInitializationsFile,universalInitializationsProjectNumFile,universalInitializationProjectNumThreshold);

            // add the stashed variable names in the same commit
            ArrayList<String> stashedNameList = stashMap.get(initializer + javaFilePath);
            ArrayList<MethodDeclaration> stashedMDList = stashMDMap.get(initializer + javaFilePath);
            /*
            univeral initialization filter
             */
            boolean isUniversalInitializers= isUniversalInitializers(universalInitializations, initializer);
            /*
            retrieve homogeneous variable
             */
            if(ifReuse){
                suggestNamesViaReuse(index, initializer, stashedNameList, stashedMDList,  valueMap, methodBeforeRefactoring,
                        enclosingCU, usedLocalNames,initializerExpression, initializerParent,
                        initializationComplexityThreshold,fineGrainedContextSimThreshold,isUniversalInitializers);
            }

            if (!ifReused) {
                String generated_name = null;
                generated_name = suggestNamesViaExpression(variableName, initializerExpression);
                if (generated_name != null && !isKeyWord(generated_name) && !isUsedLocalNames(usedLocalNames,generated_name)) {
                    heuristicRuleExpression_recommended++;
                    ifRecommended=true;
                    if(generated_name.replace("_","").equalsIgnoreCase(variableName)){
                        heuristicRuleExpression_correct++;
                    }
                    else{
                        expressionFailureCases.append(index+":"+variableName).append("=").append(initializer).append(":").append(generated_name).append("\n");
                    }
                }
            }
            ifReused = false;
            ifReuse =true;
            // stash the previous changes on the same commit
            stashThePreviousChanges(stashMap, stashMDMap, initializer, javaFilePath, variableName, methodAfterRefactoring);



            long endTime = System.currentTimeMillis();
//            System.out.println(index + "-endTime:" + endTime);
            long runtime = endTime - startTime;
//            System.out.println("run time:" + runtime);
            total_run_time += runtime;
            ifRecommended=false;

        }
    }
    public static boolean isUsedLocalNames(ArrayList<String> usedLocalNames, String name){
        return usedLocalNames.contains(name);
    }
    private static ASTNode obtainEnclosingBodyDeclaration(ASTNode node) {
        // expression must be in a method, lambda or initializer body
        // make sure it is not in method or parameter annotation
        StructuralPropertyDescriptor location= null;
        while (node != null && !(node instanceof BodyDeclaration)) {
            location= node.getLocationInParent();
            node= node.getParent();
            if (node instanceof LambdaExpression) {
                break;
            }
        }
        if (location == MethodDeclaration.BODY_PROPERTY || location == Initializer.BODY_PROPERTY||location == LambdaExpression.BODY_PROPERTY) {
            node= (ASTNode) node.getStructuralProperty(location);
            return node;
        }
        return null;
    }

    private static List<String> loadUniversalInitializations(String universalInitializationsFile,String projectNumFile, int threshold) throws FileNotFoundException {
        ArrayList<String> initializationList = FileHelper.readFileByLines(universalInitializationsFile);
        ArrayList<String> projectNumList = FileHelper.readFileByLines(projectNumFile);
        List<String> selectedList=new ArrayList<>();
        for(int i=0;i<projectNumList.size();i++){
            String projectNum=projectNumList.get(i);
            String initialization=initializationList.get(i);
            double projectNumber = Double.parseDouble(projectNum);
            if(projectNumber>threshold){
                selectedList.add(initialization);
            }
        }
        return selectedList;
    }
    private static boolean isUniversalInitializers(List<String> universalInitializations, String initializer) {
        for(String uniIni:universalInitializations){
            if(uniIni.replace(" ","").equalsIgnoreCase(initializer)){
                return true;
            }
        }
        return false;
    }


    private static boolean isKeyWord(String generatedName) {
        for(String keyword:KEYWORDS){
            if(generatedName.equalsIgnoreCase(keyword))
                return true;
        }
        return false;
    }

    private static MethodDeclaration loadMethodDeclaration(String methodFilePath,int index) {
        String methodPath= methodFilePath + index + ".java";
        return DataProcessUtil.getMethodDeclaration(methodPath);
    }


    private static void stashThePreviousChanges(HashMap<String, ArrayList<String>> stashMap, HashMap<String, ArrayList<MethodDeclaration>> stashMDMap, String initializer, String javaFilePath, String variableName, MethodDeclaration methodAfterRefactoring) {
        ArrayList<String> variableNamelist = new ArrayList<>();
        ArrayList<String> existList = stashMap.get(initializer + javaFilePath);
        if (existList == null) {
            variableNamelist.add(variableName);
            stashMap.put(initializer + javaFilePath, variableNamelist);
        } else {
            existList.add(variableName);
            stashMap.put(initializer + javaFilePath, existList);
        }

        ArrayList<MethodDeclaration> MDlist = new ArrayList<>();
        ArrayList<MethodDeclaration> existMDList = stashMDMap.get(initializer + javaFilePath);
        if (existMDList == null) {
            MDlist.add(methodAfterRefactoring);
            stashMDMap.put(initializer + javaFilePath, MDlist);
        } else {
            existMDList.add(methodAfterRefactoring);
            stashMDMap.put(initializer + javaFilePath, existMDList);
        }
    }

    private static HashMap<ArrayList<String>, ArrayList<MethodDeclaration>> searchInEnclosingJavaForNameAndMD(HomogeneousVariableVisitor homogeneousVariableVisitor, ArrayList<String> usedLocalNames) {
        HashMap<ArrayList<String>, ArrayList<MethodDeclaration>> resMap = new HashMap<>();
        ArrayList<String> similarNameAndType = homogeneousVariableVisitor.getSimilarNameAndType();
        ArrayList<MethodDeclaration> similarMDs = homogeneousVariableVisitor.getMethodDeclarations();
        ArrayList<String> homogeneousVariableNames = new ArrayList<>();
        ArrayList<MethodDeclaration> homogeneousVariableMDs = new ArrayList<>();
        if(similarNameAndType.size()==similarMDs.size()){
            removeUsedLocalNames(similarNameAndType,similarMDs,usedLocalNames, homogeneousVariableNames,homogeneousVariableMDs);
            resMap.put(homogeneousVariableNames, homogeneousVariableMDs);
            return resMap;
        }
        return null;
    }
    private static void removeUsedLocalNames(ArrayList<String> similarNameAndTypes, ArrayList<MethodDeclaration> methodDeclarations, ArrayList<String> usedLocalNames, ArrayList<String> homogeneousVariableNames, ArrayList<MethodDeclaration> homogeneousVariableMDs) {
        for (int i = 0; i < similarNameAndTypes.size(); i++) {
            String similarNameAndType = similarNameAndTypes.get(i);
            MethodDeclaration methodDeclaration = methodDeclarations.get(i);
            if (similarNameAndType.split("#").length == 2) {
                String type = similarNameAndType.substring(similarNameAndType.indexOf("#") + 1);
                String name = similarNameAndType.substring(0, similarNameAndType.indexOf("#"));
                if (!usedLocalNames.contains(name)) {
                    homogeneousVariableNames.add(name);
                    homogeneousVariableMDs.add(methodDeclaration);
                }
            }
        }
    }
    private static String suggestNamesViaExpression(String variableName, Expression initializerExpression) {
        if(initializerExpression==null) return null;
        String name = null;
        Expression assignmentExpression=initializerExpression;
        if(assignmentExpression instanceof CastExpression castExpression){
//            System.out.println("initializerExpression:"+ variableName+"="+ initializerExpression);
            assignmentExpression= castExpression.getExpression();
            if(assignmentExpression instanceof SimpleName){
                return castExpression.getType().toString();
            }
        }

        if(assignmentExpression instanceof MethodInvocation methodInvocation){
            String methodName = methodInvocation.getName().toString();
            String priorName = generateNameForMethodInvocation(variableName, methodInvocation);
            if(priorName!=null) return priorName;
            name = methodName;
        }
        else if(assignmentExpression instanceof ClassInstanceCreation classInstanceCreation){
            return classInstanceCreation.getType().toString();
        }
        else if (assignmentExpression instanceof FieldAccess fieldAccess) {
            return  fieldAccess.getName().getIdentifier();
        }
        else if (assignmentExpression instanceof SuperMethodInvocation superMethodInvocation) {
            name= superMethodInvocation.getName().toString();
        }
        else if(assignmentExpression instanceof ArrayAccess arrayAccess){
            Expression arrayExp= arrayAccess.getArray();
//            System.out.println(arrayExp.getClass());
            if (arrayExp instanceof SimpleName) {
//                System.out.println("arrayAccess:"+ variableName+"="+ arrayAccess);
                String array = arrayAccess.getArray().toString();
                String baseName = EnglishWordUtil.modifyBaseName(array);
                if (!baseName.equals("element")){
                    return baseName;
                }
            }
            return null;
        }
        else if(assignmentExpression instanceof QualifiedName qualifiedName){
            String qualifiedNameName = qualifiedName.getName().toString();
//            System.out.println("qualifiedName:"+ variableName+"="+ qualifiedNameName);
            return qualifiedNameName;
        }
        else{
            return null;
        }
        for (String curr : ALL_KNOWN_METHOD_NAME_PREFIXES) {
            if (name.startsWith(curr)) {
                if (name.equals(curr)) {
//                    System.out.println("cannot handled cases:"+ variableName + "="+assignmentExpression);
                    return null; // don't suggest 'get' as variable name
                } else if (Character.isUpperCase(name.charAt(curr.length()))) {
                    return name.substring(curr.length());
                }
            }
        }
        return name;
    }

    public static String generateNameForMethodInvocation(String variableName, MethodInvocation methodInvocation) {
        String receiver = null;
        Expression receiverExpression= methodInvocation.getExpression();
        if(methodInvocation.getExpression()!=null){
            receiver=methodInvocation.getExpression().toString();
        }
        if(receiver==null) return null;
        String methodName = methodInvocation.getName().toString();
        String generated_name = null;
        int argumentNum=methodInvocation.arguments().size();
        List<Expression> arguments= methodInvocation.arguments();
        // 1. if method name is next, and scope is plural, take the singular case as the variable name.
        if(methodName.equals("next")){
            if (receiverExpression instanceof SimpleName) {
                String baseName = EnglishWordUtil.modifyBaseName(receiver);
                if (!baseName.equals("element")){
                    return baseName;
                }
            }
        }
        return generated_name;
    }

    private static void suggestNamesViaReuse(int index, String initializer,ArrayList<String> stashedNameList,
                                             ArrayList<MethodDeclaration> stashedMDList,HashMap<String, String> valueMap,
                                             MethodDeclaration enclosingMethod, CompilationUnit enclosingCU,
                                             ArrayList<String> usedLocalNames,Expression initializerExpression,
                                             ASTNode initializerExpressionParent,
                                             double initializationComplexityThreshold,
                                             double fineGrainedContextSimThreshold,
                                             boolean isUniversalInitializers) {

        ArrayList<String> candidateHomogeneousVariableNames= new ArrayList<>();
        ArrayList<MethodDeclaration> candidateHomogeneousVariableMDs;
        candidateHomogeneousVariableMDs = new ArrayList<>();
        if (stashedNameList != null && stashedMDList!=null &&stashedNameList.size()==stashedMDList.size()) {
            candidateHomogeneousVariableNames.addAll(stashedNameList);
            candidateHomogeneousVariableMDs.addAll(stashedMDList);
        }

        HomogeneousVariableVisitor homogeneousVariableVisitor = new HomogeneousVariableVisitor(valueMap);
        enclosingCU.accept(homogeneousVariableVisitor);
        HashMap<ArrayList<String>, ArrayList<MethodDeclaration>> resMap= searchInEnclosingJavaForNameAndMD(homogeneousVariableVisitor,usedLocalNames);
        if(resMap==null) {
            return;
        }
        ArrayList<String> stableHVNList = resMap.entrySet().iterator().next().getKey();
        ArrayList<MethodDeclaration> stableHVNMDList = resMap.entrySet().iterator().next().getValue();
        if(stableHVNList.size()!=0 &&stableHVNMDList.size()==stableHVNList.size()){
            candidateHomogeneousVariableNames.addAll(stableHVNList);
            candidateHomogeneousVariableMDs.addAll(stableHVNMDList);
            stableHVNNum++;
        }

//        System.out.println("candidate homogeneousVariableNames:" + candidateHomogeneousVariableNames);
        if(candidateHomogeneousVariableNames.size()==0) {
            return;
        }

        all_number_homogeneous_variable += candidateHomogeneousVariableNames.size();
//        System.out.println("final homogeneousVariableNames:" + candidateHomogeneousVariableNames);
        String name= candidateHomogeneousVariableNames.get(0);
        MethodDeclaration methodDeclaration= candidateHomogeneousVariableMDs.get(0);
        if (candidateHomogeneousVariableNames.size() == 1) {
//            System.out.println("methodBeforeRefactoring");
//            System.out.println(enclosingMethod);
//            System.out.println("siblingMethod");
//            System.out.println(methodDeclaration);
            if(isUniversalInitializers) return;
            // judge the reliability of HVN from the aspect of the complexity of initializer
            double initializationComplexity = getInitializationComplexity(initializer,initializerExpression);
            if(initializationComplexity<initializationComplexityThreshold){
                // judge the reliability of HVN from the aspect of its reference similarity
                HashMap<String, Double> res =
                        getFineGrainedContextSimilarity(valueMap, name, methodDeclaration,initializerExpression,initializerExpressionParent);
                String referenceNode= res.entrySet().iterator().next().getKey();
                double sim= res.entrySet().iterator().next().getValue();
                if(sim<= fineGrainedContextSimThreshold) {
                    return;
                }
            }

            reuseSingleHomogeneousVariableName(index,name,valueMap,usedLocalNames);
        }
        else{
            HashSet<String> set = new HashSet<>(candidateHomogeneousVariableNames);
            if (set.size() == 1) {
                reuseMultipleSameHomogeneousVariableName(index,name,valueMap,usedLocalNames);
//                printPerformance("Reuse Multiple Same HVN");
            }
            else{
                if(isUniversalInitializers) return;
//                System.out.println("calculating similarity...");
                ArrayList<String> homogeneousVariableNames=new ArrayList<>();
                ArrayList<MethodDeclaration> homogeneousVariableMDs=new ArrayList<>();
                retainNearestHVN(index,valueMap,candidateHomogeneousVariableNames,candidateHomogeneousVariableMDs,
                        homogeneousVariableNames,homogeneousVariableMDs,initializationComplexityThreshold,
                        fineGrainedContextSimThreshold, initializerExpression,initializerExpressionParent);
                reuseMultipleDifferentHomogeneousVariableNameSimFiltered(index,valueMap, enclosingMethod,homogeneousVariableMDs,homogeneousVariableNames,usedLocalNames);
//                printPerformance("Reuse Multiple Different HVN");
            }
        }
//        printPerformance("Reuse");
    }

    private static double getInitializationComplexity(String initializer, Expression initializerExpression) {
        int iniLength= initializer.length();
        return iniLength;
    }
    private static void retainNearestHVN(int index,HashMap<String, String> valueMap, ArrayList<String>
            candidateHomogeneousVariableNames, ArrayList<MethodDeclaration> candidateHomogeneousVariableMDs,
                                         ArrayList<String> homogeneousVariableNames, ArrayList<MethodDeclaration>
                                                 homogeneousVariableMDs,
                                         double initializationComplexityThreshold,
                                         double fineGrainedContextSimThreshold,
                                         Expression initializerExpression,
                                         ASTNode initializerExpressionParent
                                         ) {

        for (int i = 0; i < candidateHomogeneousVariableNames.size(); i++) {
            // get the statements that reference the newly introduced variable.
//            System.out.println("searching reference");
            String candidateHomogeneousVariableName = candidateHomogeneousVariableNames.get(i);
            MethodDeclaration methodDeclaration = candidateHomogeneousVariableMDs.get(i);

            // judge the reliability of HVN from the aspect of the complexity of initializer
            double initializationComplexity = getInitializationComplexity(valueMap.get("initializer"),initializerExpression);
//            System.out.println("initializationComplexity");
//            System.out.println(initializationComplexity);
            if(initializationComplexity<initializationComplexityThreshold){
                // judge the reliability of HVN from the aspect of its reference similarity
                HashMap<String, Double> res =
                        getFineGrainedContextSimilarity(valueMap, candidateHomogeneousVariableName, methodDeclaration,initializerExpression,initializerExpressionParent);
                String referenceNode= res.entrySet().iterator().next().getKey();
                double sim= res.entrySet().iterator().next().getValue();
                if(sim<= fineGrainedContextSimThreshold) {
                    return;
                }
            }

            homogeneousVariableNames.add(candidateHomogeneousVariableName);
            homogeneousVariableMDs.add(methodDeclaration);

        }
    }

    private static HashMap<String,Double> getFineGrainedContextSimilarity(HashMap<String, String> valueMap,
                                                                          String candidateHomogeneousVariableName,
                                                                          MethodDeclaration methodDeclaration,
                                                                          Expression initializerExpression,
                                                                          ASTNode initializerExpressionParent) {
        VariableReferenceVisitor variableReferenceVisitor = new VariableReferenceVisitor(candidateHomogeneousVariableName);
        methodDeclaration.accept(variableReferenceVisitor);
        ArrayList<Statement> referenceStatements = variableReferenceVisitor.getReferenceStatements();
//        System.out.println("referenceStatements.size()");
//        System.out.println(referenceStatements.size());
        MostSimilarVariableReferenceFinder mostSimilarVariableReferenceFinder = new MostSimilarVariableReferenceFinder
                (valueMap, referenceStatements, candidateHomogeneousVariableName,initializerExpression,initializerExpressionParent);
        double sim = mostSimilarVariableReferenceFinder.getLiteralSimBetweenReferenceNode();
        String referenceNode = mostSimilarVariableReferenceFinder.getReferenceNode();
//        System.out.println(valueMap.get("involvedExpression"));
//        System.out.println(referenceNode);
//        System.out.println("Single Match Sim Between Reference Node:"+sim);
        HashMap<String,Double> res=new HashMap<>();
        res.put(referenceNode,sim);
        return res;
    }
    private static void printPerformance(String moduleName) {
        reuse_correct=reuse_single_correct  +reuse_multiplyEqual_correct +reuse_multiplyNotEqual_correct;
        reuse_recommended=reuse_multiplyEqual_recommended  +reuse_multiplyNotEqual_recommended +reuse_single_recommended;
        int modified_eclipse_correct=  heuristicRuleExpression_correct;
        int modified_eclipse_recommended= heuristicRuleExpression_recommended;
        int all_recommended =reuse_recommended + modified_eclipse_recommended;
        int all_correct=reuse_correct + modified_eclipse_correct;
        if(runType.equals("EmpiricalStudy")) all_records=4881; else all_records=27158;
        switch (moduleName) {
            case "Reuse":
                System.out.println(moduleName + ":" + reuse_correct + ":" + reuse_recommended + ":" + all_records +
                        String.format("precision:%.4f", reuse_correct * 1.0 / reuse_recommended) +
                        String.format("recall:%.4f", reuse_correct * 1.0 / all_records));
                break;

            case "Generation":
                System.out.println(moduleName + ":" + heuristicRuleExpression_correct + ":" + heuristicRuleExpression_recommended + ":" + all_records +
                        String.format("precision:%.4f", heuristicRuleExpression_correct * 1.0 / heuristicRuleExpression_recommended) +
                        String.format("recall:%.4f", heuristicRuleExpression_correct * 1.0 / all_records));
                break;
            case "Overall":
                System.out.println(moduleName + ":" + all_correct + ":" + all_recommended + ":" + all_records +
                        String.format("precision:%.4f", all_correct * 1.0 / all_recommended) +
                        String.format("recall:%.4f", all_correct * 1.0 / all_records));
                break;
        }

    }
    private static void reuseMultipleDifferentHomogeneousVariableNameSimFiltered(int index, HashMap<String, String> valueMap, MethodDeclaration thisMethodDeclaration, ArrayList<MethodDeclaration> homogeneousVariableMDs, ArrayList<String> homogeneousVariables,ArrayList<String> usedLocalNames) {
        String selectedElement="";
        double highestSim=0;
        if(homogeneousVariables.size()!=0&&homogeneousVariableMDs.size()==homogeneousVariables.size()) {
//            System.out.println("thisMethodDeclaration------------------");
//            System.out.println(thisMethodDeclaration);
            for (int k = 0; k < homogeneousVariableMDs.size(); k++) {
                MethodDeclaration similarMethodDeclaration = homogeneousVariableMDs.get(k);
//                System.out.println("similarMethodDeclaration------------------");
//                System.out.println(similarMethodDeclaration);
                double structuralSim = SimilarityCalculator.calculateStructuralSimilarity(similarMethodDeclaration, thisMethodDeclaration);
                double literalSim = SimilarityCalculator.calculateLiteralSimilarity(similarMethodDeclaration.toString(), thisMethodDeclaration.toString());
                double totalSim = (structuralSim + literalSim)/2;
//                System.out.println("structuralSim:"+structuralSim);
//                System.out.println("literalSim:"+literalSim);
//                System.out.println("totalSim:"+totalSim);

                highestSim = Math.max(highestSim, totalSim);
                if (highestSim == totalSim) selectedElement = homogeneousVariables.get(k);
            }
//            System.out.println("highestSim:"+highestSim);
//            System.out.println("selectedElement:"+selectedElement);
            String variableName = valueMap.get("variableName");
//            System.out.println("groundTruth:"+ variableName);
//            if(isTop10UniversalNames(selectedElement)) return;
            if (selectedElement != null && !isKeyWord(selectedElement) && !isUsedLocalNames(usedLocalNames,selectedElement)) {
//            if (selectedElement != null) {
                reuse_multiplyNotEqual_recommended++;
                ifRecommended=true;
                ifReused = true;
                if (selectedElement.equalsIgnoreCase(variableName)) {
                    reuse_multiplyNotEqual_correct++;
                }
            }
        }
    }

    private static void reuseMultipleSameHomogeneousVariableName(int index,String homogeneousVariableName,HashMap<String, String> valueMap,ArrayList<String> usedLocalNames) {
        String variableName= valueMap.get("variableName");
        if (homogeneousVariableName != null && !isKeyWord(homogeneousVariableName) && !isUsedLocalNames(usedLocalNames,homogeneousVariableName)) {
            reuse_multiplyEqual_recommended++;
            ifRecommended=true;
            ifReused=true;
            if (homogeneousVariableName.equalsIgnoreCase(variableName)) {
                reuse_multiplyEqual_correct++;
            }
        }

    }

    private static void reuseSingleHomogeneousVariableName(int index,String homogeneousVariableName,HashMap<String, String> valueMap,ArrayList<String> usedLocalNames) {
        String variableName= valueMap.get("variableName");
        if (homogeneousVariableName != null && !isKeyWord(homogeneousVariableName) && !isUsedLocalNames(usedLocalNames,homogeneousVariableName)) {
            reuse_single_recommended++;
            ifRecommended=true;
            ifReused=true;
            if (homogeneousVariableName.equalsIgnoreCase(variableName)) {
                reuse_single_correct++;
//                System.out.println("Reuse Single:");
//                System.out.println("HVN:"+homogeneousVariableName);
//                System.out.println("GroundTruth:"+variableName);
            }
        }

    }
}

