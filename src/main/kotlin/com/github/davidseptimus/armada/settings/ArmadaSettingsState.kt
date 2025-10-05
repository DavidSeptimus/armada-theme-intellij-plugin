package com.github.davidseptimus.armada.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.davidseptimus.armada.settings.ArmadaSettingsState",
    storages = [Storage("ArmadaSettings.xml")]
)
class ArmadaSettingsState : PersistentStateComponent<ArmadaSettingsState> {

    var disableSyntaxHighlighting: Boolean = false
    var enableForAllThemes: Boolean = false

    override fun getState(): ArmadaSettingsState = this

    override fun loadState(state: ArmadaSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: ArmadaSettingsState
            get() = ApplicationManager.getApplication().getService(ArmadaSettingsState::class.java)
    }
}