package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSBinaryExpression
import org.intellij.idea.lang.javascript.psiutil.BoolUtils

public class ConvertOrToIf: ConvertBooleanToIf("||") {
    override fun getCondition(expression: JSBinaryExpression) =
            BoolUtils.getNegatedExpressionText(expression.getLOperand())

    override fun getThen(expression: JSBinaryExpression) =
            expression.getROperand().getText()
}
