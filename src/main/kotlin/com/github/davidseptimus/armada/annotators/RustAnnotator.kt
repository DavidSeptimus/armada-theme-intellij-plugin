package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.sql.dialects.redshift.RsTypes
import org.rust.lang.core.psi.RsInnerAttr
import org.rust.lang.core.psi.RsMetaItem
import org.rust.lang.core.psi.RsMetaItemArgs
import org.rust.lang.core.psi.RsOuterAttr
import org.rust.lang.core.psi.ext.elementType


class RustAnnotator : BaseArmadaAnnotator() {
    val attributePunctuation = setOf("#", "!", "[", "]", "(", ")", ",", "=")

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
       if  (annotateMetadata(element, holder)) return
        annotateBooleanLiteral(element, holder)
    }

    private fun annotateBooleanLiteral(
        element: PsiElement,
        holder: AnnotationHolder
    ): Boolean {
        if (element.elementType == RsTypes.RS_BOOLEAN_LITERAL) {
            highlightElement(element, holder, TextAttributeKeys.RUST_BOOLEAN)
            return true
        }
        return false
    }


    private fun annotateMetadata(element: PsiElement, holder: AnnotationHolder): Boolean {
        when {
            attributePunctuation.contains(element.text) && (element.parent is RsOuterAttr || element.parent is RsInnerAttr || element.parent is RsMetaItemArgs || element.parent is RsMetaItem ) -> {
                highlightElement(element, holder, TextAttributeKeys.RUST_ATTRIBUTE_PUNCTUATION)
                return true
            }
        }
        return false
    }
}
