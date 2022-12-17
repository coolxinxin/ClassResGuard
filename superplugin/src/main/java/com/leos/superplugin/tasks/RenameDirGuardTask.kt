package com.leos.superplugin.tasks

import com.leos.superplugin.entension.ConfigExtension
import com.leos.superplugin.entension.getDirName
import com.leos.superplugin.entension.javaDir
import com.leos.superplugin.entension.manifestFile
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
        listFiles?.forEach {
            if (it.isDirectory) {
                workDir(it)
            } else {
                renameDir(it)
            }
        }
    }

    private fun renameDir(file: File) {
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
        val newName = "${dirPrefixName.lowercase()}$oldName"
        val newDirPath = file.parentFile.parent + "\\${newName}"
        file.renameTo(File(newDirPath))
        val sb = StringBuilder()
        var oldPackage = ""
        var newPackage = ""
        file.readLines().forEach {
            if (it.startsWith("package")) {
                oldPackage = it
                sb.append(it.replace(oldName, newName).apply {
                    newPackage = this
                }).append("\n")
            } else {
                sb.append(it).append("\n")
            }
        }
        file.writeText(sb.toString())
        val manifestFile = project.manifestFile()
        manifestFile.writeText(manifestFile.readText().replace(oldPackage, newPackage))
    }

}