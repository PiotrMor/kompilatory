package pl.edu.agh.kompilatory.CppParser;

import org.antlr.v4.runtime.ParserRuleContext;
import pl.edu.agh.kompilatory.gen.CPPBaseVisitor;
import pl.edu.agh.kompilatory.gen.CPPParser;

import java.io.PrintStream;

public class CppVisitor extends CPPBaseVisitor {

    private PrintStream stream;

    private int indent = 0;
    private boolean indentNeeded = true;
    private boolean skipNextString = false;
    private boolean skipIf = false;

    public CppVisitor(PrintStream stream) {
        this.stream = stream;
    }


    @Override
    public Object visitSimpletypespecifier(CPPParser.SimpletypespecifierContext ctx) {
        return super.visitSimpletypespecifier(ctx);
    }

    @Override
    public Object visitUnqualifiedid(CPPParser.UnqualifiedidContext ctx) {
        printWithIndent(ctx.getText());
        return super.visitUnqualifiedid(ctx);
    }

    @Override
    public Object visitBraceorequalinitializer(CPPParser.BraceorequalinitializerContext ctx) {
        print(" = ");
        return super.visitBraceorequalinitializer(ctx);
    }

    @Override
    public Object visitLiteral(CPPParser.LiteralContext ctx) {
        if (!(ctx.getChild(0) instanceof CPPParser.BooleanliteralContext)) {
            print(ctx.getText());
        }
        return super.visitLiteral(ctx);
    }

    @Override
    public Object visitBooleanliteral(CPPParser.BooleanliteralContext ctx) {
        if (ctx.False() != null) {
            print("False");
        } else if (ctx.True() != null) {
            print("True");
        }
        return super.visitBooleanliteral(ctx);
    }

    @Override
    public Object visitSimpledeclaration(CPPParser.SimpledeclarationContext ctx) {
        super.visitSimpledeclaration(ctx);
        if (ctx.Semi() != null) {
            println();
        }
        return null;
    }

    @Override
    public Object visitBracedinitlist(CPPParser.BracedinitlistContext ctx) {
        print("[");
        super.visitBracedinitlist(ctx);
        print("]");
        return 0;
    }

    @Override
    public Object visitInitializerlist(CPPParser.InitializerlistContext ctx) {
        super.visitInitializerlist(ctx);
        if (ctx.getParent() instanceof CPPParser.InitializerlistContext && ((CPPParser.InitializerlistContext) ctx.getParent()).Comma() != null) {
            print(", ");
        }
        return null;
    }

    @Override
    public Object visitFunctiondefinition(CPPParser.FunctiondefinitionContext ctx) {
        printWithIndent("def ");
        indentNeeded = false;
        return super.visitFunctiondefinition(ctx);
    }

    @Override
    public Object visitParametersandqualifiers(CPPParser.ParametersandqualifiersContext ctx) {
        print("(");
        indentNeeded = false;
        super.visitParametersandqualifiers(ctx);
        print(("):"));
        indentNeeded = true;
        return null;
    }

    @Override
    public Object visitCompoundstatement(CPPParser.CompoundstatementContext ctx) {
        indent += 1;
        if (ctx.LeftBrace() != null) {
            println();
        }
        super.visitCompoundstatement(ctx);
        if (ctx.RightBrace() != null) {
            println();
        }
        indent -= 1;
        return null;
    }

    @Override
    public Object visitSelectionstatement(CPPParser.SelectionstatementContext ctx) {
        int statementIndex = 0;
        if (ctx.If() != null && !skipIf) {
            printWithIndent("if ");
            indentNeeded = false;
        }
        skipIf = false;

        visitChildren(ctx.condition());
        if (ctx.RightParen() != null) {
            print(":");
            indentNeeded = true;
        }
        visitChildren(ctx.statement(statementIndex++));
        if (ctx.Else() != null) {
            if (ctx.statement(statementIndex).selectionstatement() != null) {
                printWithIndent("elif ");
                skipIf = true;
                indentNeeded = false;
            } else {
                printWithIndent("else:");
            }
            visitChildren(ctx.statement(statementIndex));
        }
        return null;
    }

    @Override
    public Object visitAssignmentoperator(CPPParser.AssignmentoperatorContext ctx) {
        if (ctx.Assign() != null) {
            print(" = ");
        } else if (ctx.PlusAssign() != null) {
            print(" += ");
        } else if (ctx.MinusAssign() != null) {
            print(" -= ");
        }
        super.visitAssignmentoperator(ctx);
        return null;
    }

    @Override
    public Object visitRelationalexpression(CPPParser.RelationalexpressionContext ctx) {
        if (ctx.relationalexpression() != null) {
            visitChildren(ctx.relationalexpression());
        }
        if (ctx.Greater() != null) {
            print(" > ");
        } else if (ctx.Less() != null) {
            print(" < ");
        } else if (ctx.LessEqual() != null) {
            print(" <= ");
        } else if (ctx.GreaterEqual() != null) {
            print(" >=");
        }
        if (ctx.shiftexpression() != null) {
            visitChildren(ctx.shiftexpression());
        }
        return null;
    }

    @Override
    public Object visitPostfixexpression(CPPParser.PostfixexpressionContext ctx) {

        if (ctx.PlusPlus() != null) {
            visitChildren(ctx.postfixexpression());
            print(" += 1");
        } else if (ctx.LeftParen() != null) {
            visitChildren(ctx.postfixexpression());
            if (ctx.expressionlist() != null) {
                println("(" + ctx.expressionlist().getText() + ")");
            } else {
                println("()");
            }
        } else {
            super.visitPostfixexpression(ctx);
        }

        return null;
    }

    @Override
    public Object visitIterationstatement(CPPParser.IterationstatementContext ctx) {
        if (ctx.While() != null) {
            printWithIndent("while ");
        } else if (ctx.For() != null) {
            printWithIndent("for ");
        }
        indentNeeded = false;
        safeVisitChildren(ctx.forrangedeclaration());
        safeVisitChildren(ctx.condition());
        if (ctx.forrangedeclaration() != null) {
            print(" in ");
            indentNeeded = false;
            safeVisitChildren(ctx.forrangeinitializer());
        }
        print(":");


        indentNeeded = true;

        return visitChildren(ctx.statement());
    }

    @Override
    public Object visitEqualityexpression(CPPParser.EqualityexpressionContext ctx) {
        if (ctx.Equal() != null || ctx.NotEqual() != null) {
            safeVisitChildren(ctx.equalityexpression());
            if (ctx.Equal() != null) {
                print(" == ");
                indentNeeded = false;
            } else if (ctx.NotEqual() != null) {
                print(" != ");
            }
            safeVisitChildren(ctx.relationalexpression());
        } else {
            super.visitEqualityexpression(ctx);
        }

        return null;
    }

    @Override
    public Object visitJumpstatement(CPPParser.JumpstatementContext ctx) {
        if (ctx.Return() != null) {
            printWithIndent("return ");
            indentNeeded = false;
        }
        super.visitJumpstatement(ctx);
        indentNeeded = true;
        return null;
    }

    @Override
    public Object visitUnaryoperator(CPPParser.UnaryoperatorContext ctx) {
        if (ctx.getText().equals("!")) {
            print("not ");
        } else if (ctx.And() != null) {
            print(" + ");
        }
        indentNeeded = false;
        return super.visitUnaryoperator(ctx);
    }

    @Override
    public Object visitAdditiveexpression(CPPParser.AdditiveexpressionContext ctx) {
        if (ctx.Plus() != null || ctx.Minus() != null) {
            safeVisitChildren(ctx.additiveexpression());
            if (ctx.Plus() != null) {
                print(" + ");
            } else {
                print(" - ");
            }
            indentNeeded = false;
            safeVisitChildren(ctx.multiplicativeexpression());
            indentNeeded = true;
        } else {
            super.visitAdditiveexpression(ctx);
        }
        return null;
    }

    @Override
    public Object visitMultiplicativeexpression(CPPParser.MultiplicativeexpressionContext ctx) {
        if (ctx.Star() != null || ctx.Mod() != null || ctx.Div() != null) {
            safeVisitChildren(ctx.multiplicativeexpression());
            if (ctx.Star() != null) {
                print(" * ");
            } else if (ctx.Mod() != null) {
                print(" % ");
                indentNeeded = false;
            } else {
                print(" / ");
                indentNeeded = false;
            }
            safeVisitChildren(ctx.pmexpression());
        } else {
            super.visitMultiplicativeexpression(ctx);
        }

        return null;
    }

    @Override
    public Object visitParameterdeclarationlist(CPPParser.ParameterdeclarationlistContext ctx) {
        if (ctx.parameterdeclarationlist() != null) {
            safeVisitChildren(ctx.parameterdeclarationlist());
            print(", ");
            safeVisitChildren(ctx.parameterdeclaration());
        } else {
            super.visitParameterdeclarationlist(ctx);
        }

        return null;
    }

    @Override
    public Object visitLogicalandexpression(CPPParser.LogicalandexpressionContext ctx) {
        if (ctx.logicalandexpression() != null) {
            safeVisitChildren(ctx.logicalandexpression());
            print(" and ");
            indentNeeded = false;
            safeVisitChildren(ctx.inclusiveorexpression());
            indentNeeded = true;
        } else {
            super.visitLogicalandexpression(ctx);
        }
        return null;
    }

    @Override
    public Object visitLogicalorexpression(CPPParser.LogicalorexpressionContext ctx) {
        if (ctx.logicalorexpression() != null) {
            safeVisitChildren(ctx.logicalorexpression());
            print(" or ");
            indentNeeded = false;
            safeVisitChildren(ctx.logicalandexpression());
            indentNeeded = true;
        } else {
            super.visitLogicalorexpression(ctx);
        }
        return null;
    }

    @Override
    public Object visitExpressionstatement(CPPParser.ExpressionstatementContext ctx) {
        super.visitExpressionstatement(ctx);
        if (ctx.getText().contains(";")) {
            println();
        }
        return null;
    }

    @Override
    public Object visitClasskey(CPPParser.ClasskeyContext ctx) {
        print("class ");
        return super.visitClasskey(ctx);
    }

    @Override
    public Object visitClassname(CPPParser.ClassnameContext ctx) {
        if (!ctx.getText().equals("string")) {
            print(ctx.getText());
        }
        return super.visitClassname(ctx);
    }

    @Override
    public Object visitClassspecifier(CPPParser.ClassspecifierContext ctx) {
        safeVisitChildren(ctx.classhead());

        println(":");
        indent++;
        safeVisitChildren(ctx.memberspecification());
        indent--;
        return null;
    }





    private String getIndent() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }

    private void print(String text) {
        if (!skipNextString)
            stream.print(text);
        skipNextString = false;
    }

    private void println(String text) {
        if (!skipNextString)
            stream.println(text);
        skipNextString = false;

    }

    private void println() {
        stream.println();
    }

    private void printWithIndent(String text) {
        if (indentNeeded && !skipNextString) {
            stream.print(getIndent() + text);
        } else if (!skipNextString) {
            stream.print(text);
            indentNeeded = true;
            skipNextString = false;
        }
        skipNextString = false;
    }

    private void safeVisitChildren(ParserRuleContext context) {
        if (context != null) {
            visitChildren(context);
        }
    }


}