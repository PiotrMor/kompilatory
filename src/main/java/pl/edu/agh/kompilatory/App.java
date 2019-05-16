package pl.edu.agh.kompilatory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.edu.agh.kompilatory.CppParser.CppVisitor;
import pl.edu.agh.kompilatory.gen.CPPBaseVisitor;
import pl.edu.agh.kompilatory.gen.CPPLexer;
import pl.edu.agh.kompilatory.gen.CPPParser;
import pl.edu.agh.kompilatory.gen.CPPVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class App {

    public static void main(String[] args) {
        try {
            ANTLRInputStream inputStream = new ANTLRInputStream(new FileInputStream("src/main/java/pl/edu/agh/kompilatory/input.cpp"));
            CPPLexer cppLexer = new CPPLexer(inputStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(cppLexer);
            CPPParser cppParser = new CPPParser(commonTokenStream);

            CPPParser.TranslationunitContext translationunitContext = cppParser.translationunit();

            CppVisitor visitor = new CppVisitor(new PrintStream("python.py"));

            visitor.visit(translationunitContext);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}