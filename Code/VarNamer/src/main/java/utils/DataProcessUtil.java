package utils;

import edu.lu.uni.serval.utils.FileHelper;
import org.eclipse.jdt.core.dom.*;
import visitors.MethodDeclarationVisitor;

import java.io.*;
import java.util.*;

public class DataProcessUtil {
    private static void copyJavaFile2SpecificPath(String originalFilePath, String path2Copy,String prefix,String javaFileName) {
        String fileContents = "";
        // restore the java file.
        if(FileHelper.isValidPath(originalFilePath))
            fileContents = FileHelper.readFile(originalFilePath);
        File javaFile = new File(path2Copy+prefix+"_"+javaFileName);
        if(!fileContents.equals(""))
            FileHelper.createFile(javaFile,fileContents);
    }
    public static CompilationUnit getCuFromString(String method){
        ASTParser astParser = ASTParser.newParser(AST.JLS18);
        astParser.setEnvironment(null, null, null, true);
        astParser.setResolveBindings(true);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(method.toCharArray());
        CompilationUnit compilationUnit = (CompilationUnit) (astParser.createAST(null));
        return compilationUnit;
    }
    public static CompilationUnit getCu(String filePath){
//        String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf("."));
        ASTParser astParser = ASTParser.newParser(AST.JLS18);
        astParser.setEnvironment(null, null, null, true);
//        astParser.setUnitName(fileName);
//        astParser.setResolveBindings(true);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
//        IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(file));
////      ICompilationUnit cu = JavaCore.createCompilationUnitFrom(ifile);
//        ICompilationUnit iCompilationUnit =(ICompilationUnit)JavaCore.create(ifile);
//        astParser.setSource(iCompilationUnit);
        String readFile = FileHelper.readFile(filePath);
        if(readFile==null )return null;
        astParser.setSource(readFile.toCharArray());
        return (CompilationUnit) (astParser.createAST(null));
    }
    public static MethodDeclaration getMethodDeclaration(String filePath){
//        String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf("."));
        ASTParser astParser = ASTParser.newParser(AST.JLS18);
        astParser.setEnvironment(null, null, null, true);
//        astParser.setUnitName(fileName);
//        astParser.setResolveBindings(true);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
//        IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(file));
////      ICompilationUnit cu = JavaCore.createCompilationUnitFrom(ifile);
//        ICompilationUnit iCompilationUnit =(ICompilationUnit)JavaCore.create(ifile);
//        astParser.setSource(iCompilationUnit);
        String readFile = FileHelper.readFile(filePath);
        if(readFile==null )return null;
        astParser.setSource(("class A{"+ readFile +"}").toCharArray());
        CompilationUnit compilationUnit = (CompilationUnit) (astParser.createAST(null));
        MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
        compilationUnit.accept(visitor);
        return visitor.getMethodDeclaration();
    }
    public static HashMap<String,ArrayList<String>> deserializeObject(String fileName){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            HashMap<String,ArrayList<String>> infos = (HashMap<String,ArrayList<String>> ) ois.readObject();
            return infos;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }
    public static void serializeObject(HashMap<String,ArrayList<String>> extendToProjectMap, String fileName){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(extendToProjectMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void splitDataViaProject(String dataFilePath, String outputPath) throws FileNotFoundException {
        ArrayList<String> lines= FileHelper.readFileByLines(dataFilePath);
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        for(String line:lines){
            HashMap<String, String> valueMap = resolveValueBag(line);
            assert valueMap != null;
            String javaFilePath = valueMap.get("javaFilePath");
            String projectName = javaFilePath.substring(1, javaFilePath.indexOf("/", 1));
            if(map.containsKey(projectName)){
                ArrayList<String> list= map.get(projectName);
                list.add(line);
                map.put(projectName,list);
            }
            else{
                ArrayList<String> list= new ArrayList<>();
                list.add(line);
                map.put(projectName,list);
            }
        }
        int cnt=0;
        for(Map.Entry<String,ArrayList<String>> entry:map.entrySet()){
            StringBuilder stringBuilder= new StringBuilder();
            String projectName= entry.getKey();
            ArrayList<String> list= entry.getValue();
            for(String s:list){
                cnt++;
                stringBuilder.append(s).append("\n");
            }
            FileHelper.outputToFile(outputPath+ projectName+".txt",stringBuilder,false);
        }
        System.out.println(cnt);
    }
    public static HashMap<String,String> resolveValueBag(String variableLine) {
//        f0e1b75fb9632a50cf37307630493b9780a26bb0###/addthis_stream-lib/src/test/java/com/clearspring/analytics/stream/cardinality/TestCountThenEstimate.java###/TestCountThenEstimate.java###com.clearspring.analytics.stream.cardinality.TestCountThenEstimate###assertCountThenEstimateEquals:CountThenEstimate CountThenEstimate ###assertArrayEquals(expected.estimator.getBytes(),actual.estimator.getBytes());###expBytes###expected.estimator.getBytes()###256:13:256:91
        // commitID+"###"+javaFilePath +"###" javaFileName +"###" + classPath + "###" + methodInfo +"###" + involvedExpression +"###" + variableName+"###" + initializer +"###" + offset;
        String[] splitArray = variableLine.replaceAll("\n", "").split("###");
        HashMap<String, String> values = new HashMap<>();
        if (splitArray.length != 9) {
            System.out.println("not valid record <8");
            return null;
        } else {
            String commitID = splitArray[0];
            values.put("commitID", commitID);
            String javaFileName = splitArray[2];
            values.put("javaFileName", javaFileName);
            String javaFilePath = splitArray[1];
            values.put("javaFilePath", javaFilePath);
            String classPath = splitArray[3];
            values.put("classPath", classPath);
            String methodInfo = splitArray[4];
            values.put("methodInfo", methodInfo);
            String involvedExpression = splitArray[5];
            values.put("involvedExpression", involvedExpression);
            String variableName = splitArray[6];
            values.put("variableName", variableName);
            String initializer = splitArray[7];
            values.put("initializer", initializer);
            String lineAndColumn = splitArray[8];
            values.put("lineAndColumn", lineAndColumn);
        }
        return values;
    }
}

