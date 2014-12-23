package net.detunized.unfuckjs;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory;
import org.jetbrains.annotations.NotNull;

public class UnfuckJsIntention extends PsiElementBaseIntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        JSStatement statement = PsiTreeUtil.getParentOfType(psiElement, JSStatement.class);
        if (statement != null)
            unfuckStatement(statement);
    }

    private void unfuckStatement(JSStatement s) {
        if (s instanceof JSExpressionStatement)
            unfuckExpressionStatement((JSExpressionStatement)s);
        else if (s instanceof JSReturnStatement) {
            unfuckReturnStatement((JSReturnStatement)s);
        }
    }

    private void unfuckExpressionStatement(JSExpressionStatement s) {
        JSExpression e = s.getExpression();
        if (e instanceof JSCommaExpression) {
            splitComma(s, (JSCommaExpression)e);
        }
    }

    private void unfuckReturnStatement(JSReturnStatement s) {
        JSExpression e = s.getExpression();
        if (e instanceof JSCommaExpression) {
            splitComma(s, (JSCommaExpression)e, "return");
        }
    }

    private void splitComma(JSStatement s, JSCommaExpression e) {
        splitComma(s, e, "");
    }

    // TODO: Add support for while, for and others
    // keyword: return, throw
    private void splitComma(JSStatement s, JSCommaExpression e, String keyword) {
        String lhs = e.getLOperand().getText() + ';';
        String rhs = appendSpace(keyword) + e.getROperand().getText() + ';';

        JSStatement parent = PsiTreeUtil.getParentOfType(s, JSStatement.class);
        if (parent instanceof JSIfStatement) {
            JSIfStatement ifStatement = (JSIfStatement)parent;
            if (s == ifStatement.getThen() || s == ifStatement.getElse()) {
                JSElementFactory.replaceStatement(s, '{' + lhs + rhs + '}');
            }
        } else {
            JSStatement ns = JSElementFactory.replaceStatement(s, lhs);
            JSElementFactory.addStatementAfter(ns, rhs);
        }
    }

    private String appendSpace(String text) {
        return text.isEmpty() ? text : text + ' ';
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return true;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Unfuck";
    }

    @NotNull
    @Override
    public String getText() {
        return "Unfuck some";
    }
}
