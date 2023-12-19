package exp.evalidea;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class MyVisitor extends JavaRecursiveElementVisitor {
    private final String variableName;
    private PsiType variableType;
    private PsiExpression initializerExpression;
    public MyVisitor(String variableName){
        this.variableName=variableName;
        this.variableType=null;
        initializerExpression=null;
        System.out.println("variableName:"+variableName);
    }

    public PsiExpression getInitializerExpression() {
        return initializerExpression;
    }

    public PsiType getVariableType() {
        return variableType;
    }

    @Override
    public void visitLocalVariable(@NotNull PsiLocalVariable element) {
        System.out.println(element);
        String localVariableName = element.getName();
        if(localVariableName.equals(this.variableName)){
            this.initializerExpression= element.getInitializer();
            this.variableType=element.getType();
        }
    }
}
