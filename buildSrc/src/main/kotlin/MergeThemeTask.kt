import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import buildscripts.ThemeMerger
import java.io.File

@CacheableTask
abstract class MergeThemeTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun merge() {
        val inputs = inputFiles.get().toTypedArray()
        val output = outputFile.asFile.get()

        logger.lifecycle("Merging ${inputs.size} theme files into ${output.name}")

        val merged = ThemeMerger.mergeThemes(*inputs)
        ThemeMerger.writeTheme(merged, output)

        logger.lifecycle("Generated theme: ${output.absolutePath}")
    }
}