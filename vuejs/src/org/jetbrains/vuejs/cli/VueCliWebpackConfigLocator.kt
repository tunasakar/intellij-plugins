// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.cli

import com.intellij.javascript.nodejs.NodeModuleDirectorySearchProcessor
import com.intellij.javascript.nodejs.NodeModuleSearchUtil
import com.intellij.javascript.nodejs.PackageJsonData
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.lang.javascript.buildTools.webpack.WebPackConfigLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

class VueCliWebpackConfigLocator : WebPackConfigLocator {
  override fun detectConfig(project: Project, context: PsiElement): VirtualFile? =
    PackageJsonFileManager.getInstance(project).validPackageJsonFiles
      .asSequence()
      .filter { it.isValid && PackageJsonData.getOrCreateWithPreferredProject(project, it).isDependencyOfAnyType(VUE_CLI_SERVICE_PKG) }
      .mapNotNull {
        NodeModuleSearchUtil.resolveModuleFromNodeModulesDir(
          it.parent, VUE_CLI_SERVICE_PKG, NodeModuleDirectorySearchProcessor.PROCESSOR)
      }
      .mapNotNull { it.moduleSourceRoot.findChild("webpack.config.js") }
      .firstOrNull()

  companion object {
    const val VUE_CLI_SERVICE_PKG = "@vue/cli-service"
  }
}