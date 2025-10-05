package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.CommentSaver.Companion.tokenType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtLabelReferenceExpression


class KotlinAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        annotateLabel(element, holder)
    }



    private fun annotateLabel(element: PsiElement, holder: AnnotationHolder) {
        if (element.tokenType == KtTokens.AT && element.parent is KtLabelReferenceExpression){
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                .textAttributes(TextAttributeKeys.KOTLIN_LABEL_AT)
                .create()
        }
    }
}
