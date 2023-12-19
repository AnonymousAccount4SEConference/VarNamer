package visitors;

import org.eclipse.jdt.core.dom.*;

import javax.swing.plaf.nimbus.State;

public class InitializerParentVisitor extends GenericVisitor {
    private Expression initializer;
    private ASTNode initializerInPlace;
    private ASTNode initializerParent;
    private String involvedExpression;
    public InitializerParentVisitor(Expression initializer,String involvedExpression){
        this.initializer=initializer;
        this.initializerParent=null;
        this.initializerInPlace=null;
        this.involvedExpression=involvedExpression;
    }

    public ASTNode getInitializerParent() {
        return initializerParent;
    }
    public ASTNode getInitializerInPlace() {
        return initializerInPlace;
    }
    @Override
    public boolean visitNode(ASTNode node) {

        boolean subtreeMatch = node.subtreeMatch(new ASTMatcher(), initializer);
        if(subtreeMatch){
//            System.out.println(initializer);
//            System.out.println("node");
//            System.out.println(node);
            initializerInPlace=node;
            while(node.getParent() instanceof Expression){
                node = node.getParent();
            }
            initializerParent= node;
//            System.out.println("initializerParent");
//            System.out.println(initializerParent);
//            System.out.println(initializerParent.toString().replace(" ",""));
//            System.out.println(involvedExpression);
            String initializerParentStr=initializerParent.toString().replace(" ","").replace("\n", "").trim();
            if((involvedExpression.contains(initializerParentStr) || initializerParentStr.contains(involvedExpression))){
                throw new AbortSearchException();
            }
        }
        return true;
    }

}
