package extractvariable.ast;

import org.eclipse.jdt.core.dom.*;

public class InitializerVisitor extends ASTVisitor {
    private final String variableName;
    private final String initializerStr;
    private Expression initializer;
    private Type type;
    public InitializerVisitor(String variableName,String initializerStr){
        this.variableName=variableName;
        this.initializer=null;
        this.type=null;
        this.initializerStr=initializerStr;
    }

    public Expression getInitializer() {
        return initializer;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
            String name=fragment.getName().toString();
            if(this.variableName.equals(name) && fragment.getInitializer()!=null) {
                String ini = fragment.getInitializer().toString().replace(" ","").replace("\n", "");
//                System.out.println(ini);
//                System.out.println(initializerStr);
                if(ini.equalsIgnoreCase(initializerStr)){
                    this.initializer= fragment.getInitializer();
                    this.type=node.getType();
                }
            }
        }
        return true;
    }

}
