import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class ThemeMergerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("themeMerger", ThemeMergerExtension::class.java)

        // Register tasks after configuration
        project.afterEvaluate {
            val taskNames = mutableListOf<String>()

            extension.variants.forEach { variant ->
                val taskName = "generate${variant.getName().replaceFirstChar { it.uppercase() }}"
                taskNames.add(taskName)

                project.tasks.register(taskName, MergeThemeTask::class.java).configure {
                    group = variant.taskGroup.get()
                    description = variant.description.getOrElse("Generates the ${variant.getName()} theme variant")

                    // Collect all input files (base theme + overrides)
                    val inputs = mutableListOf<File>()
                    inputs.add(project.file(variant.baseTheme.get()))
                    variant.overrides.get().forEach { override ->
                        inputs.add(project.file(override))
                    }

                    inputFiles.set(inputs)
                    outputFile.set(project.file(variant.output.get()))
                }
            }

            // Create a task to generate all theme variants
            if (taskNames.isNotEmpty()) {
                project.tasks.register("generateAllThemes", Task::class.java).configure {
                    group = "generate"
                    description = "Generates all theme variants"

                    taskNames.forEach { taskName ->
                        dependsOn(taskName)
                    }

                    doLast {
                        logger.lifecycle("Generated all theme variants")
                    }
                }
            }
        }
    }
}