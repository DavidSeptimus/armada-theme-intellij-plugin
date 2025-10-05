package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.ArmadaAnnotationController
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

abstract class BaseArmadaAnnotator : Annotator {

    override fun isDumbAware(): Boolean = true

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!ArmadaAnnotationController.instance.shouldAnnotate()) {
            return
        }

        doAnnotate(element, holder)
    }

    /**
     * Perform the actual annotation logic.
     * Subclasses should override this method instead of annotate().
     */
    protected abstract fun doAnnotate(element: PsiElement, holder: AnnotationHolder)
}