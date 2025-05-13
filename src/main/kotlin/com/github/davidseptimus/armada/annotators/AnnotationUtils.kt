package com.github.davidseptimus.armada.annotators

import com.intellij.psi.PsiElement


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