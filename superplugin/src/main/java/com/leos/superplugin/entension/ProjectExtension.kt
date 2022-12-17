package com.leos.superplugin.entension

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.gradle.api.Project
import java.io.File

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
 * @date : 2022/12/16 19:00
 * @desc :
 * @since : xinxiniscool@gmail.com
 */

fun Project.javaDir(path: String = ""): File = file("src/main/java/$path")

fun Project.resDir(path: String = ""): File = file("src/main/res/$path")

fun Project.layoutDir(path: String = ""): File = file("src/main/res/layout/$path")

fun Project.manifestFile(): File = file("src/main/AndroidManifest.xml")

fun findClassByManifest(text: String, classPaths: MutableList<String>, namespace: String?): String {
    val rootNode = XmlParser(false, false).parseText(text)
    val packageName = namespace ?: rootNode.attribute("package").toString()
    val nodeList = rootNode.get("application") as? NodeList ?: return packageName
    val applicationNode = nodeList.firstOrNull() as? Node ?: return packageName
    val application = applicationNode.attribute("android:name")?.toString()
    if (application != null) {
//        val classPath = if (application.startsWith(".")) packageName + application else application
        classPaths.add(application)
    }
    for (children in applicationNode.children()) {
        val childNode = children as? Node ?: continue
        val childName = childNode.name()
        if ("activity" == childName || "service" == childName ||
            "receiver" == childName || "provider" == childName
        ) {
            val name = childNode.attribute("android:name").toString()
//            val classPath = if (name.startsWith(".")) packageName + name else name
            classPaths.add(name)
        }
    }
    return packageName
}