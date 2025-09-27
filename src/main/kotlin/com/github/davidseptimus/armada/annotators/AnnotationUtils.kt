package com.github.davidseptimus.armada.annotators

import com.intellij.psi.PsiElement


fun hasAncestor(element: PsiElement, ancestorPredicate: (PsiElement) -> Boolean): Boolean {
    return getAncestor(element, ancestorPredicate) != null
}

fun getAncestor(element: PsiElement, ancestorPredicate: (PsiElement) -> Boolean): PsiElement? {
    var current: PsiElement? = element
    while (current != null) {
        if (ancestorPredicate(current)) {
            return current
        }
        current = current.parent
    }
    return null
}

fun hasAncestor(element: PsiElement, ancestorType: Class<out PsiElement>): Boolean {
    return getAncestor(element, ancestorType) != null
}

fun getAncestor(element: PsiElement, ancestorType: Class<out PsiElement>): PsiElement? {
    var current: PsiElement? = element
    while (current != null) {
        if (ancestorType.isInstance(current)) {
            return current
        }
        current = current.parent
    }
    return null
}

fun hasSibling(element: PsiElement, siblingType: Class<out PsiElement>, lookBehind: Boolean = false): Boolean {
    return getNearestSibling(element, siblingType, lookBehind) != null
}

fun getNearestSibling(
    element: PsiElement,
    siblingType: Class<out PsiElement>,
    lookBehind: Boolean
): PsiElement? {
    var current: PsiElement? = if (lookBehind) element.prevSibling else element.nextSibling
    while (current != null) {
        if (siblingType.isInstance(current)) {
            return current
        }
        current = if (lookBehind) current.prevSibling else current.nextSibling
    }
    return null
}

fun hasSibling(element: PsiElement, siblingPredicate: (PsiElement) -> Boolean, lookBehind: Boolean = false): Boolean {
    return getNearestSibling(element, siblingPredicate, lookBehind) != null
}

fun getNearestSibling(
    element: PsiElement,
    siblingPredicate: (PsiElement) -> Boolean,
    lookBehind: Boolean
): PsiElement? {
    var current: PsiElement? = if (lookBehind) element.prevSibling else element.nextSibling
    while (current != null) {
        if (siblingPredicate(current)) {
            return current
        }
        current = if (lookBehind) current.prevSibling else current.nextSibling
    }
    return null
}

/**
 * Returns the first sibling of the specified element (starting from the parent's first child).
 *
 * @param element The element whose first sibling is to be returned.
 * @return The first sibling of the specified element, or null if it has no siblings.
 */
fun firstSibling(element: PsiElement): PsiElement? {
    val parent = element.parent
    return if (parent != null && parent.firstChild != element) {
        parent.firstChild
    } else {
        null
    }
}

fun isLastChild(element: PsiElement): Boolean {
    val parent = element.parent
    return parent != null && parent.lastChild == element
}

fun highlightElement(
    element: PsiElement,
    holder: com.intellij.lang.annotation.AnnotationHolder,
    textAttributesKey: com.intellij.openapi.editor.colors.TextAttributesKey
) {
    holder.newSilentAnnotation(com.intellij.lang.annotation.HighlightSeverity.INFORMATION)
        .range(com.intellij.openapi.util.TextRange(element.textRange.startOffset, element.textRange.endOffset))
        .textAttributes(textAttributesKey)
        .create()
}