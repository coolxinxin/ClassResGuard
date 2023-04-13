package com.leos.superplugin.tasks

import com.leos.superplugin.entension.*
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * @author: Leo
 * @time: 2022/11/17
 * @desc:
 */
open class RenameResGuardTask @Inject constructor(
    private val configExtension: ConfigExtension,
) : DefaultTask() {

    init {
        group = "guard"
    }

    private val random by lazy { Random() }

    @TaskAction
    fun execute() {
        handleResClass(project)
    }

    private fun handleResClass(project: Project) {
        val pathArray = configExtension.changeResDir
            ?: throw IllegalArgumentException("The changeXmlPkg has not been configured yet. Please configure the changeXmlPkg before running the task")
        pathArray.forEach {
            val resDir = project.resDir(it)
            renameFile(it, resDir)
        }
    }

    private fun renameFile(path: String, file: File) {
        val listFiles = file.listFiles()
        if (file.exists()) {
            listFiles?.forEach {
                val xmlPrefixNameArray = configExtension.resPrefixName
                if (xmlPrefixNameArray.isEmpty()) {
                    throw IllegalArgumentException("The xmlPrefixName has not been configured yet. Please configure the xmlPrefixName before running the task")
                }
                val xmlPrefixName = if (xmlPrefixNameArray.size == 1) {
                    xmlPrefixNameArray[0]
                } else {
                    xmlPrefixNameArray[random.nextInt(xmlPrefixNameArray.size)]
                }
                val newFileName = "${xmlPrefixName.lowercase()}_${it.name}"
                it.renameTo(File("${file.absolutePath}/${newFileName}"))
                if (path.startsWith("mipmap") || path.startsWith("drawable") || path.startsWith("layout")
                    || name.startsWith("navigation")
                ) {
                    val oldName = it.name.substring(0, it.name.indexOf("."))
                    val newName = newFileName.substring(0, newFileName.indexOf("."))
                    val resFileList = project.resDir().listFiles { _, name ->
                        name.startsWith("layout") || name.startsWith("navigation")
                                || name.startsWith("drawable")
                    }?.toMutableList() ?: return
                    project.files(resFileList).asFileTree.forEach { xmlFile ->
                        if (xmlFile.path.getClassName().lowercase().contains("xml")){
                            replaceXmlText(oldName, newName, xmlFile)
                        }
                    }
                    obfuscateAllClass(project.javaDir(), oldName, newName, path)
                }
            }
        }
    }

    private fun generateBinding(name: String): String {
        var newName = ""
        val nameSplit = name.split("_")
        var change: String
        for (i in nameSplit.indices) {
            val smallName = nameSplit[i]
            val first = smallName.substring(0, 1).uppercase()
            change = first + smallName.substring(1)
            newName += change
        }
        return newName + "Binding"
    }

    private fun obfuscateAllClass(file: File, oldName: String, newName: String, path: String) {
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                obfuscateAllClass(it, oldName, newName, path)
            } else {
                replaceClassText(it, oldName, newName, path.startsWith("layout"))
            }
        }
    }

    private fun replaceClassText(
        file: File,
        oldName: String,
        newName: String,
        isNeedChangeBinding: Boolean
    ) {
        val sb = StringBuilder()
        file.readLines().forEach {
            if (it.contains("R.layout")) {
                sb.append(it.replaceWords("R.layout.$oldName", "R.layout.$newName")).append("\n")
            } else if (it.contains("R.mipmap")) {
                sb.append(it.replaceWords("R.mipmap.$oldName", "R.mipmap.$newName")).append("\n")
            } else if (it.contains("R.drawable")) {
                sb.append(it.replaceWords("R.drawable.$oldName", "R.drawable.$newName"))
                    .append("\n")
            } else if (it.contains("R.navigation")) {
                sb.append(it.replaceWords("R.navigation.$oldName", "R.navigation.$newName"))
                    .append("\n")
            } else {
                sb.append(it).append("\n")
            }
        }
        if (isNeedChangeBinding) {
            val oldBinding = generateBinding(oldName)
            val newBinding = generateBinding(newName)
            file.writeText(sb.toString().replace(oldBinding, newBinding))
        } else {
            file.writeText(sb.toString())
        }
    }

    private fun replaceXmlText(oldName: String, newName: String, file: File) {
        val originalText = file.readText()
        val newText = originalText.replaceWords(oldName, newName)
        file.writeText(newText)
    }
}