package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSFunctionExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSThisExpression
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType


class JavaScriptAnnotator : Annotator {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateThisIdentifier(element, holder)
        annotatePropertyReference(element, holder)
        annotateFunctionDeclaration(element, holder)
    }

    private fun annotateFunctionDeclaration(
        element: PsiElement,
        holder: AnnotationHolder
    ) {
        if (element.elementType != JSTokenTypes.IDENTIFIER) {
            return
        }

            if (element.parent is JSFunction
                || element.parent is JSVariable && hasSibling(element, JSFunctionExpression::class.java)
            ) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.JAVASCRIPT_FUNCTION_DECLARATION_IDENTIFIER)
                    .create()
            }
    }

    private fun annotateThisIdentifier(element: PsiElement, holder: AnnotationHolder) {
        if (element is JSThisExpression) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                .textAttributes(TextAttributeKeys.JAVASCRIPT_THIS_IDENTIFIER)
                .create()
        }
    }


    private fun annotatePropertyReference(element: PsiElement, holder: AnnotationHolder) {

        if (element is JSReferenceExpression && element.lastChild is PsiElement && element.lastChild.elementType == JSTokenTypes.IDENTIFIER) {
            if (element.parent is JSCallExpression) {
                // Skip if the parent is a call expression since it is a function call, not a property reference
                return
            }

            val identifier = element.lastChild
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(identifier.textRange.startOffset, identifier.textRange.endOffset))
                .textAttributes(TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE)
                .create()
            return
        }
    }
}
