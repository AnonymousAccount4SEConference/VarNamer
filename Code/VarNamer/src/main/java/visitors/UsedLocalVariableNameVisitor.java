package visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class UsedLocalVariableNameVisitor extends ASTVisitor {
    private ArrayList<String> similarName;
    public UsedLocalVariableNameVisitor(){
        similarName = new ArrayList<>();
    }
    public ArrayList<String> getSimilarName() {
        return this.similarName;
    }

    public void setSimilarName(String similarName) {
        this.similarName.add(similarName);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
            setSimilarName(fragment.getName().toString());
        }
        return true;
    }
}