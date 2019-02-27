package org.jetbrains.kotlin.spec.grammar.tools

import org.jetbrains.kotlin.spec.grammar.tools.parsing.Parser

class KotlinToken(
    val type: String,
    val text: String,
    val channel: Int
) {
    override fun toString() = "$type(\"${text.replace(System.lineSeparator(), "\\n")}\")"
}

class KotlinTokensList(list: List<KotlinToken>): ArrayList<KotlinToken>(list) {
    override fun toString() = joinToString(System.lineSeparator())
}

enum class KotlinParseTreeNodeType {
    RULE, TERMINAL
}

class KotlinLexerException(val lexerMessage: String, val position: Pair<Int, Int>) : Throwable() {
    init {
        System.err.println("Lexer error: $lexerMessage, position: $position")
    }
}

class KotlinParserException(val parserMessage: String, val position: Pair<Int, Int>) : Throwable() {
    init {
        System.err.println("Parser error: $parserMessage, position: $position")
    }
}

class KotlinParseTree(
    val type: KotlinParseTreeNodeType,
    private val name: String,
    private val text: String? = null,
    val children: MutableList<KotlinParseTree> = mutableListOf()
) {
    companion object {
        private val ls = System.lineSeparator()
    }

    private fun stringifyTree(builder: StringBuilder, node: KotlinParseTree, depth: Int = 0): StringBuilder =
        builder.apply {
            node.children.forEach { child ->
                when (child.type) {
                    KotlinParseTreeNodeType.RULE -> append(" ".repeat(depth) + child.name + ls)
                    KotlinParseTreeNodeType.TERMINAL -> append(" ".repeat(depth) + "${child.name}(\"${child.text!!.replace(ls, "\\n")}\")" + ls)
                }
                stringifyTree(builder, child, depth + 1)
            }
        }

    override fun toString() = stringifyTree(StringBuilder(), this).toString()
}

fun parseKotlinCode(tokens: List<KotlinToken>) = Parser.parse(tokens)

fun parseKotlinCode(sourceCode: String) = parseKotlinCode(tokenizeKotlinCode(sourceCode))

fun tokenizeKotlinCode(sourceCode: String) = Parser.tokenize(sourceCode)
