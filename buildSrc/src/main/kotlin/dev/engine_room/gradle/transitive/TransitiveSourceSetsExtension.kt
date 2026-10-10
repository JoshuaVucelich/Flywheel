package dev.engine_room.gradle.transitive

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet

open class TransitiveSourceSetsExtension(val project: Project) {
    var compileClasspath: FileCollection? = null
    var runtimeClasspath: FileCollection? = null

    private val transitives = mutableMapOf<SourceSet, TransitiveSourceSetConfigurator>()

    fun sourceSet(sourceSet: SourceSet) {
        registerSourceSet(sourceSet)
    }

    fun sourceSet(sourceSet: SourceSet, action: Action<TransitiveSourceSetConfigurator>) {
        action.execute(registerSourceSet(sourceSet))
    }

    private fun registerSourceSet(sourceSet: SourceSet): TransitiveSourceSetConfigurator {
        return transitives.computeIfAbsent(sourceSet) { TransitiveSourceSetConfigurator(this, it) }
    }

    fun createCompileConfigurations() {
        val configs = transitives.mapValues { (sourceSet, _) ->
            project.configurations.create("for${sourceSet.name.replaceFirstChar { it.uppercase() }}") {
                isCanBeConsumed = true
                isCanBeResolved = false
            }
        }

        transitives.forEach { (sourceSet, configurator) ->
            val config = configs.getValue(sourceSet)
            project.configurations.named(sourceSet.compileOnlyConfigurationName).configure {
                extendsFrom(config)
                configurator.compileSourceSets.forEach {
                    extendsFrom(configs.getValue(it))
                }
            }
        }
    }

    fun createRuntimeConfigurations() {
        val configs = transitives.mapValues { (sourceSet, _) ->
            project.configurations.create("run${sourceSet.name.replaceFirstChar { it.uppercase() }}") {
                isCanBeConsumed = true
                isCanBeResolved = false
            }
        }

        transitives.forEach { (sourceSet, configurator) ->
            val config = configs.getValue(sourceSet)
            project.configurations.named(sourceSet.runtimeOnlyConfigurationName).configure {
                extendsFrom(config)
                configurator.runtimeSourceSets.forEach {
                    extendsFrom(configs.getValue(it))
                }
            }
        }
    }
}
