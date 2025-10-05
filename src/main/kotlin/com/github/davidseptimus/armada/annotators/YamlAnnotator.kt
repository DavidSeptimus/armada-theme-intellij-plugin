package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.psi.YAMLAlias
import org.jetbrains.yaml.psi.YAMLQuotedText

class YamlAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        when {
            // Highlight aliases (e.g., *params)
            element is YAMLAlias -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.YAML_ALIAS)
                    .create()
            }
            element is YAMLQuotedText && element.text.startsWith("'") -> {
                // Highlight single-quoted strings
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.YAML_SINGLE_QUOTED_STRING)
                    .create()
            }
        }
    }
}