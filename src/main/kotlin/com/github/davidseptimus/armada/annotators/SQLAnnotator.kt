package com.github.davidseptimus.armada.annotators

import com.github.davidseptimus.armada.settings.TextAttributeKeys
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPrimaryKeyDefinitionImpl


class SQLAnnotator : Annotator {

    private val sqlSymbolicOperators = setOf(
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

    private val siblingPredicate = { s: PsiElement -> s.text == "." }

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        var annotated = false
        when {
            sqlSymbolicOperators.contains(element.text) -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_SYMBOLIC_OPERATORS)
                annotated = true
            }

            element is SqlPrimaryKeyDefinitionImpl -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_PRIMARY_KEY_DEFINITION)
                annotated = true
            }
            (element is SqlIdentifier || element is SqlNameElement )&& !hasAncestor(element, {p: PsiElement -> p.elementType == SqlElementTypes.SQL_TABLE_ELEMENT_LIST }) && hasAncestor(element, { a: PsiElement -> (a.elementType == SqlElementTypes.SQL_TABLE_REFERENCE && a.parent is SqlCreateTableStatement) || (a.elementType == SqlElementTypes.SQL_SCHEMA_REFERENCE && a.parent is SqlCreateSchemaStatement)}) -> {
                highlightElement(element, holder, TextAttributeKeys.SQL_SCHEMA_TABLE_IDENTIFIER)
                annotated = true
            }
            hasAncestor(element, SqlForeignKeyDefinition::class.java) -> {
                annotated = annotateForeignKey(element, holder)
            }
        }
        if (annotated) {
            return
        }
        annotateReferenceIdentifier(element, holder)

    }

    private val fkReferenceClauseString = "SQL_FOREIGN_KEY_REFERENCES_CLAUSE"
    private val fkCascadeClauseString = "SQL_FOREIGN_KEY_CASCADE_OPTION"
    private val fkCascadeKeywords = setOf("cascade", "update", "on", "delete")

    private fun annotateForeignKey(
        element: PsiElement,
        holder: AnnotationHolder
    ): Boolean {
        val text = element.text.lowercase()
        if (annotateReferenceIdentifier(element, holder)) {
            return true
        }
        if (text == "foreign" || text == "key"
            || (text == "references" && element.parent.toString() == fkReferenceClauseString)
            || (fkCascadeKeywords.contains(text) && element.parent.toString() == fkCascadeClauseString)
        ) {
            highlightElement(element, holder, TextAttributeKeys.SQL_FOREIGN_KEY_DEFINITION)
            return true
        }
        return false
    }

    private fun annotateReferenceIdentifier(
        element: PsiElement,
        holder: AnnotationHolder
    ): Boolean {
        if ((element is SqlIdentifier || element is SqlNameElement)
            && hasAncestor(
                element,
                SqlReferenceExpression::class.java
            )
            && (element.nextSibling?.text == "." || hasSibling(
                element.parent,
                siblingPredicate
            ) || element.prevSibling?.text == "." || hasSibling(
                element,
                siblingPredicate,
                true
            ))
        ) {
            highlightElement(element, holder, TextAttributeKeys.SQL_REFERENCE_IDENTIFIER)
            return true
        }
        return false
    }
}