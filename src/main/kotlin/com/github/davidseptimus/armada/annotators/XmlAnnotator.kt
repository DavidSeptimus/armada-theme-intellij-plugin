package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlDoctype
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType


class XmlAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        annotateDocType(element, holder)
    }

    private fun annotateDocType(element: PsiElement, holder: AnnotationHolder) {
        if (element !is XmlToken || element.parent !is XmlDoctype) {
            return
        }

        if (element.tokenType == XmlTokenType.XML_DOCTYPE_START
            || element.tokenType == XmlTokenType.XML_DOCTYPE_END
            || element.tokenType == XmlTokenType.XML_DOCTYPE_SYSTEM
            || element.tokenType == XmlTokenType.XML_DOCTYPE_PUBLIC
        ) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                .textAttributes(TextAttributeKeys.XML_DOCTYPE)
                .create()
        }
    }
}
