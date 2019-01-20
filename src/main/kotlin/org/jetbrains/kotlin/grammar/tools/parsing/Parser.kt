package org.jetbrains.kotlin.grammar.tools.parsing

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.jetbrains.kotlin.grammar.tools.KotlinToken
import java.io.ByteArrayInputStream
import org.antlr.v4.runtime.misc.Pair
import java.nio.charset.StandardCharsets
import org.antlr.v4.runtime.Token.DEFAULT_CHANNEL
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.jetbrains.kotlin.grammar.parser.KotlinLexer
import org.jetbrains.kotlin.grammar.parser.KotlinParser
import org.jetbrains.kotlin.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.grammar.tools.KotlinParseTreeNodeType
import org.jetbrains.kotlin.grammar.tools.KotlinTokensList

internal object Parser {
    private fun getCharsStream(str: String) =
        CharStreams.fromStream(ByteArrayInputStream(str.toByteArray()), StandardCharsets.UTF_8)

    private fun getAntlrTokenByKotlinToken(token: KotlinToken, tokenTypeMap: Map<String, Int>): CommonToken {
        val tokenNumber = tokenTypeMap[token.type]!!

        return if (token.channel == DEFAULT_CHANNEL)
            CommonToken(tokenNumber, token.text)
        else {
            CommonToken(Pair(null, getCharsStream(token.text)), tokenNumber, token.channel, 0, 0)
        }
    }

    fun tokenize(sourceCode: String): KotlinTokensList {
        val lexer = KotlinLexer(getCharsStream(sourceCode))

        return KotlinTokensList(
            lexer.allTokens.map {
                KotlinToken(
                    lexer.vocabulary.getSymbolicName(it.type),
                    it.text,
                    it.channel
                )
            }
        )
    }

    private fun buildTree(
        parser: KotlinParser,
        tokenTypeMap: Map<String, Int>,
        antlrParseTree: ParseTree,
        kotlinParseTree: KotlinParseTree
    ): KotlinParseTree {
        for (i in 0..antlrParseTree.childCount) {
            val antlrParseTreeNode = antlrParseTree.getChild(i) ?: continue
            val kotlinParseTreeNode = when (antlrParseTreeNode) {
                is TerminalNodeImpl ->
                    KotlinParseTree(
                        KotlinParseTreeNodeType.TERMINAL,
                        KotlinLexer.VOCABULARY.getSymbolicName(antlrParseTreeNode.symbol.type),
                        antlrParseTreeNode.symbol.text
                    )
                else ->
                    KotlinParseTree(
                        KotlinParseTreeNodeType.RULE,
                        parser.ruleNames[(antlrParseTreeNode as RuleContext).ruleIndex]
                    )
            }

            kotlinParseTree.children.add(kotlinParseTreeNode)
            buildTree(parser, tokenTypeMap, antlrParseTreeNode, kotlinParseTreeNode)
        }

        return kotlinParseTree
    }

    fun parse(tokens: List<KotlinToken>): KotlinParseTree {
        val tokenTypeMap = KotlinLexer(null).tokenTypeMap
        val tokensList = ListTokenSource(tokens.map { getAntlrTokenByKotlinToken(it, tokenTypeMap) })
        val parser = KotlinParser(CommonTokenStream(tokensList))
        val tree = parser.kotlinFile()

        return buildTree(
            parser,
            tokenTypeMap,
            tree,
            KotlinParseTree(
                KotlinParseTreeNodeType.RULE,
                parser.ruleNames[parser.ruleIndexMap["kotlinFile"]!!]
            )
        )
    }
}
