package net.detunized.unfuckjs

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.intellij.idea.lang.javascript.psiutil.BinaryOperatorUtils
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory
import org.intellij.idea.lang.javascript.psiutil.ParenthesesUtils
import org.intellij.idea.lang.javascript.psiutil.BoolUtils

public class UnfuckJsIntention: PsiElementBaseIntentionAction() {
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val statement = PsiTreeUtil.getParentOfType(psiElement, javaClass<JSStatement>())
        if (statement != null)
            unfuckStatement(statement)
    }

    private fun unfuckStatement(s: JSStatement) {
        when (s) {
            is JSExpressionStatement -> unfuckExpressionStatement(s)
            is JSReturnStatement -> unfuckReturnStatement(s)
            is JSIfStatement -> unfuckIfStatement(s)
        }
    }

    private fun unfuckExpressionStatement(s: JSExpressionStatement) {
        val e = s.getExpression()
        when (e) {
            is JSCommaExpression -> splitComma(s, e)
            is JSBinaryExpression -> {
                val op = BinaryOperatorUtils.getOperatorText(e.getOperationSign())
                when (op) {
                    "&&" -> convertAndToIf(s, e)
                    "||" -> convertOrToIf(s, e)
                }
            }
            is JSParenthesizedExpression -> stripParens(s, e)
        }
    }

    private fun unfuckReturnStatement(s: JSReturnStatement) {
        val e = s.getExpression()
        if (e is JSCommaExpression) {
            splitComma(s, e, "return")
        }
    }

    fun unfuckIfStatement(s: JSIfStatement) {
        fun isInBlock(s: JSStatement) =
                s.getParent() is JSBlockStatement

        fun addBefore(anchor: JSStatement, code: String) {
            if (isInBlock(anchor))
                JSElementFactory.addStatementBefore(anchor, "${code};\n")
            else
                JSElementFactory.replaceElementWithStatement(anchor, "{ ${code};\n${s.getText()} }")
        }

        fun addBefore(anchor: JSStatement, e: PsiElement) =
                addBefore(anchor, e.getText())

        val e = s.getCondition()
        if (e is JSCommaExpression) {
            val lhs = e.getLOperand();
            val rhs = e.getROperand()
            JSElementFactory.replaceExpression(e, rhs.getText());
            addBefore(s, lhs)
        }
    }

    private fun splitComma(s: JSStatement, e: JSCommaExpression) {
        splitComma(s, e, "")
    }

    // TODO: Add support for while, for and others
    // keyword: return, throw
    private fun splitComma(s: JSStatement, e: JSCommaExpression, keyword: String) {
        val lhs = e.getLOperand().getText() + ';'
        val rhs = appendSpace(keyword) + e.getROperand().getText() + ';'

        val parent = PsiTreeUtil.getParentOfType<JSStatement>(s, javaClass<JSStatement>())
        if (parent is JSIfStatement) {
            if (s == parent.getThen() || s == parent.getElse()) {
                val block = JSElementFactory.replaceStatement(s, '{' + lhs + rhs + '}') as JSBlockStatement
                for (i in block.getStatements())
                    unfuckStatement(i)
            }
        } else {
            val ns1 = JSElementFactory.replaceStatement(s, lhs)
            val ns2 = JSElementFactory.addStatementAfter(ns1, rhs)

            unfuckStatement(ns1)
            unfuckStatement(ns2)
        }
    }

    private fun convertAndToIf(s: JSStatement, e: JSBinaryExpression) {
        val ifs = JSElementFactory.replaceStatement(
                s,
                "if (%s) { %s; }".format(e.getLOperand().getText(), e.getROperand().getText())
        ) as JSIfStatement

        unfuckStatement((ifs.getThen() as JSBlockStatement).getStatements()[0])
    }

    private fun convertOrToIf(s: JSStatement, e: JSBinaryExpression) {
        val ifs = JSElementFactory.replaceStatement(
                s,
                "if (%s) { %s; }".format(
                        BoolUtils.getNegatedExpressionText(e.getLOperand()),
                        e.getROperand().getText()
                )
        ) as JSIfStatement

        unfuckStatement((ifs.getThen() as JSBlockStatement).getStatements()[0])
    }

    private fun stripParens(s: JSStatement, e: JSParenthesizedExpression) {
        val ns = JSElementFactory.replaceStatement(s, ParenthesesUtils.stripParentheses(e).getText())

        unfuckStatement(ns)
    }

    private fun appendSpace(text: String): String {
        return if (text.isEmpty()) text else text + ' '
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        return false
    }

    override fun getFamilyName(): String {
        return "Unfuck"
    }

    override fun getText(): String {
        return "Unfuck some"
    }
}
