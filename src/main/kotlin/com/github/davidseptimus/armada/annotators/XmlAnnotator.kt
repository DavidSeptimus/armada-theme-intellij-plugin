
package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType


class XmlAnnotator : Annotator {
    companion object {
        val doctypeAttributes: TextAttributesKey = TextAttributeKeys.XML_DOCTYPE
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateDocType(element, holder)
    }

    private fun annotateDocType(element: PsiElement, holder: AnnotationHolder) {
            if (element is XmlToken && element.tokenType == XmlTokenType.XML_DOCTYPE_START) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(doctypeAttributes)
                    .create()
            return
            }

            if (element is XmlToken && element.tokenType == XmlTokenType.XML_DOCTYPE_END) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(doctypeAttributes)
                    .create()
                return
            }

            if (element is XmlToken && element.tokenType == XmlTokenType.XML_DOCTYPE_SYSTEM) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(doctypeAttributes)
                    .create()
                return
            }
    }
}