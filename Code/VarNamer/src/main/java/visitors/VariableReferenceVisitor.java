package visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;

public class VariableReferenceVisitor extends ASTVisitor {
    String variableName;
    ArrayList<Statement> referenceStatements;
    public VariableReferenceVisitor(String variableName){
        this.variableName=variableName;
        this.referenceStatements= new ArrayList<>();
    }

    public ArrayList<Statement> getReferenceStatements() {
        return referenceStatements;
    }

    @Override
     public boolean visit(SimpleName astNode) {
        if(astNode.getIdentifier().equals(variableName)){
            ASTNode surroundingBlock= astNode;
            while ((surroundingBlock= surroundingBlock.getParent()) != null) {
                if (surroundingBlock instanceof Statement) {
                    break;
                }
            }
            if(surroundingBlock!=null) {
                this.referenceStatements.add((Statement) surroundingBlock);
            }
        }
        return true;
    }
}
