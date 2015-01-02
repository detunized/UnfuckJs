package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSCommaExpression
import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSStatement
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory
import com.intellij.openapi.ui.Messages
import com.intellij.lang.javascript.psi.JSReturnStatement

public class SplitCommaExpression: StatementIntentionAction() {
    override val name = "Split comma expression"

    // TODO: Remove copy paste
    override fun invoke(statement: JSStatement) {
        fun doIt(s: JSExpressionStatement, e: JSCommaExpression) {
            val lhs = e.getLOperand().getText() + ';'
            val rhs = e.getROperand().getText() + ';'

            if (s.getParent() is JSBlockStatement) {
                val newLhs = JSElementFactory.replaceStatement(s, lhs)
                JSElementFactory.addStatementAfter(newLhs, rhs)
            } else {
                Messages.showInfoMessage(
                        "Only works in the block at the moment.\n" +
                                "Use built-in quick fix to add braces first.",
                        getText()
                )
            }
        }

        fun doIt(s: JSReturnStatement, e: JSCommaExpression) {
            val lhs = e.getLOperand().getText() + ';'
            val rhs = "return " + e.getROperand().getText() + ';'

            if (s.getParent() is JSBlockStatement) {
                val newLhs = JSElementFactory.replaceStatement(s, lhs)
                JSElementFactory.addStatementAfter(newLhs, rhs)
            } else {
                Messages.showInfoMessage(
                        "Only works in the block at the moment.\n" +
                                "Use built-in quick fix to add braces first.",
                        getText()
                )
            }
        }

        when (statement) {
            is JSExpressionStatement -> doIt(statement, statement.getExpression() as JSCommaExpression)
            is JSReturnStatement -> doIt(statement, statement.getExpression() as JSCommaExpression)
        }
    }

    override fun isAvailable(statement: JSStatement) = when (statement) {
        is JSExpressionStatement -> statement.getExpression() is JSCommaExpression
        is JSReturnStatement -> statement.getExpression() is JSCommaExpression
        else -> false
    }
}
