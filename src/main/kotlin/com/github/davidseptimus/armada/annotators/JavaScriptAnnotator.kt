package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.ecma6.JSTypedEntity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType


class JavaScriptAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
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

        if (element is JSReferenceExpression
            && !hasAncestor(element, JSTypedEntity::class.java) // skip type annotations
            && element.parent!is JSCallExpression // function rather than property reference
            && element.lastChild.elementType == JSTokenTypes.IDENTIFIER
            ){
            val identifier = element.lastChild
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(identifier.textRange.startOffset, identifier.textRange.endOffset))
                .textAttributes(TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE)
                .create()
            return
        }
    }
}
