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

    override fun invoke(statement: JSStatement) {
        when (statement) {
            is JSExpressionStatement -> split(statement)
            is JSReturnStatement -> split(statement)
        }
    }

    override fun isAvailable(statement: JSStatement) = when (statement) {
        is JSExpressionStatement -> statement.getExpression() is JSCommaExpression
        is JSReturnStatement -> statement.getExpression() is JSCommaExpression
        else -> false
    }

    fun split(statement: JSExpressionStatement) {
        val e = statement.getExpression() as JSCommaExpression
        split(
                statement,
                e.getLOperand().getText() + ';',
                e.getROperand().getText() + ';'
        )
    }

    fun split(statement: JSReturnStatement) {
        val e = statement.getExpression() as JSCommaExpression
        split(
                statement,
                e.getLOperand().getText() + ';',
                "return " + e.getROperand().getText() + ';'
        )
    }

    fun split(statement: JSStatement, lhs: String, rhs: String) {
        if (statement.getParent() is JSBlockStatement) {
            val newLhs = JSElementFactory.replaceStatement(statement, lhs)
            JSElementFactory.addStatementAfter(newLhs, rhs)
        } else {
            // TODO: Handle braceless blocks
            Messages.showInfoMessage(
                    "Only works in the block at the moment.\n" +
                            "Use built-in quick fix to add braces first.",
                    getText()
            )
        }
    }
}
