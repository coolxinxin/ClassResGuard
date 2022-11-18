package com.leos.superplugin.tasks

import com.leos.superplugin.entensions.GuardExtension
import com.leos.superplugin.utils.allDependencyAndroidProjects
import com.leos.superplugin.utils.layoutDir
import com.leos.superplugin.utils.resDir
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * @author: Leo
 * @time: 2022/11/17
 * @desc:
 */
open class XmlNameChangeGuardTask @Inject constructor(
    private val guardExtension: GuardExtension,
) : DefaultTask() {

    init {
        group = "guard"
    }

    @TaskAction
    fun execute() {
        val androidProjects = allDependencyAndroidProjects()
        androidProjects.forEach { handleResClass(it) }
    }

    private fun handleResClass(project: Project) {
        val pathArray = guardExtension.changeXmlPkg ?: return
        pathArray.forEach {
            val resDir = project.resDir(it)
            renameFile(it, resDir, project.layoutDir())
        }
    }

    private fun renameFile(path: String, file: File, layoutDir: File) {
        val listFiles = file.listFiles()
        if (file.exists()) {
            listFiles?.forEach {
                val newFileName = "${guardExtension.prefixName.lowercase()}_${it.name}"
                it.renameTo(File("${file.absolutePath}/${newFileName}"))
                if (path.contains("mipmap") || path.contains("drawable") || path.contains("layout")) {
                    val listLayoutFile = layoutDir.listFiles()
                    if (layoutDir.exists()) {
                        listLayoutFile?.forEach { layoutFile ->
                            val oldName = it.name.substring(0, it.name.indexOf("."))
                            val newName = newFileName.substring(0, newFileName.indexOf("."))
                            replaceNameText(oldName, newName, layoutFile)
                        }
                    }
                }
            }
        }
    }

    private fun replaceNameText(oldName: String, newName: String, layoutFile: File) {
        val originalText = layoutFile.readText()
        val newText = originalText.replace(oldName, newName)
        layoutFile.writeText(newText)
    }
}