package com.leos.superplugin.entensions

import java.io.File

/**
 * User: ljx
 * Date: 2022/3/2
 * Time: 12:46
 */
open class GuardExtension {

    /*
     * 是否查找约束布局的constraint_referenced_ids属性的值，并添加到AabResGuard的白名单中，
     * 是的话，要求你在XmlClassGuard前依赖AabResGuard插件，默认false
     */
    var findConstraintReferencedIds = false

    var prefixName = ""

    var changeXmlPkg: Array<String>? = null

    var mappingFile: File? = null

    var packageChange = HashMap<String, String>()

    var moveDir = HashMap<String, String>()

    var classMethodCount = 3

    var packageName = "com.leos.superplugin"

    var activityClassCount = 10

    var layoutClassCount = 10

    var layoutClassMethodCount = 10

    var drawableClassCount = 10
}