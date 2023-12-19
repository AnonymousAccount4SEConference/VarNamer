package evaleclipse.handlers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.core.manipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.AbortSearchException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import extractvariable.ast.InitializerParentVisitor;
import extractvariable.ast.InitializerVisitor;
import extractvariable.ast.UsedLocalVariableNameVisitor;
import safeextractor.utils.DataProcessUtil;
import safeextractor.utils.FileHelper;


public class SampleHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		double startTime= System.currentTimeMillis();
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {    
			
			handleCommand(window);  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		double endTime= System.currentTimeMillis();
		double during= endTime-startTime;
		System.out.println(during);
		System.out.println(during/1000+"s");
		return null;
	} 
	private void handleCommand(IWorkbenchWindow window) throws Exception { 
		String projectName = "javaee_jersey";
		ArrayList<Double> timeCostList = new ArrayList<>();
		IJavaProject project = findJavaProject(projectName);

		String allProjectBasePath = "<need to be configured>";
        String allRecordsPath = allProjectBasePath + "Records.txt";
        String afterEnclosingMethodFilePath=allProjectBasePath+ "RelatedMethods_AfterCommit_Java/";
        String enclosingMethodFilePath=allProjectBasePath + "RelatedMethods_BeforeCommit_Java/";
        String empiricalStudyProjectBasePath = allProjectBasePath + "EmpiricalStudy/";
        String empiricalStudyIndexFile = empiricalStudyProjectBasePath + "empiricalIndex.txt";
        StringBuilder stringBuilder= new StringBuilder();
        StringBuilder missingStringBuilder= new StringBuilder();
        ArrayList<String> allRecords = null;
        ArrayList<String> empiricalIndex=null;
        StringBuilder recommendedNames = new StringBuilder();
        try {
            allRecords = FileHelper.readFileByLines(allRecordsPath);
            empiricalIndex = FileHelper.readFileByLines(empiricalStudyIndexFile);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int nullCaseNum = 0;
        int eclipse_correct = 0;
        int valid_record_num = 0;

        for(int i=0;i<allRecords.size();i++){
            // empirical study
//            if (!empiricalIndex.contains(String.valueOf(i))) {
//                continue;
//            }
            // test
            if (empiricalIndex.contains(String.valueOf(i))) {
                continue;
            }
            double eachStartTime= System.currentTimeMillis();
            System.out.println(i);
           
            recommendedNames.append(i).append(":");
            String variableLine=allRecords.get(i);
            System.out.println(variableLine);
            String generated_name = "";
            HashMap<String, String> valueMap = DataProcessUtil.resolveValueBag(variableLine);
            assert valueMap != null;
            String variableName = valueMap.get("variableName");
            recommendedNames.append(variableName).append(":");
            String involvedExpression = valueMap.get("involvedExpression");
            String initializer = valueMap.get("initializer");
            String afterEachMethodFilePath = afterEnclosingMethodFilePath + i + ".java";
            String beforeEachMethodFilePath = enclosingMethodFilePath + i + ".java";
           
            if(!FileHelper.isValidPath(afterEachMethodFilePath)||!FileHelper.isValidPath(beforeEachMethodFilePath)) {
            	System.out.println("InvalidPath");
            	recommendedNames.append(" \n");
            	stringBuilder.append(i+":"+" \n");
                continue;
            }
            MethodDeclaration methodDeclaration= DataProcessUtil.getMethodDeclaration(afterEachMethodFilePath);
            MethodDeclaration beforeMethodDeclaration= DataProcessUtil.getMethodDeclaration(beforeEachMethodFilePath);
            
            if (methodDeclaration == null) {
            	System.out.println("MethodDeclarationNull");
            	recommendedNames.append(" \n");
            	stringBuilder.append(i+":"+" \n");
            	continue;
            }

            // logic of eclipse recommending names
            InitializerVisitor initializerVisitor= new InitializerVisitor(variableName,initializer);
            methodDeclaration.accept(initializerVisitor);
            Expression initializerExpression=initializerVisitor.getInitializer();
            Type type = initializerVisitor.getType();
            if(initializerExpression==null) {
            	System.out.println("ExpressionNull");
            	recommendedNames.append(" \n");
            	stringBuilder.append(i+":"+" \n");
            	missingStringBuilder.append(i).append("\n");
            	continue;
            }
            String [] res= StubUtility.getVariableNameSuggestions(NamingConventions.VK_LOCAL,null, type, initializerExpression, new ArrayList<String>());
            generated_name= res[0];
            recommendedNames.append(generated_name+"\n");
            System.out.println(generated_name);
            valid_record_num++;
            if(generated_name.equalsIgnoreCase(variableName)){
            	eclipse_correct++;
            }
            double eachEndTime= System.currentTimeMillis();
            double timeCost = eachEndTime-eachStartTime;
			timeCostList.add(timeCost);
            stringBuilder.append(i+":"+timeCost+"\n");
           
        }
        System.out.println(eclipse_correct + ":"+ valid_record_num +
                String.format("precision:%.4f",eclipse_correct*1.0/valid_record_num) +
                String.format("recall:%.4f",eclipse_correct*1.0/valid_record_num));
       
	}


	/**
	 * @param projectName
	 * @return
	 */
	public static IJavaProject findJavaProject(String projectName) {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; ++i) {
			if (JavaCore.create(projects[i]).getPath().lastSegment().contains(projectName)) {
				return JavaCore.create(projects[i]);
			}
		}
		return null;
	}



	public boolean isValid(CompilationUnit cu, String srcPath) { 
		if (cu.getJavaElement() != null
				&& cu.getJavaElement().getPath().removeFirstSegments(1).toString().startsWith(srcPath)) {
			return true;
		}
		return false;
	}

}
