package org.code4everything.hutool

import cn.hutool.core.io.FileUtil
import com.alibaba.fastjson.JSON

/**
 * @author pantao
 * @since 2021/5/11
 */
object HutoolAlias {

    @JvmStatic
    fun main(args: Array<String>) {
        val json = FileUtil.readUtf8String("${Hutool.homeDir}\\class.json")
        JSON.parseObject(json).forEach { k, _ ->
            Hutool.main("$k#alias -d".split(" ").toTypedArray()).also { println() }
        }
    }
}
