package org.jetbrains.kotlin.spec.grammar.tools.loader

import java.io.File

internal object GrammarLoader {
    private const val GRAMMAR_DIR = "src/org.jetbrains.kotlin.grammar.validate.main/antlr"
    private const val LEXER_FILENAME = "KotlinLexer.g4"
    private const val PARSER_FILENAME = "KotlinParser.g4"
    private const val UNICODE_CLASSES_FILENAME = "UnicodeClasses.g4"

    private fun getGrammarFileText(grammarName: String) = File("$GRAMMAR_DIR/$grammarName").readText()

    fun getLexerGrammarAsText() = getGrammarFileText(LEXER_FILENAME)

    fun getParserGrammarAsText() = getGrammarFileText(PARSER_FILENAME)

    fun getUnicodeClassesGrammarAsText() = getGrammarFileText(UNICODE_CLASSES_FILENAME)
}
