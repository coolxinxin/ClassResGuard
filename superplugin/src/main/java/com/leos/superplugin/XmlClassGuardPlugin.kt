package com.leos.superplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.leos.superplugin.entensions.GuardExtension
import com.leos.superplugin.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: ljx
 * Date: 2022/2/25
 * Time: 19:03
 */
class XmlClassGuardPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkApplicationPlugin(project)
        val guardExtension = project.extensions.create("xmlClassGuard", GuardExtension::class.java)
        project.tasks.create("xmlClassGuard", XmlClassGuardTask::class.java, guardExtension)
        project.tasks.create("packageChange", PackageChangeTask::class.java, guardExtension)
        project.tasks.create("moveDir", MoveDirTask::class.java, guardExtension)
        project.tasks.create("xmlNameChangeGuard",
            XmlNameChangeGuardTask::class.java,
            guardExtension)
        project.tasks.create("garbageGuard", GarbageGuardTask::class.java, guardExtension)
        project.tasks.create("superXmlGuard", SuperXmlClassGuard::class.java, guardExtension)

        val android = project.extensions.getByName("android") as AppExtension
        project.afterEvaluate {
            if (guardExtension.findConstraintReferencedIds) {
                android.applicationVariants.all { variant ->
                    createFindConstraintReferencedIds(project, variant)
                }
            }
        }
    }

    private fun createFindConstraintReferencedIds(project: Project, variant: ApplicationVariant) {
        val variantName = variant.name.capitalize()
        val aabResGuardTaskName = "aabresguard$variantName"
        val aabResGuardTask = project.tasks.findByName(aabResGuardTaskName)
            ?: throw  GradleException("AabResGuard plugin required")
        val findConstraintReferencedIdsTaskName = "findConstraintReferencedIds"
        val findConstraintReferencedIdsTask =
            project.tasks.findByName(findConstraintReferencedIdsTaskName) ?: project.tasks.create(
                findConstraintReferencedIdsTaskName, FindConstraintReferencedIdsTask::class.java
            )
        aabResGuardTask.dependsOn(findConstraintReferencedIdsTask)
    }

    private fun checkApplicationPlugin(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw  GradleException("Android Application plugin required")
        }
    }
}