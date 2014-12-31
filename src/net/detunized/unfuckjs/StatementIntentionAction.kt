package net.detunized.unfuckjs

import com.intellij.lang.javascript.psi.JSStatement
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.lang.javascript.psi.JSBinaryExpression
import org.intellij.idea.lang.javascript.psiutil.BinaryOperatorUtils

abstract class StatementIntentionAction: IntentionAction() {
    abstract fun invoke(statement: JSStatement)
    abstract fun isAvailable(statement: JSStatement): Boolean

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val s = getStatement(element)
        if (s != null) // Shouldn't really happen, makes compiler happy
            invoke(s)
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val s = getStatement(element)
        return s != null && isAvailable(s)
    }

    fun getStatement(e: PsiElement) =
            getParentOfType<JSStatement>(e)

    fun getOperatorText(e: JSBinaryExpression) =
            BinaryOperatorUtils.getOperatorText(e.getOperationSign())
}
