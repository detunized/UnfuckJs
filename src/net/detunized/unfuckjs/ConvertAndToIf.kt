package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSStatement
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSBinaryExpression
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory

public class ConvertAndToIf: StatementIntentionAction() {
    override val name = "Convert && to if"

    override fun invoke(statement: JSStatement) {
        val e = (statement as JSExpressionStatement).getExpression() as JSBinaryExpression
        val lhs = e.getLOperand().getText()
        val rhs = e.getROperand().getText()
        JSElementFactory.replaceStatement(statement, "if (${lhs}) { ${rhs}; }")
    }

    override fun isAvailable(statement: JSStatement): Boolean {
        if (statement is JSExpressionStatement) {
            val e = statement.getExpression()
            if (e is JSBinaryExpression && getOperatorText(e) == "&&")
                return true
        }
        return false
    }
}
