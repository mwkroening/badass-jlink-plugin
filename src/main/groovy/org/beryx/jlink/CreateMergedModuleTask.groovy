/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.jlink

import org.beryx.jlink.data.JlinkPluginExtension
import org.beryx.jlink.impl.CreateMergedModuleTaskImpl
import org.beryx.jlink.data.ModuleInfo
import org.beryx.jlink.data.CreateMergedModuleTaskData
import org.beryx.jlink.util.PathUtil
import org.beryx.jlink.util.Util
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class CreateMergedModuleTask extends BaseTask {
    @Input
    Property<String> mergedModuleName

    @Input
    Property<List<String>> forceMergedJarPrefixes

    @Input
    Property<String> javaHome

    @Input
    Property<ModuleInfo> mergedModuleInfo

    @Input
    Property<String> jdepsEnabled

    @OutputFile
    File getMergedModuleJar() {
        new File(PathUtil.getJlinkJarsDirPath(jlinkBasePath.get()), "${mergedModuleName.get()}.jar")
    }

    @javax.inject.Inject
    CreateMergedModuleTask() {
        dependsOn('jar')
        description = 'Merges all non-modularized jars into a single module'
    }

    @Override
    void init(JlinkPluginExtension extension) {
        super.init(extension)
        mergedModuleName = extension.mergedModuleName
        forceMergedJarPrefixes = extension.forceMergedJarPrefixes
        javaHome = extension.javaHome
        mergedModuleInfo = extension.mergedModuleInfo
        jdepsEnabled = extension.jdepsEnabled
    }

    @TaskAction
    void createMergedModuleAction() {
        def taskData = new CreateMergedModuleTaskData()
        taskData.jlinkBasePath = jlinkBasePath.get()
        taskData.mergedModuleName = mergedModuleName.get()
        taskData.forceMergedJarPrefixes = forceMergedJarPrefixes.get()
        taskData.javaHome = javaHome.get()
        taskData.mergedModuleInfo = mergedModuleInfo.get()
        taskData.jdepsEnabled = jdepsEnabled.get()
        taskData.mergedModuleJar = mergedModuleJar

        taskData.nonModularJarsDirPath = PathUtil.getNonModularJarsDirPath(taskData.jlinkBasePath)
        taskData.jlinkJarsDirPath = PathUtil.getJlinkJarsDirPath(taskData.jlinkBasePath)
        taskData.tmpMergedModuleDirPath = PathUtil.getTmpMergedModuleDirPath(taskData.jlinkBasePath)
        taskData.tmpModuleInfoDirPath = PathUtil.getTmpModuleInfoDirPath(taskData.jlinkBasePath)
        taskData.mergedJarsDirPath = PathUtil.getMergedJarsDirPath(taskData.jlinkBasePath)
        taskData.tmpJarsDirPath = PathUtil.getTmpJarsDirPath(taskData.jlinkBasePath)

        def taskImpl = new CreateMergedModuleTaskImpl(project, taskData)
        taskImpl.execute()
    }
}