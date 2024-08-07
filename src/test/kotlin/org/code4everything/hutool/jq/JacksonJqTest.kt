package org.code4everything.hutool.jq

import org.junit.Test

class JacksonJqTest {

    @Test
    fun jq() {
        val expression = ".[]|.name+\"=\"+(.version|tostring)"
        val content = "[" +
            "{" +
            "    \"name\": \"name1\"," +
            "    \"version\":4" +
            "},{" +
            "    \"name\": \"name2\"," +
            "    \"version\":6" +
            "}]"

        println(JacksonJq.queryJson(expression, content))
    }
}
