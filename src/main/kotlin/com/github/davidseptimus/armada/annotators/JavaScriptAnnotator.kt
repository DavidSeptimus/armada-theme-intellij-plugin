
package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType


class JavaScriptAnnotator : Annotator {
    companion object {
        val propertyReferenceAttributes: TextAttributesKey = TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotatePropertyReference(element, holder)
    }

    private fun annotatePropertyReference(element: PsiElement, holder: AnnotationHolder) {

        if (element is JSReferenceExpression && element.lastChild is  PsiElement && element.lastChild.elementType == JSTokenTypes.IDENTIFIER) {
           if (element.parent is JSCallExpression) {
                // Skip if the parent is a call expression since it is a function call, not a property reference
                return
            }

            val identifier = element.lastChild
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(identifier.textRange.startOffset, identifier.textRange.endOffset))
                .textAttributes(propertyReferenceAttributes)
                .create()
            return
        }
    }
}