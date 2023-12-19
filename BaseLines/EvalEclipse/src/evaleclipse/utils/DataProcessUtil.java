package evaleclipse.utils;


import java.util.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import extractvariable.ast.MethodDeclarationVisitor;


public class DataProcessUtil {
	
    public static MethodDeclaration getMethodDeclaration(String filePath){
//      String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.lastIndexOf("."));
      ASTParser astParser = ASTParser.newParser(AST.JLS18);
      astParser.setEnvironment(null, null, null, true);
//      astParser.setUnitName(fileName);
//      astParser.setResolveBindings(true);
      astParser.setKind(ASTParser.K_COMPILATION_UNIT);
//      IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(file));
////    ICompilationUnit cu = JavaCore.createCompilationUnitFrom(ifile);
//      ICompilationUnit iCompilationUnit =(ICompilationUnit)JavaCore.create(ifile);
//      astParser.setSource(iCompilationUnit);
      String readFile = FileHelper.readFile(filePath);
      if(readFile==null )return null;
      astParser.setSource(("class A{"+ readFile +"}").toCharArray());
      CompilationUnit compilationUnit = (CompilationUnit) (astParser.createAST(null));
      MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
      compilationUnit.accept(visitor);
      return visitor.getMethodDeclaration();
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


