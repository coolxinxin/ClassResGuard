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
 * @date : 2022/12/17 10:48
 * @desc :
 * @since : xinxiniscool@gmail.com
 */
open class RenameDirGuardTask @Inject constructor(
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
        listFiles?.forEachIndexed { index, it ->
            if (it.isDirectory) {
                workDir(it)
            } else {
                renameDir(it, index == listFiles.size - 1)
            }
        }
    }

    private fun renameDir(file: File, needRename: Boolean) {
        val oldName = file.parent.getDirName()
        val dirPrefixNameArray = configExtension.dirPrefixName
        if (dirPrefixNameArray.isEmpty()) {
            throw IllegalArgumentException("The dirPrefixName has not been configured yet. Please configure the dirPrefixName before running the task")
        }
        val dirPrefixName = if (dirPrefixNameArray.size == 1) {
            dirPrefixNameArray[0]
        } else {
            dirPrefixNameArray[random.nextInt(dirPrefixNameArray.size)]
        }
        val startIndex = file.parent.replace("\\", ".").lastIndexOf("src.main.java.")
        if (startIndex == -1) {
            throw IllegalArgumentException("index exception, index can not be -1")
        }
        val newName = "${dirPrefixName.lowercase()}$oldName"
        val newDirPath = file.parentFile.parent + "\\${newName}"
        val oldClassPath =
            file.parent.replace("\\", ".").substring(startIndex + 14, file.parent.length)
        val newClassPath =
            newDirPath.replace("\\", ".").substring(startIndex + 14, newDirPath.length)
        obfuscateAllClass(project.javaDir(), oldClassPath, newClassPath)
        val manifestFile = project.manifestFile()
        val xmlContent = mutableListOf<String>()
        var text = manifestFile.readText()
        findClassByManifest(
            text,
            xmlContent,
            (project.extensions.getByName("android") as BaseExtension).namespace
        )
        for (classPath in xmlContent) {
            val removeClass = classPath.substring(0, classPath.lastIndexOf("."))
            val dirName = removeClass.getClassName()
            if (dirName == oldName) {
                text = text.replace(classPath, "${newClassPath}.${classPath.getClassName()}")
            }
        }
        manifestFile.writeText(text)
        if (needRename)
            file.parentFile.renameTo(File(newDirPath))
    }

    private fun obfuscateAllClass(file: File, oldClassPath: String, newClassPath: String) {
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                obfuscateAllClass(it, oldClassPath, newClassPath)
            } else {
                replaceClassText(it, oldClassPath, newClassPath)
            }
        }
    }

    private fun replaceClassText(file: File, oldClassPath: String, newClassPath: String) {
        val sb = StringBuilder()
        file.readLines().forEach {
            if (it.startsWith("package")) {
                sb.append(it.replace(oldClassPath, newClassPath)).append("\n")
            } else if (it.startsWith("import")
                && it.contains(configExtension.dirPackage)
            ) {
                sb.append(it.replaceFirst(oldClassPath, newClassPath)).append("\n")
            } else {
                sb.append(it).append("\n")
            }
        }
        file.writeText(sb.toString())
    }
}