package com.github.davidseptimus.armada.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class ArmadaSettingsConfigurable : Configurable {

    private var disableSyntaxHighlightingCheckBox: JBCheckBox? = null
    private var enableForAllThemesCheckBox: JBCheckBox? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "Armada Theme"

    override fun createComponent(): JComponent {
        val description1 = JBLabel(
            "<html>Armada provides syntax highlighting enhancements for various languages<br>" +
            "(JavaScript, Python, PHP, etc.) to enable more precise highlighting of language tokens.</html>"
        )

        val description2 = JBLabel("By default, these enhancements only apply when using an Armada theme.")

        disableSyntaxHighlightingCheckBox = JBCheckBox("Disable syntax highlighting enhancements")
        enableForAllThemesCheckBox = JBCheckBox("Enable syntax highlighting enhancements in all color schemes")

        disableSyntaxHighlightingCheckBox?.addChangeListener {
            enableForAllThemesCheckBox?.isEnabled = !disableSyntaxHighlightingCheckBox!!.isSelected
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(description1)
            .addVerticalGap(6)
            .addComponent(description2)
            .addVerticalGap(10)
            .addComponent(disableSyntaxHighlightingCheckBox!!)
            .addComponent(enableForAllThemesCheckBox!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = ArmadaSettingsState.instance
        return disableSyntaxHighlightingCheckBox?.isSelected != settings.disableSyntaxHighlighting ||
                enableForAllThemesCheckBox?.isSelected != settings.enableForAllThemes
    }

    override fun apply() {
        val settings = ArmadaSettingsState.instance
        settings.disableSyntaxHighlighting = disableSyntaxHighlightingCheckBox?.isSelected ?: false
        settings.enableForAllThemes = enableForAllThemesCheckBox?.isSelected ?: false

        // Notify controller to recalculate annotation state
        ArmadaAnnotationController.instance.onSettingsChanged()
    }

    override fun reset() {
        val settings = ArmadaSettingsState.instance
        disableSyntaxHighlightingCheckBox?.isSelected = settings.disableSyntaxHighlighting
        enableForAllThemesCheckBox?.isSelected = settings.enableForAllThemes
        enableForAllThemesCheckBox?.isEnabled = !settings.disableSyntaxHighlighting
    }

    override fun disposeUIResources() {
        disableSyntaxHighlightingCheckBox = null
        enableForAllThemesCheckBox = null
    }
}