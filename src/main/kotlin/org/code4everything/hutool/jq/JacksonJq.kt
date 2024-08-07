package org.code4everything.hutool.jq

import cn.hutool.core.io.FileUtil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.nio.file.FileSystems
import net.thisptr.jackson.jq.BuiltinFunctionLoader
import net.thisptr.jackson.jq.JsonQuery
import net.thisptr.jackson.jq.Scope
import net.thisptr.jackson.jq.Versions
import net.thisptr.jackson.jq.internal.functions.EnvFunction
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader
import net.thisptr.jackson.jq.module.loaders.ChainedModuleLoader
import net.thisptr.jackson.jq.module.loaders.FileSystemModuleLoader
import org.code4everything.hutool.HelpInfo
import org.code4everything.hutool.Hutool
import org.code4everything.hutool.MethodArg
import org.code4everything.hutool.converter.FileConverter

/**
 * official jq: https://stedolan.github.io/jq/
 * java jq implementation: https://github.com/eiiches/jackson-jq
 *
 */
object JacksonJq {

    @JvmStatic
    @HelpInfo(helps = [
        "example: '.name' '{\"name\":\"jq\"}'", "",
        "param1: the jq expression",
        "param2: the json content or json file", "",
        "optional args: strip",
        "strip: remove string head and tail double quotes", "",
        "jq grammar: https://stedolan.github.io/jq/manual/#Basicfilters",
    ])
    fun queryJson(expression: String, content: String): String {
        val params = MethodArg.getSubParams(Hutool.ARG, 2)
        val strip = params.contains("strip")

        var json = content
        if (!json.startsWith('[') && !json.startsWith('{')) {
            json = FileConverter().string2Object(json).let { if (it.exists()) FileUtil.readUtf8String(it) else json }
        }

        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)

        val version = Versions.JQ_1_6
        val scope = Scope.newEmptyScope()
        BuiltinFunctionLoader.getInstance().loadFunctions(version, scope)
        scope.addFunction("env", 0, EnvFunction())
        val loader = FileSystemModuleLoader(scope, version, FileSystems.getDefault().getPath("").toAbsolutePath())
        scope.moduleLoader = ChainedModuleLoader(BuiltinModuleLoader.getInstance(), loader)

        val jsonNode = mapper.factory.createParser(json).readValueAsTree<JsonNode>()
        val jq = JsonQuery.compile(expression, version)

        val sb = StringBuilder()
        jq.apply(scope, jsonNode) {
            var value = mapper.writeValueAsString(it)
            if (strip && value.startsWith('"') && value.endsWith('"')) {
                value = value.removePrefix("\"").removeSuffix("\"")
            }
            sb.append(value).append(System.lineSeparator())
        }
        return sb.removeSuffix(System.lineSeparator()).toString()
    }
}
