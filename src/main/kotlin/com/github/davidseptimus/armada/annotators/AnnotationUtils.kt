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