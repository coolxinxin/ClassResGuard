package com.leos.superplugin.entension

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
 * @date : 2022/12/16 19:08
 * @desc :
 * @since : xinxiniscool@gmail.com
 */
open class ConfigExtension {

    var classPrefixName: Array<String> = arrayOf("")

    var dirPrefixName: Array<String> = arrayOf("")

    var resPrefixName: Array<String> = arrayOf("")

    var filterSuffixFiles: Array<String> = arrayOf("")

    var changeResDir: Array<String>? = null

    var junkPackage = "com.leos.superplugin"

    var junkResPackage = "com.leos.superplugin"

    var activityClassMethodCount = 0

    var activityClassCount = 0

    var normalClassCount = 0

    var normalClassMethodCount = 0

    var layoutClassCount = 0

    var layoutClassMethodCount = 0

    var drawableClassCount = 0

    var colorCount = 0

    var stringsCount = 0

    var colorPrefixName: Array<String> = arrayOf("")

    var stringsPrefixName: Array<String> = arrayOf("")

}