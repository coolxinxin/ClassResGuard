package com.leos.superplugin.tasks

import com.leos.superplugin.entensions.GuardExtension
import com.leos.superplugin.utils.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
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
open class SuperXmlClassGuard @Inject constructor(
    private val guardExtension: GuardExtension,
) : DefaultTask() {

    private val pkg by lazy { project.manifestFile().findPackage() }

    init {
        group = "guard"
    }

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
                workFile(it)
            }
        }
    }

    private fun workFile(file: File) {
        val path = file.path.replace("\\", ".").removeSuffix()
        val startIndex = path.lastIndexOf("src.main.java.")
        if (startIndex == -1) {
            throw IllegalArgumentException("index exception, index can not be -1")
        }
        val oldName = file.name
        val newName = "${"Test"}${oldName}"
        val oldClassPath = path.substring(startIndex + 14, path.length).getDirPath()
        val newClassPath = "${oldClassPath}.$newName"
        println("workFile:file oldPath:$oldClassPath")
        println("workFile:file newPath:$newClassPath")
    }
}