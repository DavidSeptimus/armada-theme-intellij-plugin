package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.python.PyTokenTypes
import com.jetbrains.python.psi.PyAnnotation
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.PyTypeParameter

class PythonAnnotator : Annotator {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!annotateTypeParameter(element, holder)) {
            annotateTypeAnnotation(element, holder)
        }
    }

    private fun annotateTypeParameter(
        element: PsiElement,
        holder: AnnotationHolder
    ): Boolean {
        if (element.elementType == PyTokenTypes.IDENTIFIER && hasAncestor(element, PyTypeParameter::class.java)) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                .textAttributes(TextAttributeKeys.PYTHON_TYPE_PARAMETER)
                .create()
            return true
        }
        return false
    }

    private fun annotateTypeAnnotation(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        when {
            element.elementType == PyTokenTypes.IDENTIFIER && element.parent is PyReferenceExpression -> {
                if (hasAncestor(element, PyAnnotation::class.java)) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                        .textAttributes(TextAttributeKeys.PYTHON_TYPE_ANNOTATION)
                        .create()
                }
            }
        }
    }
}