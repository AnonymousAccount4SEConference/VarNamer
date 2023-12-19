package visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashMap;

public class HomogeneousVariableVisitor extends ASTVisitor {
    private final String initializer;
    private ArrayList<String> similarNameAndType;
    private ArrayList<String> similarNames;
    private ArrayList<MethodDeclaration> methodDeclarations;
    private HashMap<String, String> valueMap;

    public HomogeneousVariableVisitor(String initializer){
        this.initializer = initializer;
        similarNameAndType = new ArrayList<>();
        methodDeclarations=new ArrayList<>();
        similarNames = new ArrayList<>();
        valueMap=new HashMap<>();
    }

    public HomogeneousVariableVisitor(HashMap<String, String> valueMap) {
        this.valueMap=valueMap;
        this.initializer = valueMap.get("initializer");
        similarNameAndType = new ArrayList<>();
        methodDeclarations=new ArrayList<>();
        similarNames = new ArrayList<>();
    }

    public ArrayList<String> getSimilarNames() {
        return similarNames;
    }

    public ArrayList<String> getSimilarNameAndType() {
        return this.similarNameAndType;
    }

    public void setSimilarNameAndType(String similarName,String type) {
        this.similarNameAndType.add(similarName+"#"+type);
    }

    public ArrayList<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclarations.add(methodDeclaration);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        String type= node.getType().toString();
        if(type.contains("<"))
            type= type.substring(0,type.indexOf("<"));
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
//            System.out.println("Local variable: " + fragment.getName().getIdentifier() );
            if(fragment.getInitializer()!=null){
                Expression initializerExpr= fragment.getInitializer();
                String initializer = initializerExpr.toString().replaceAll(" ","");
//                int distance = RecycleNamesInSameJavaFile.calculateEditDistance(initializer,this.initializer);
                if(this.initializer.equalsIgnoreCase(initializer)
                ){
                    setSimilarNameAndType(fragment.getName().toString(),type);
                    similarNames.add(fragment.getName().toString());
                    ASTNode surroundingBlock= initializerExpr;
                    while ((surroundingBlock= surroundingBlock.getParent()) != null) {
                        if (surroundingBlock instanceof MethodDeclaration) {
                            break;
                        }
                    }
                    if(surroundingBlock!=null) {
                        setMethodDeclaration((MethodDeclaration) surroundingBlock);
                    }
                }

            }
        }
        return true;
    }
}