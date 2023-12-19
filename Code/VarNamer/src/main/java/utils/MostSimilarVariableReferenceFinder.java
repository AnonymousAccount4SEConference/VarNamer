package utils;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MostSimilarVariableReferenceFinder {
    String variableName;
    private final HashMap<String, String> valueMap;
    private final ASTNode initializerExpressionParent;
    private final Expression initializerExpression;
    private double literalSimBetweenReferenceNode;
    private String referenceNode;
    ArrayList<Statement> referenceStatements;
    List<Statement> validReferenceStatements;
    ArrayList<String> referenceStatementStrs;

    public double getLiteralSimBetweenReferenceNode() {
        return literalSimBetweenReferenceNode;
    }

    public String getReferenceNode() {
        return referenceNode;
    }

    public MostSimilarVariableReferenceFinder(HashMap<String, String> valueMap, ArrayList<Statement> referenceStatements,String variableName){
        this.valueMap=valueMap;
        this.variableName=variableName;
        this.referenceStatements=referenceStatements;
        literalSimBetweenReferenceNode=0.0;
        referenceStatementStrs=new ArrayList<>();
        referenceNode=null;
        this.initializerExpressionParent=null;
        this.initializerExpression=null;
        if(this.referenceStatements.size()>1){
            preprocessStatements();
            getTheMostSimilarReferenceNodeAndSim();
        }

    }
    public MostSimilarVariableReferenceFinder(HashMap<String, String> valueMap, ArrayList<Statement> referenceStatements,String variableName, Expression initializerExpression,ASTNode initializerExpressionParent){
        this.valueMap=valueMap;
        this.variableName=variableName;
        this.referenceStatements=referenceStatements;
        literalSimBetweenReferenceNode=0.0;
        referenceNode=null;
        this.initializerExpression=initializerExpression;
        this.initializerExpressionParent=initializerExpressionParent;
        if(referenceStatements.size()>1){
            validReferenceStatements=referenceStatements.subList(1,referenceStatements.size());
            if(this.validReferenceStatements.size()>1){
                getTheMostSimilarReferenceNodeAndSim();
            }
        }
    }

    private void getTheMostSimilarReferenceNodeAndSim() {

//        System.out.println("validReferenceStatements");
//        System.out.println(validReferenceStatements);
        double highestSim=0;
        String referenceNode=null;
        for(Statement referenceStr:validReferenceStatements){
            ASTNode referenceExp= preprocessStatements(referenceStr);
//            System.out.println(initializerExpressionParent);
//            System.out.println(initializerExpressionParent.toString().replace(initializerExpression.toString(),variableName));
//            System.out.println(referenceExp);
            double structuralSimilarity= SimilarityCalculator.calculateStructuralSimilarity(initializerExpressionParent,referenceExp);
            double literalSimilarity= SimilarityCalculator.calculateLiteralSimilarity(
                    initializerExpressionParent.toString().replace(initializerExpression.toString(),variableName),
                    referenceExp.toString());
//            System.out.println("structuralSimilarity");
//            System.out.println(structuralSimilarity);
//            System.out.println("literalSimilarity");
//            System.out.println(literalSimilarity);
            double totalSim= (literalSimilarity + structuralSimilarity) /2;
//            System.out.println("totalSim:");
//            System.out.println(totalSim);
//            System.out.println(initializerExpression.toString());
            if(totalSim>highestSim){
                highestSim=totalSim;
                referenceNode=referenceExp.toString();
            }
        }
        this.literalSimBetweenReferenceNode =highestSim ;
        this.referenceNode =referenceNode;

    }
    private ASTNode preprocessStatements(Statement statement) {
        if(statement instanceof EnhancedForStatement){
            EnhancedForStatement enhancedForStatement = (EnhancedForStatement) statement;
//            SingleVariableDeclaration parameter = enhancedForStatement.getParameter();
//            String vd= parameter.toString().replace(parameter.getType().toString(),"");
            Expression expression=enhancedForStatement.getExpression();
            return expression;
        }
        else if(statement instanceof ForStatement){
//            StringBuilder stringBuilder= new StringBuilder();
            ForStatement forStatement=((ForStatement) statement);
//            List<Expression> initializers = forStatement.initializers();
//            for(Expression ini:initializers) stringBuilder.append(ini);
            Expression expression=forStatement.getExpression();
            return expression;
//            stringBuilder.append(";").append(expression).append(";");
//            List<Expression> updaters = forStatement.updaters();
//            for(Expression ini:updaters) stringBuilder.append(ini);
        }
        else if(statement instanceof IfStatement ifStatement){
            return ifStatement.getExpression();
        }
        else if(statement instanceof SwitchStatement switchStatement){
            return switchStatement.getExpression();
        }
        else if(statement instanceof WhileStatement whileStatement){
            return whileStatement.getExpression();
        }
        else if(statement instanceof TryStatement){
            TryStatement tryStatement= ((TryStatement) statement);
            List<Expression> resources = tryStatement.resources();
            return resources.get(0);
        }
        else if(statement instanceof ExpressionStatement expressionStatement){
            return expressionStatement.getExpression();
        }
        else{
            return statement;
        }
    }

    private void preprocessStatements() {
//        System.out.println(referenceStatements);
//        System.out.println(validReferenceStatements);
        for(Statement statement:validReferenceStatements){
            if(statement instanceof EnhancedForStatement){
                EnhancedForStatement enhancedForStatement = (EnhancedForStatement) statement;
                SingleVariableDeclaration parameter = enhancedForStatement.getParameter();
                String vd= parameter.toString().replace(parameter.getType().toString(),"");
                String expression=enhancedForStatement.getExpression().toString();
                referenceStatementStrs.add("for("+vd+":"+expression+")");
            }
            else if(statement instanceof ForStatement){
                StringBuilder stringBuilder= new StringBuilder();
                ForStatement forStatement=((ForStatement) statement);
                List<Expression> initializers = forStatement.initializers();
                for(Expression ini:initializers) stringBuilder.append(ini);
                String expression=forStatement.getExpression().toString();
                stringBuilder.append(";").append(expression).append(";");
                List<Expression> updaters = forStatement.updaters();
                for(Expression ini:updaters) stringBuilder.append(ini);
                referenceStatementStrs.add("for("+stringBuilder+")");
            }
            else if(statement instanceof IfStatement){
                String expression = ((IfStatement) statement).getExpression().toString().trim();
                referenceStatementStrs.add("if("+expression+")");
            }
            else if(statement instanceof SwitchStatement){
                referenceStatementStrs.add(((SwitchStatement) statement).getExpression().toString().trim());
            }
            else if(statement instanceof WhileStatement){
                referenceStatementStrs.add(((WhileStatement) statement).getExpression().toString().trim());
            }
            else if(statement instanceof TryStatement){
                TryStatement tryStatement= ((TryStatement) statement);
                List<Expression> resources = tryStatement.resources();
                for(Expression expression:resources){
                    referenceStatementStrs.add(expression.toString().trim());
                }
            }
            else{
                referenceStatementStrs.add(statement.toString().trim());
            }
        }
    }
}
