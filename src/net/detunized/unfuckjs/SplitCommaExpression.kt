package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSCommaExpression
import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSStatement
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory
import com.intellij.openapi.ui.Messages
import com.intellij.lang.javascript.psi.JSReturnStatement
import com.intellij.lang.javascript.psi.JSThrowStatement
import com.intellij.lang.javascript.psi.JSIfStatement

public class SplitCommaExpression: StatementIntentionAction() {
    override val name = "Split comma expression"

    override fun invoke(statement: JSStatement) {
        when (statement) {
            is JSExpressionStatement -> split(statement)
            is JSIfStatement -> split(statement)
            is JSReturnStatement -> split(statement)
            is JSThrowStatement -> split(statement)
        }
    }

    override fun isAvailable(statement: JSStatement) = when (statement) {
        is JSExpressionStatement -> statement.getExpression() is JSCommaExpression
        is JSIfStatement -> statement.getCondition() is JSCommaExpression
        is JSReturnStatement -> statement.getExpression() is JSCommaExpression
        is JSThrowStatement -> statement.getExpression() is JSCommaExpression
        else -> false
    }

    // split  a, b;
    //
    // to     a;
    //        b;
    fun split(statement: JSExpressionStatement) {
        val e = statement.getExpression() as JSCommaExpression
        split(
                statement,
                e.getLOperand().getText() + ';',
                e.getROperand().getText() + ';'
        )
    }

    // split  if (a, b)
    //
    // to     a;
    //        if (b)
    fun split(statement: JSIfStatement) {
        applyInBlock(statement, {
            val e = statement.getCondition() as JSCommaExpression
            val lhs = e.getLOperand().getText() + ';'
            val rhs = e.getROperand().getText()
            JSElementFactory.replaceExpression(e, rhs)
            JSElementFactory.addStatementBefore(statement, lhs)
        })
    }

    // split  return a, b;
    //
    // to     a;
    //        return b;
    fun split(statement: JSReturnStatement) =
        split(statement, statement.getExpression() as JSCommaExpression, "return")

    // split  throw a, b;
    //
    // to     a;
    //        throw b;
    fun split(statement: JSThrowStatement) =
        split(statement, statement.getExpression() as JSCommaExpression, "throw")

    fun split(statement: JSStatement, expression: JSCommaExpression, keyword: String) {
        split(
                statement,
                expression.getLOperand().getText() + ';',
                keyword + ' ' + expression.getROperand().getText() + ';'
        )
    }

    fun split(statement: JSStatement, lhs: String, rhs: String) {
        applyInBlock(statement, {
            val newLhs = JSElementFactory.replaceStatement(statement, lhs)
            JSElementFactory.addStatementAfter(newLhs, rhs)
        })
    }

    fun applyInBlock<S: JSStatement>(statement: S, action: () -> Unit) {
        if (statement.getParent() is JSBlockStatement) {
            action()
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
