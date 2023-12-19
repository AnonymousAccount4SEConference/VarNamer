package utils;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.remapper.dto.LeafNode;
import org.remapper.util.DiceFunction;

public class SimilarityCalculator {
    public static void main(String [] args){


    }
    public static double calculateStructuralSimilarity(ASTNode methodDeclaration1, ASTNode methodDeclaration2){
        LeafNode method1 = new LeafNode((CompilationUnit) methodDeclaration1.getRoot(), "", methodDeclaration1);
        method1.setDeclaration(methodDeclaration1);
        LeafNode method2 = new LeafNode((CompilationUnit) methodDeclaration2.getRoot(), "", methodDeclaration2);
        method2.setDeclaration(methodDeclaration2);
        return DiceFunction.calculateDice(method1, method2);
    }

    public static double calculateLiteralSimilarity(String text1, String text2){
        NormalizedLevenshtein nl = new NormalizedLevenshtein();
        return (1 - nl.distance(text1, text2));
    }

}
