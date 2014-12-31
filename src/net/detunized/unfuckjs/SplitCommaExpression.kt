package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSCommaExpression
import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSStatement
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory
import com.intellij.openapi.ui.Messages

public class SplitCommaExpression: StatementIntentionAction() {
    override val name = "Split comma expression"

    override fun invoke(statement: JSStatement) {
        fun doIt(s: JSExpressionStatement, e: JSCommaExpression) {
            val lhs = e.getLOperand().getText() + ';'
            val rhs = e.getROperand().getText() + ';'

            if (s.getParent() is JSBlockStatement) {
                val newLhs = JSElementFactory.replaceStatement(s, lhs)
                JSElementFactory.addStatementAfter(newLhs, rhs)
            } else {
                Messages.showInfoMessage("Only works in the block at the moment", getText())
            }
        }

        when (statement) {
            is JSExpressionStatement -> doIt(statement, statement.getExpression() as JSCommaExpression)
            //is JSExpressionStatement -> doIt(s, s.getExpression() as JSCommaExpression)
        }
    }

    override fun isAvailable(statement: JSStatement) = when (statement) {
        is JSExpressionStatement -> statement.getExpression() is JSCommaExpression
        //is JSReturnStatement -> s.getExpression() is JSCommaExpression
        else -> false
    }
}
