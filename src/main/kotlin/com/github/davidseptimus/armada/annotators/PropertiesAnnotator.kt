
package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.properties.psi.impl.PropertyValueImpl
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement


class PropertiesAnnotator : Annotator {
    companion object {
        val stringLiteralValueAttributes: TextAttributesKey = TextAttributeKeys.PROPERTIES_STRING_LITERAL_VALUE
    }

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateStringLiteralValue(element, holder)
    }

    private fun annotateStringLiteralValue(element: PsiElement, holder: AnnotationHolder) {
            if (element is PropertyValueImpl && (element.text.startsWith("\"") || element.text.startsWith("'"))) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(stringLiteralValueAttributes)
                    .create()
            }
    }
}