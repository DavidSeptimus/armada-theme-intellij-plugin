package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.YAMLTokenTypes

class YamlAnnotator : Annotator {
    companion object {
        val aliasAttributes: TextAttributesKey = TextAttributeKeys.YAML_ALIAS
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when {
            // Highlight aliases (e.g., *params)
            element.node.elementType == YAMLTokenTypes.ALIAS -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange)
                    .textAttributes(aliasAttributes)
                    .create()
            }
        }
    }
}