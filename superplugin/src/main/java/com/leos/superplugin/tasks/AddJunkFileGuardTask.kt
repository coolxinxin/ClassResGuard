package com.leos.superplugin.tasks

import com.leos.superplugin.entension.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import javax.inject.Inject
import javax.lang.model.element.Modifier

/**
 * @author: Leo
 * @time: 2022/11/18
 * @desc:
 */
open class AddJunkFileGuardTask @Inject constructor(
    private val configExtension: ConfigExtension,
) : DefaultTask() {

    init {
        group = "guard"
    }

    companion object {
        private val abc = "abcdefghijklmnopqrstuvwxyz".toCharArray()
        private val abcAndABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        private val ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        private val color = "0123456789abcdef".toCharArray()
        private val viewArray = arrayOf(
            "TextView",
            "RadioButton",
            "TimePicker",
            "SearchView",
            "CheckBox",
            "SeekBar",
            "SurfaceView",
            "Button",
            "EditText",
            "FrameLayout",
            "LinearLayout",
            "RelativeLayout",
            "ProgressBar",
            "Spinner"
        )
        private val whiteText = arrayListOf(
            "byte", "char", "boolean", "short", "int", "float", "long", "double",
            "val", "var", "for", "if", "companion", "class", "abstract", "default",
            "enum", "import", "package", "static", "void", "continue", "if", "else",
            "native", "public", "protected", "private", "instanceof", "final", "interface",
            "implements", "finally", "super", "strictfp", "return", "switch", "synchronized",
            "goto", "transient", "volatile", "throws", "while", "extends", "brake", "assert"
        )
    }

    private val random by lazy { Random() }


    @TaskAction
    fun execute() {
        handleResClass(project)
    }

    private fun handleResClass(project: Project) {
        val layoutDir = project.layoutDir()
        val javaDir = project.javaDir()
        val drawableDir = project.resDir("drawable")
        val xmlNameArray = ArrayList<String>()
        for (i in 0 until configExtension.layoutClassCount) {
            val xmlName = generateXmlName()
            xmlNameArray.add(xmlName)
            val layoutFile = File(layoutDir, "${xmlName}.xml")
            layoutFile.writeText(generateLayoutText())
        }
        val count = xmlNameArray.size
        for (i in 0 until configExtension.activityClassCount) {
            var j = i
            if (i >= count) {
                j = count - 1
            }
            generateActivityClass(
                configExtension.junkPackage,
                generateActivityName(),
                xmlNameArray[j],
                javaDir,
                project.manifestFile()
            )
        }
        for (i in 0 until configExtension.normalClassCount) {
            generateNormalClass(
                configExtension.junkPackage,
                generateActivityName(),
                javaDir
            )
        }
        for (i in 0 until configExtension.drawableClassCount) {
            generateDrawable(File(drawableDir, "${generateXmlName()}.xml"))
        }
        generateValueRes()
    }

    private fun generateValueRes() {
        var colorFile = project.resDir("values/colors.xml")
        if (!colorFile.exists()) {
            colorFile = project.resDir("values/color.xml")
        }
        for (i in 0 until configExtension.colorCount) {
            val generateColor = generateColor()
            val colorPrefixNameArray = configExtension.colorPrefixName
            if (colorPrefixNameArray.isEmpty()) {
                throw IllegalArgumentException("The colorPrefixName has not been configured yet. Please configure the colorPrefixName before running the task")
            }
            val colorPrefixName = if (colorPrefixNameArray.size == 1) {
                colorPrefixNameArray[0]
            } else {
                colorPrefixNameArray[random.nextInt(colorPrefixNameArray.size)]
            }
            val colorName = colorPrefixName.lowercase() + "_" + generateColor.replace("#", "")
            val color = "    <color name=\"${colorName}\">${generateColor}</color>"
            val reader = BufferedReader(FileReader(colorFile))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("</resources>")) {
                    sb.append(color + "\n")
                }
                sb.append(line + "\n")
            }
            colorFile.writeText(sb.toString())
        }
        var stringsFile = project.resDir("values/strings.xml")
        if (!stringsFile.exists()) {
            stringsFile = project.resDir("values/string.xml")
        }
        for (i in 0 until configExtension.stringsCount) {
            val generateText = generateText()
            val stringsPrefixNameArray = configExtension.stringsPrefixName
            val stringsPrefixName = if (stringsPrefixNameArray.isEmpty()) {
                ""
            } else if (stringsPrefixNameArray.size == 1) {
                stringsPrefixNameArray[0]
            } else {
                stringsPrefixNameArray[random.nextInt(stringsPrefixNameArray.size)]
            }
            val stringsName = if (stringsPrefixName.isEmpty()) {
                generateText
            } else {
                stringsPrefixName.lowercase() + "_" + generateText
            }
            val strings = "    <string name=\"${stringsName}\">${generateText}</string>"
            val reader = BufferedReader(FileReader(stringsFile))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("</resources>")) {
                    sb.append(strings + "\n")
                }
                sb.append(line + "\n")
            }
            stringsFile.writeText(sb.toString())
        }
    }

    private fun generateDrawable(file: File) {
        val drawable =
            "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" + "    xmlns:aapt=\"http://schemas.android.com/aapt\"\n" + "    android:width=\"108dp\"\n" + "    android:height=\"108dp\"\n" + "    android:viewportWidth=\"108\"\n" + "    android:viewportHeight=\"108\">\n" + "    <path\n" + "        android:fillType=\"evenOdd\"\n" + "        android:pathData=\"M32,64C32,64 38.39,52.99 44.13,50.95C51.37,48.37 70.14,49.57 70.14,49.57L108.26,87.69L108,109.01L75.97,107.97L32,64Z\"\n" + "        android:strokeWidth=\"1\"\n" + "        android:strokeColor=\"#00000000\">\n" + "        <aapt:attr name=\"android:fillColor\">\n" + "            <gradient\n" + "                android:endX=\"78.5885\"\n" + "                android:endY=\"90.9159\"\n" + "                android:startX=\"48.7653\"\n" + "                android:startY=\"61.0927\"\n" + "                android:type=\"linear\">\n" + "                <item\n" + "                    android:color=\"#44000000\"\n" + "                    android:offset=\"0.0\" />\n" + "                <item\n" + "                    android:color=\"#00000000\"\n" + "                    android:offset=\"1.0\" />\n" + "            </gradient>\n" + "        </aapt:attr>\n" + "    </path>\n" + "    <path\n" + "        android:fillColor=\"%s\"\n" + "        android:fillType=\"nonZero\"\n" + "        android:pathData=\"M66.94,46.02L66.94,46.02C72.44,50.07 76,56.61 76,64L32,64C32,56.61 35.56,50.11 40.98,46.06L36.18,41.19C35.45,40.45 35.45,39.3 36.18,38.56C36.91,37.81 38.05,37.81 38.78,38.56L44.25,44.05C47.18,42.57 50.48,41.71 54,41.71C57.48,41.71 60.78,42.57 63.68,44.05L69.11,38.56C69.84,37.81 70.98,37.81 71.71,38.56C72.44,39.3 72.44,40.45 71.71,41.19L66.94,46.02ZM62.94,56.92C64.08,56.92 65,56.01 65,54.88C65,53.76 64.08,52.85 62.94,52.85C61.8,52.85 60.88,53.76 60.88,54.88C60.88,56.01 61.8,56.92 62.94,56.92ZM45.06,56.92C46.2,56.92 47.13,56.01 47.13,54.88C47.13,53.76 46.2,52.85 45.06,52.85C43.92,52.85 43,53.76 43,54.88C43,56.01 43.92,56.92 45.06,56.92Z\"\n" + "        android:strokeWidth=\"1\"\n" + "        android:strokeColor=\"#00000000\" />\n" + "</vector>"
        val drawableText = String.format(drawable, generateColor())
        file.writeText(drawableText)
    }

    private fun generateColor(): String {
        val sb = StringBuffer()
        sb.append("#")
        for (i in 0..5) {
            sb.append(color[random.nextInt(color.size)])
        }
        return sb.toString()
    }

    private fun generateNormalClass(pkgName: String, className: String, file: File) {
        val typeBuilder = TypeSpec.classBuilder(className)
        typeBuilder.addModifiers(Modifier.PUBLIC)
        for (i in 0 until configExtension.normalClassMethodCount) {
            val methodName = generateText()
            val methodBuilder = MethodSpec.methodBuilder(methodName)
            generateMethods(methodBuilder)
            typeBuilder.addMethod(methodBuilder.addModifiers(Modifier.PRIVATE).build())
        }
        val fileBuilder = JavaFile.builder(pkgName, typeBuilder.build())
        fileBuilder.build().writeTo(file)
    }

    private fun generateActivityClass(
        pkgName: String, className: String, xmlName: String, file: File,
        manifestFile: File,
    ) {
        val typeBuilder = TypeSpec.classBuilder(className)
        typeBuilder.superclass(ClassName.get("android.app", "Activity"))
        typeBuilder.addModifiers(Modifier.PUBLIC)
        val bundleClassName = ClassName.get("android.os", "Bundle")
        val rClassname = ClassName.get(configExtension.junkResPackage, "R")
        typeBuilder.addMethod(
            MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override::class.java).addModifiers(Modifier.PROTECTED)
                .addParameter(bundleClassName, "savedInstanceState")
                .addStatement("super.onCreate(savedInstanceState)")
                .addStatement("setContentView(\$T.layout.${xmlName})", rClassname).build()
        )
        for (i in 0 until configExtension.activityClassMethodCount) {
            val methodName = generateText()
            val methodBuilder = MethodSpec.methodBuilder(methodName)
            generateMethods(methodBuilder)
            typeBuilder.addMethod(methodBuilder.addModifiers(Modifier.PUBLIC).build())
        }
        val fileBuilder = JavaFile.builder(pkgName, typeBuilder.build())
        fileBuilder.build().writeTo(file)
        addToManifestByFileIo(manifestFile, pkgName, className)
    }

    private fun addToManifestByFileIo(file: File, pkgName: String, className: String) {
        val activity =
            "        <activity android:name=\"%s.%s\" \n            android:exported=\"false\" \n            android:launchMode=\"%s\" \n            android:screenOrientation=\"portrait\">\n" + "        </activity>\n"
        val reader = BufferedReader(FileReader(file))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            if (line!!.contains("</application>")) {
                sb.append(
                    String.format(
                        activity,
                        pkgName,
                        className,
                        generateActivityLaunchMode()
                    ) + "\n"
                )
            }
            sb.append(line + "\n")
        }
        file.writeText(sb.toString())
    }

    private fun generateActivityLaunchMode(): String {
        val launchMode = arrayOf("singleTask", "singleInstance", "singleTop", "standard")
        return launchMode[random.nextInt(launchMode.size)]
    }

    private fun generateMethods(methodBuilder: MethodSpec.Builder) {
        when (random.nextInt(3)) {
            0 -> {
                val s1Value = generateText()
                val s2Value = generateText()
                val s1Text = getStringText(getStringText1())
                methodBuilder.addCode("String $s1Text = \"$s1Value\";\n")
                val s2Text = getStringText(getStringText1())
                methodBuilder.addCode("String $s2Text = \"${s2Value}\";\n")
                val s3Desc = getStringText(getStringText1())
                methodBuilder.addCode("String $s3Desc = $s1Text + $s2Text;\n")
                methodBuilder.addCode("System.out.println(${s3Desc});\n")
            }
            1 -> {
                val i = random.nextInt(100)
                val desc = getStringText(getStringText1())
                methodBuilder.addCode(
                    "int $desc = 0;\n"
                            + "for (int i = 0; i < $i; i++) {\n"
                            + "  $desc += i;\n"
                            + "}\n"
                )
                methodBuilder.addCode("System.out.println($desc);\n")
            }
            else -> {
                val s1Value = random.nextInt(100)
                val s2Value = random.nextInt(100)
                val s1Text = getIntText(getIntText1())
                val s2Text = getIntText(getIntText1())
                methodBuilder.addCode("int $s1Text = $s1Value; \n")
                methodBuilder.addCode("int $s2Text = $s2Value; \n")
                val s3Desc = getIntText(getIntText1())
                methodBuilder.addCode("int $s3Desc = $s1Text + $s2Text; \n")
                methodBuilder.addCode("System.out.println(${s3Desc});\n")
            }
        }
    }

    private fun getStringText(text: String): String {
        val name = if (whiteText.contains(name)) {
            getStringText(getStringText1())
        } else {
            text
        }
        return name
    }

    private fun getStringText1(): String {
        val name = StringBuilder()
        for (i in 0..10) {
            name.append(abc[random.nextInt(abc.size)])
        }
        return name.toString()
    }

    private fun getIntText(text: String): String {
        val name = if (whiteText.contains(name)) {
            getIntText(getIntText1())
        } else {
            text
        }
        return name
    }

    private fun getIntText1(): String {
        val name = StringBuilder()
        for (i in 0..8) {
            name.append(abc[random.nextInt(abc.size)])
        }
        return name.toString()
    }

    private fun generateLayoutText(): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\"\n    android:orientation=\"vertical\">\n")
        for (i in 0 until configExtension.layoutClassMethodCount) {
            sb.append(generateLayoutChildView()).append("\n")
        }
        sb.append("</LinearLayout>")
        return sb.toString()
    }

    private fun generateLayoutChildView(): String {
        val sb = StringBuilder()
        sb.append("<").append(viewArray[random.nextInt(viewArray.size)]).append(" ")
            .append("android:id=\"@+id/").append("${generateText()}\"").append(" ")
            .append("android:layout_width=\"").append(random.nextInt(150).toString()).append("dp\"")
            .append(" ").append("android:layout_height=\"").append(random.nextInt(80).toString())
            .append("dp\"").append("/>")
        return sb.toString()
    }

    private fun generateActivityName(): String {
        val up = ABC[random.nextInt(ABC.size)]
        val name = StringBuilder()
        name.append(up)
        for (i in 0..24) {
            name.append(abcAndABC[random.nextInt(abcAndABC.size)])
        }
        return name.toString()
    }

    private fun generateXmlName(): String {
        val name = StringBuilder()
        for (i in 0..24) {
            name.append(abc[random.nextInt(abc.size)])
        }
        return name.toString()
    }

    private fun generateText(): String {
        val name = StringBuilder()
        for (i in 0..15) {
            name.append(abc[random.nextInt(abc.size)])
        }
        return name.toString()
    }

}
