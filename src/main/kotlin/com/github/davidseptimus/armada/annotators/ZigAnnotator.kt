package com.github.davidseptimus.armada.annotators

import com.falsepattern.zigbrains.zig.psi.*
import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class ZigAnnotator : BaseArmadaAnnotator() {

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {
        val isFieldOrParam = annotateFieldType(element, holder)
        if (!isFieldOrParam) {
            annotateReturnTypeIdentifiers(element, holder)
        }
    }

    private fun annotateFieldType(element: PsiElement, holder: AnnotationHolder): Boolean {
        when {
            // Field type in a struct or function argument type
            element is ZigPrimaryTypeExpr && (element.parent is ZigParamType || (element.parent is ZigContainerField && element.parent.parent is ZigContainerMembers)|| element.parent is ZigExprList) -> {
                val identifier = element.identifier
                if (identifier != null) {
                    doAnnotate(
                        identifier,
                        holder,
                        TextAttributeKeys.ZIG_FIELD_TYPE,
                    )
                    return true
                }
            }

            // e.g. field: ?*Node
            element is ZigTypeExpr && (element.parent is ZigContainerField || element.parent is ZigParamType) -> {
                val children = element.children.filterIsInstance<ZigPrimaryTypeExpr>()
                children.forEach { typeExpr ->
                    doAnnotate(
                        typeExpr,
                        holder,
                        TextAttributeKeys.ZIG_FIELD_TYPE,
                    )
                }
                return children.isNotEmpty()
            }

            element.text == "anytype" && element.parent is ZigParamType -> {
                doAnnotate(
                    element,
                    holder,
                    TextAttributeKeys.ZIG_FIELD_TYPE,
                )
                return true
            }
        }
        return false
    }

    private fun annotateReturnTypeIdentifiers(element: PsiElement, holder: AnnotationHolder) {
        when {
            // Simple function return type
            element is ZigPrimaryTypeExpr && element.parent is ZigFnProto -> {
                doAnnotate(
                    element,
                    holder,
                    TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER,
                )
            }

            element is ZigErrorUnionExpr && element.parent is ZigFnProto -> {
                handleErrorUnionExpr(element, holder, TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER)
            }

            element is ZigSuffixExpr && element.parent is ZigFnProto -> {
                handleSuffixExpr(element, holder, TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER)
            }
        }
    }

    private fun handleErrorUnionExpr(
        element: ZigErrorUnionExpr,
        holder: AnnotationHolder,
        textAttributesKey: TextAttributesKey
    ) {
        for (child in element.children) {
            when (child) {
                is ZigPrimaryTypeExpr -> {
                    handlePrimaryTypeExpr(child, holder, textAttributesKey)
                }

                is ZigSuffixExpr -> {
                    handleSuffixExpr(child, holder, textAttributesKey)
                }
            }
        }
    }

    private fun handleSuffixExpr(
        element: ZigSuffixExpr,
        holder: AnnotationHolder,
        textAttributesKey: TextAttributesKey,
        depth: Int = 0
    ) {
        if (depth > 10) {
            // Prevent infinite recursion
            return
        }
        for (child in element.children) {
            when (child) {
                is ZigPrimaryTypeExpr -> {
                    handlePrimaryTypeExpr(child, holder, textAttributesKey)
                }

                is ZigSuffixOp -> {
                    handleSuffixOp(child, holder, textAttributesKey)
                }

                is ZigSuffixExpr -> {
                    // Recursively handle nested suffix expressions
                    handleSuffixExpr(child, holder, textAttributesKey, depth + 1)
                }
            }
        }
    }

    private fun handlePrimaryTypeExpr(
        element: ZigPrimaryTypeExpr,
        holder: AnnotationHolder,
        textAttributesKey: TextAttributesKey
    ) {
        val identifier = element.identifier
        if (identifier != null) {
            doAnnotate(
                identifier,
                holder,
                textAttributesKey,
            )
        }
    }

    private fun handleSuffixOp(element: ZigSuffixOp, holder: AnnotationHolder, textAttributesKey: TextAttributesKey) {
        val identifier = element.identifier
        if (identifier != null) {
            doAnnotate(
                identifier,
                holder,
                textAttributesKey,
            )
        }
    }


    private fun doAnnotate(
        element: PsiElement,
        holder: AnnotationHolder,
        attribute: TextAttributesKey,
        severity: HighlightSeverity = HighlightSeverity.INFORMATION,
        range: TextRange? = null
    ) {
        var resolvedRange = range
        if (range == null) {
            resolvedRange = TextRange.create(element.textRange.startOffset, element.textRange.endOffset)
        }

        holder.newSilentAnnotation(severity)
            .range(resolvedRange)
            .textAttributes(attribute)
            .create()
    }
}
