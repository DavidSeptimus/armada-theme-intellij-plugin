package com.github.davidseptimus.armada.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme

@Service
class ArmadaAnnotationController : EditorColorsListener {

    @Volatile
    private var annotationsEnabled: Boolean = calculateEnabled()

    init {
        val connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(EditorColorsManager.TOPIC, this)
    }

    fun shouldAnnotate(): Boolean = annotationsEnabled

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        annotationsEnabled = calculateEnabled()
    }

    fun onSettingsChanged() {
        annotationsEnabled = calculateEnabled()
    }

    private fun calculateEnabled(): Boolean {
        val settings = ArmadaSettingsState.instance

        if (settings.disableSyntaxHighlighting) {
            return false
        }

        if (settings.enableForAllThemes) {
            return true
        }

        return isArmadaColorScheme()
    }

    private fun isArmadaColorScheme(): Boolean {
        val currentScheme = EditorColorsManager.getInstance().globalScheme
        return currentScheme.name.contains("armada", ignoreCase = true)
    }

    companion object {
        val instance: ArmadaAnnotationController
            get() = ApplicationManager.getApplication().getService(ArmadaAnnotationController::class.java)
    }
}