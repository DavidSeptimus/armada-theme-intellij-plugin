package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpEchoStatement
import com.jetbrains.php.lang.psi.elements.PhpGoto
import com.jetbrains.php.lang.psi.elements.PhpUse
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class PHPAnnotator : Annotator {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (annotateAliasIdentifierDeclaration(element, holder)) {
            return
        }
        if (annotateClassSelfReference(element, holder)) {
            return
        }
        if (annotateClassReference(element, holder)) {
            return
        }
        annotateGotoLabelIdentifier(element, holder)
    }

    private fun annotateGotoLabelIdentifier(
        element: PsiElement,
        holder: AnnotationHolder
    ) {
        when {
            element.elementType == PhpTokenTypes.IDENTIFIER && element.parent is PhpGoto -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.PHP_GOTO_LABEL_IDENTIFIER)
                    .create()
            }
        }
    }

    private fun annotateClassSelfReference(
        element: PsiElement,
        holder: AnnotationHolder
    ): Boolean {
        when {
            element.elementType == PhpTokenTypes.IDENTIFIER && element.text.equals("self") && element.parent is ClassReference -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.PHP_CLASS_SELF_REFERENCE)
                    .create()
                return true
            }
            else -> return false
        }
    }

    private fun annotateAliasIdentifierDeclaration(
        element: PsiElement,
        holder: AnnotationHolder,
    ): Boolean {
        when {
            element.elementType == PhpTokenTypes.IDENTIFIER && element.parent is PhpUse && isLastChild(element) -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.PHP_ALIAS_IDENTIFIER_DECLARATION)
                    .create()
                return true;
            }

            else -> return false
        }
    }

    private fun annotateClassReference(
        element: PsiElement,
        holder: AnnotationHolder,
    ): Boolean {
        when {
            element is ClassReference -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(element.textRange.startOffset, element.textRange.endOffset))
                    .textAttributes(TextAttributeKeys.PHP_CLASS_REFERENCE)
                    .create()
                return true;
            }

            else -> return false
        }
    }
}