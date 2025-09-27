package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPrimaryKeyDefinitionImpl


class SQLAnnotator : Annotator {

    val sqlSymbolicOperators = setOf(
        "=",
        "<",
        ">",
        "<=",
        ">=",
        "<>",
        "!=",
        "+",
        "-",
        "*",
        "/",
        "%",
        "||",
        "&&",
        "!",
        "|",
        "&",
        "^",
        "~",
        "<<",
        ">>",
        "|=",
        "&=",
        "^=",
        "::",
        "!<",
        "!>",
        "+=",
        "-=",
        "*=",
        "/=",
        "%="
    )

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val siblingPredicate = { s: PsiElement -> s.text == "." }
        when {
            sqlSymbolicOperators.contains(element.text) -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_SYMBOLIC_OPERATORS)
            }

            element is SqlPrimaryKeyDefinitionImpl -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_PRIMARY_KEY_DEFINITION)
            }
            (element is SqlIdentifier || element is SqlNameElement )&& hasAncestor(element, {a: PsiElement -> (a.elementType == SqlElementTypes.SQL_TABLE_REFERENCE && a.parent is SqlCreateTableStatement) || (a.elementType == SqlElementTypes.SQL_SCHEMA_REFERENCE && a.parent is SqlCreateSchemaStatement)}) -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_SCHEMA_TABLE_IDENTIFIER)
            }
            hasAncestor(element, SqlForeignKeyDefinition::class.java) -> {
                annotateForeignKey(element, holder)
            }
            (element is SqlIdentifier || element is SqlNameElement) && hasAncestor(
                element,
                SqlReferenceExpression::class.java
            )
                    && (element.nextSibling?.text == "." || hasSibling(element.parent, siblingPredicate)  || element.prevSibling?.text == "." || hasSibling(
                element,
                siblingPredicate,
                true
            )) -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_REFERENCE_IDENTIFIER)
            }
        }
    }

    val fkReferenceClauseText = "SQL_FOREIGN_KEY_REFERENCES_CLAUSE"
    val fkCascadeClauseText = "SQL_FOREIGN_KEY_CASCADE_OPTION"
    val fkCascadeKeywords = setOf("cascade", "update", "on", "delete")


    private fun annotateForeignKey(
        element: PsiElement,
        holder: AnnotationHolder
    ) {
        val text = element.text.lowercase()
        if (text == "foreign" || text == "key"
            || (text == "references" && element.parent.toString() == fkReferenceClauseText)
            || (fkCascadeKeywords.contains(text) && element.parent.toString() == fkCascadeClauseText)
        ) {
            highlightElement(element, holder, TextAttributeKeys.SQL_FOREIGN_KEY_DEFINITION)
        }
    }
}
