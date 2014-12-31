package net.detunized.unfuckjs

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

abstract class IntentionAction: PsiElementBaseIntentionAction() {
    abstract val name: String

    override fun getFamilyName() = "UnfuckJs"

    override fun getText() = "${getFamilyName()}: ${name}"

    // "inline" and "reified" are a necessary Kotlin voodoo to make
    // javaClass<T> work inside the function
    inline fun <reified T: PsiElement> getParentOfType(e: PsiElement): T? =
            PsiTreeUtil.getParentOfType(e, javaClass<T>())
}
