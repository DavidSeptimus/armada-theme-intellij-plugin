package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.css.impl.CssElementTypes
import com.intellij.psi.util.elementType

class CSSAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        when (element.elementType) {
            CssElementTypes.CSS_PERCENT -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.CSS_PERCENT)
                    .create()
            }
            CssElementTypes.CSS_IDENT -> {
                if (element.parent.elementType == CssElementTypes.CSS_TERM) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                        .textAttributes(TextAttributeKeys.CSS_IDENTIFIER_TERM)
                        .create()
                }
            }
        }
    }
}