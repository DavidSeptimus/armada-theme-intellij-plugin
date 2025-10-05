package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.sh.psi.ShLetCommand
import com.intellij.sh.psi.ShSubshellCommand


class ShellAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        when {
            element.text.equals("let") && element.parent is ShLetCommand && element.parent.firstChild == element -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.BASH_LET_COMMAND)
                    .create()
            }

            element.text.equals("(") && element.parent is ShSubshellCommand && element.parent.firstChild == element -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.BASH_SUBSHELL_COMMAND_PARENTHESES)
                    .create()
            }
            element.text.equals(")") && element.parent is ShSubshellCommand && element.parent.lastChild == element -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.BASH_SUBSHELL_COMMAND_PARENTHESES)
                    .create()
            }
        }
    }
}
