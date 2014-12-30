package net.detunized.unfuckjs

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSCommaExpression
import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSStatement
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory
import com.intellij.openapi.ui.Messages
import com.intellij.psi.util.PsiTreeUtil

public class SplitCommaExpression: PsiElementBaseIntentionAction() {
    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
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

        val s = getStatement(element)
        when (s) {
            is JSExpressionStatement -> doIt(s, s.getExpression() as JSCommaExpression)
            //is JSExpressionStatement -> doIt(s, s.getExpression() as JSCommaExpression)
        }
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val s = getStatement(element)
        return when (s) {
            is JSExpressionStatement -> s.getExpression() is JSCommaExpression
            //is JSReturnStatement -> s.getExpression() is JSCommaExpression
            else -> false
        }
    }

    override fun getText() = "UnfuckJs: Split comma expression"

    override fun getFamilyName() = "UnfuckJs"

    fun getStatement(e: PsiElement) = getParentOfType<JSStatement>(e)

    // "inline" and "reified" are a necessary Kotlin voodoo to make
    // javaClass<T> work inside the function
    inline fun <reified T: PsiElement> getParentOfType(e: PsiElement): T? =
            PsiTreeUtil.getParentOfType(e, javaClass<T>())
}
