import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class ThemeMergerExtension @Inject constructor(
    objects: ObjectFactory
) {
    val variants: NamedDomainObjectContainer<ThemeVariant> = objects.domainObjectContainer(ThemeVariant::class.java)

    fun variants(action: Action<in NamedDomainObjectContainer<ThemeVariant>>) {
        action.execute(variants)
    }
}

abstract class ThemeVariant @Inject constructor(
    private val name: String
) {
    abstract val baseTheme: Property<String>
    abstract val overrides: ListProperty<String>
    abstract val output: Property<String>
    abstract val taskGroup: Property<String>
    abstract val description: Property<String>

    init {
        taskGroup.convention("generate")
        overrides.convention(emptyList())
    }

    fun getName(): String = name

    fun overrides(vararg files: String) {
        overrides.set(files.toList())
    }
}