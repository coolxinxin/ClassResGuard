package com.leos.superplugin.tasks

import com.android.build.gradle.BaseExtension
import com.leos.superplugin.entension.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 *   █████▒█    ██  ▄████▄   ██ ▄█▀       ██████╗ ██╗   ██╗ ██████╗
 * ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝
 * ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗
 * ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║
 * ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝
 *  ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒       ╚═════╝  ╚═════╝  ╚═════╝
 *  ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
 *  ░ ░    ░░░ ░ ░ ░        ░ ░░ ░
 *           ░     ░ ░      ░  ░
 * @author : Leo
 * @date : 2022/12/16 19:57
 * @desc :
 * @since : xinxiniscool@gmail.com
 */
open class RenameClassGuardTask @Inject constructor(
    private val configExtension: ConfigExtension,
) : DefaultTask() {

    init {
        group = "guard"
    }

    private val random by lazy { Random() }

    @TaskAction
    fun execute() {
        workDir(project.javaDir())
    }

    private fun workDir(file: File) {
        val listFiles = file.listFiles()
        listFiles?.forEach {
            if (it.isDirectory) {
                workDir(it)
            } else {
                renameClass(it)
            }
        }
    }

    private fun renameClass(file: File) {
        val path = file.path.replace(File.separator, ".").removeSuffix()
        val suffix = file.name.getSuffix()
        if (configExtension.filterSuffixFiles.contains(suffix)){
            return
        }
        val javaPkg = "src.main.java."
        val kotlinPkg = "src.main.kotlin."
        var startIndex = path.lastIndexOf(javaPkg)
        var isJavaPkg = true
        if (startIndex == -1) {
            isJavaPkg = false
            startIndex = path.lastIndexOf(kotlinPkg)
            if (startIndex == -1) {
                throw IllegalArgumentException("Only src\\main\\java or src\\main\\kotlin directories are supported")
            }
        }
        val oldName = file.name.removeSuffix()
        if (oldName.isBlank()) {
            return
        }
        val classPrefixNameArray = configExtension.classPrefixName
        if (classPrefixNameArray.isEmpty()) {
            throw IllegalArgumentException("The classPrefixName has not been configured yet. Please configure the classPrefixName before running the task")
        }
        val classPrefixName = if (classPrefixNameArray.size == 1) {
            classPrefixNameArray[0]
        } else {
            classPrefixNameArray[random.nextInt(classPrefixNameArray.size)]
        }
        val newName = "$classPrefixName${oldName}"
        val length = if (isJavaPkg) javaPkg.length else kotlinPkg.length
        val oldClassPath = path.substring(startIndex + length, path.length)
        val oldClassDir = oldClassPath.getDirPath()
        val newClassPath = "${oldClassDir}.${newName}"
        file.renameTo(File(file.absolutePath.replace(oldName, newName)))
        obfuscateAllClass(project.javaDir(), oldClassPath, newClassPath, oldName, newName)
        obfuscateRes(oldClassPath, newClassPath, oldName)
    }

    private fun obfuscateRes(
        oldClassPath: String,
        newClassPath: String,
        oldName: String
    ) {
        val listFiles = project.resDir().listFiles { _, name ->
            name.startsWith("layout") || name.startsWith("navigation")
        }?.toMutableList() ?: return
        listFiles.add(project.manifestFile())
        project.files(listFiles).asFileTree.forEach { xmlFile ->
            val parentName = xmlFile.parentFile.name
            when {
                parentName.startsWith("navigation")
                        || parentName.startsWith("layout") -> {
                    xmlFile.writeText(
                        xmlFile.readText().replaceWords(oldClassPath, newClassPath)
                    )
                }
                xmlFile.name == "AndroidManifest.xml" -> {
                    val xmlContent = mutableListOf<String>()
                    var text = xmlFile.readText()
                    findClassByManifest(
                        text,
                        xmlContent,
                        (project.extensions.getByName("android") as BaseExtension).namespace
                    )
                    for (classPath in xmlContent) {
                        val className = classPath.getClassName()
                        if (className == oldName) {
                            text = text.replaceWords(classPath, newClassPath)
                        }
                    }
                    xmlFile.writeText(text)
                }
            }
        }
    }

    private fun obfuscateAllClass(
        file: File, oldClassPath: String,
        newClassPath: String, oldName: String, newName: String
    ) {
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                obfuscateAllClass(it, oldClassPath, newClassPath, oldName, newName)
            } else {
                replaceClassText(it, oldClassPath, newClassPath, oldName, newName)
            }
        }
    }

    private fun replaceClassText(
        file: File, oldClassPath: String,
        newClassPath: String, oldName: String, newName: String
    ) {
        val sb = StringBuilder()
        file.readLines().forEach {
            if (it.startsWith("import")) {
                sb.append(it.replaceWords(oldClassPath, newClassPath)).append("\n")
            } else {
                sb.append(it.replaceWords(oldName, newName)).append("\n")
            }
        }
        file.writeText(sb.toString())
    }
}