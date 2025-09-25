package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.RsInnerAttr
import org.rust.lang.core.psi.RsMetaItem
import org.rust.lang.core.psi.RsMetaItemArgs
import org.rust.lang.core.psi.RsOuterAttr


class RustAnnotator : Annotator {
    val attributePunctuation = setOf("#", "!", "[", "]", "(", ")", ",", "=")

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        annotateMetadata(element, holder)
    }


    private fun annotateMetadata(element: PsiElement, holder: AnnotationHolder): Boolean {
        when {
            attributePunctuation.contains(element.text) && (element.parent is RsOuterAttr || element.parent is RsInnerAttr || element.parent is RsMetaItemArgs || element.parent is RsMetaItem ) -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.RUST_ATTRIBUTE_PUNCTUATION)
                    .create()
                return true
            }
        }
        return false
    }
}
