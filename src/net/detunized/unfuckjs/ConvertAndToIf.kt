package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSBinaryExpression

public class ConvertAndToIf: ConvertBooleanToIf("&&") {
    override fun getCondition(expression: JSBinaryExpression) =
            expression.getLOperand().getText()

    override fun getThen(expression: JSBinaryExpression) =
            expression.getROperand().getText()
}
