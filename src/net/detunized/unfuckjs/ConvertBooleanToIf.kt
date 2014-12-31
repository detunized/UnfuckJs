package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSBinaryExpression
import com.intellij.lang.javascript.psi.JSStatement
import com.intellij.lang.javascript.psi.JSExpressionStatement
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory

abstract class ConvertBooleanToIf(val operator: String): StatementIntentionAction() {
    override val name = "Convert ${operator} to if"

    abstract fun getCondition(expression: JSBinaryExpression): String
    abstract fun getThen(expression: JSBinaryExpression): String

    override fun invoke(statement: JSStatement) {
        val e = (statement as JSExpressionStatement).getExpression() as JSBinaryExpression
        JSElementFactory.replaceStatement(
                statement,
                "if (${getCondition(e)}) { ${getThen(e)}; }"
        )
    }

    override fun isAvailable(statement: JSStatement): Boolean {
        if (statement is JSExpressionStatement) {
            val e = statement.getExpression()
            if (e is JSBinaryExpression && getOperatorText(e) == operator)
                return true
        }
        return false
    }
}
