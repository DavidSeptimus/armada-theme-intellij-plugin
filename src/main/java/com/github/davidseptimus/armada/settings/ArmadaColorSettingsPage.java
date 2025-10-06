package com.github.davidseptimus.armada.settings;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

final class ArmadaColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("CSS//Percent", TextAttributeKeys.CSS_PERCENT),
            new AttributesDescriptor("CSS//Identifier term", TextAttributeKeys.CSS_IDENTIFIER_TERM),
            new AttributesDescriptor("Java//Annotation declaration @", TextAttributeKeys.JAVA_ANNOTATION_DECLARATION),
            new AttributesDescriptor("JavaScript//Function/method declaration identifier", TextAttributeKeys.JAVASCRIPT_FUNCTION_DECLARATION_IDENTIFIER),
            new AttributesDescriptor("JavaScript//This identifier", TextAttributeKeys.JAVASCRIPT_THIS_IDENTIFIER),
            new AttributesDescriptor("JavaScript//Property reference", TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE),
            new AttributesDescriptor("Kotlin//Label @", TextAttributeKeys.KOTLIN_LABEL_AT),
            new AttributesDescriptor("PHP//Alias identifier declaration", TextAttributeKeys.PHP_ALIAS_IDENTIFIER_DECLARATION),
            new AttributesDescriptor("PHP//Class reference", TextAttributeKeys.PHP_CLASS_REFERENCE),
            new AttributesDescriptor("PHP//Class self reference", TextAttributeKeys.PHP_CLASS_SELF_REFERENCE),
            new AttributesDescriptor("PHP//Goto label identifier", TextAttributeKeys.PHP_GOTO_LABEL_IDENTIFIER),
            new AttributesDescriptor("Properties//String literal value", TextAttributeKeys.PROPERTIES_STRING_LITERAL_VALUE),
            new AttributesDescriptor("Python//Type annotation", TextAttributeKeys.PYTHON_TYPE_ANNOTATION),
            new AttributesDescriptor("Rust//Attribute punctuation", TextAttributeKeys.RUST_ATTRIBUTE_PUNCTUATION),
            new AttributesDescriptor("Rust//Boolean", TextAttributeKeys.RUST_BOOLEAN),
            new AttributesDescriptor("Shell Script//Let command", TextAttributeKeys.BASH_LET_COMMAND),
            new AttributesDescriptor("Shell Script//Subshell command parentheses", TextAttributeKeys.BASH_SUBSHELL_COMMAND_PARENTHESES),
            new AttributesDescriptor("SQL//Symbolic operators", TextAttributeKeys.SQL_SYMBOLIC_OPERATORS),
            new AttributesDescriptor("SQL//Foreign key definition", TextAttributeKeys.SQL_FOREIGN_KEY_DEFINITION),
            new AttributesDescriptor("SQL//Primary key definition", TextAttributeKeys.SQL_PRIMARY_KEY_DEFINITION),
            new AttributesDescriptor("SQL//Reference identifier", TextAttributeKeys.SQL_REFERENCE_IDENTIFIER),
            new AttributesDescriptor("SQL//Schema or table identifier (creation)", TextAttributeKeys.SQL_SCHEMA_TABLE_IDENTIFIER),
            new AttributesDescriptor("XML//DocType", TextAttributeKeys.XML_DOCTYPE),
            new AttributesDescriptor("YAML//Alias", TextAttributeKeys.YAML_ALIAS),
            new AttributesDescriptor("YAML//Single quoted string", TextAttributeKeys.YAML_SINGLE_QUOTED_STRING),
            new AttributesDescriptor("Zig//Field/argument type", TextAttributeKeys.ZIG_FIELD_TYPE),
            new AttributesDescriptor("Zig//Return type identifier", TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER),
    };

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new PlainSyntaxHighlighter();
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return """
                <comment>// CSS</comment>
                color: <css_identifier>--someColor</css_identifier>; <comment>// CSS Identifier Term</comment>
                width: <css_percent>100%</css_percent>; <comment>// CSS Percent</comment>

                <comment>// Java</comment>
                <java_annotation>@</java_annotation>interface MyAnnotation {} <comment>// Java Annotation Declaration @</comment>

                <comment>// JavaScript</comment>
                function <js_function_decl>myFunc</js_function_decl>() {} <comment>// JavaScript Function Declaration Identifier</comment>
                <js_this>this</js_this> <comment>// JavaScript This Identifier</comment>
                a.<js_property>b</js_property>.<js_property>c</js_property> <comment>// JavaScript Property Reference</comment>

                <comment>// Kotlin</comment>
                ints.forEach lit<kotlin_label>@</kotlin_label> {                 <comment>// Kotlin Label @</comment>
                    if (it == null) return <kotlin_label>@</kotlin_label>lit
                        println(it + ref)
                    }
                }

                <comment>// PHP</comment>
                use SomeType as <php_alias>MyType</php_alias>; <comment>// PHP Alias Identifier Declaration</comment>
                $def .=  <php_self>self</php_self>::magic; <comment>// PHP Class Self Reference</comment>
                A extends <php_class>B</php_class> implements <php_class>C</php_class>; <comment>// PHP Class Reference</comment>
                goto <php_label>SomeLabel</php_label>; <comment>// PHP Goto Label Identifier</comment>

                <comment>// Properties</comment>
                a = <properties_string>"b"</properties_string> <comment>// Properties String Literal Value</comment>

                <comment>// Python</comment>
                def my_func(arg1: <python_type>MyType</python_type>) -> <python_type>None</python_type>: <comment>// Python Type Annotation</comment>
                    pass

                <comment>// Rust</comment>
                <rust_attr>#</rust_attr><rust_attr>!</rust_attr><rust_attr>[</rust_attr>some_attribute = "value"<rust_attr>]</rust_attr>  <comment>// Rust Attribute Punctuation</comment>
                let is_enabled: bool = <rust_boolean>true</rust_boolean>; <comment>// Rust Boolean</comment>

                <comment>// Shell Script</comment>
                <bash_let>let</bash_let> a=5 b=10 <comment>// Shell Script let command</comment>
                rm -f <bash_subshell>$</bash_subshell><bash_subshell>(</bash_subshell>find / -name core<bash_subshell>)</bash_subshell> <comment>// Shell Script Subshell Command Parentheses</comment>
                <comment>// XML</comment>
                <xml_doctype><!DOCTYPE html></xml_doctype> <comment>// XML DocType</comment>

                <comment>// SQL</comment>
                alter table orders add constraint fk_customer <sql_foreign_key>foreign key</sql_foreign_key> (customer_id) <sql_foreign_key>references</sql_foreign_key> customers(id); <comment>// SQL Foreign Key Definition</comment>
                create table users (id int <sql_primary_key>primary key</sql_primary_key>, name varchar(100)); <comment>// SQL Primary Key Definition</comment>
                select <sql_symbolic>*</sql_symbolic> from users where id <sql_symbolic>=</sql_symbolic> 5; <comment>// SQL symbolic Operators</comment>
                select id from <sql_reference>crm</sql_reference>.<sql_reference>product</sql_reference>; <comment>// SQL Table Reference Identifier</comment>
                create table <sql_schema_table>my_table</sql_schema_table> (id int, name varchar(100)); <comment>// SQL Schema Table Identifier</comment>

                <comment>// YAML</comment>
                a: <yaml_alias>*alias</yaml_alias> <comment>// YAML Alias</comment>
                b: <yaml_single_quoted>'string'</yaml_single_quoted> <comment>// YAML Single Quoted String</comment>

                <comment>// Zig</comment>
                field: <zig_field>?*Node</zig_field> <comment>// Zig Field/Argument Type</comment>
                pub fn myFunc() <zig_return>SomeError.Variant!void</zig_return> { <comment>// Zig Return Type Identifier</comment>
                    return null;
                }
                """;
    }

    @NotNull
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return Map.ofEntries(
                Map.entry("comment", DefaultLanguageHighlighterColors.LINE_COMMENT),
                Map.entry("css_identifier", TextAttributeKeys.CSS_IDENTIFIER_TERM),
                Map.entry("css_percent", TextAttributeKeys.CSS_PERCENT),
                Map.entry("java_annotation", TextAttributeKeys.JAVA_ANNOTATION_DECLARATION),
                Map.entry("js_function_decl", TextAttributeKeys.JAVASCRIPT_FUNCTION_DECLARATION_IDENTIFIER),
                Map.entry("js_this", TextAttributeKeys.JAVASCRIPT_THIS_IDENTIFIER),
                Map.entry("js_property", TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE),
                Map.entry("kotlin_label", TextAttributeKeys.KOTLIN_LABEL_AT),
                Map.entry("php_alias", TextAttributeKeys.PHP_ALIAS_IDENTIFIER_DECLARATION),
                Map.entry("php_class", TextAttributeKeys.PHP_CLASS_REFERENCE),
                Map.entry("php_self", TextAttributeKeys.PHP_CLASS_SELF_REFERENCE),
                Map.entry("php_label", TextAttributeKeys.PHP_GOTO_LABEL_IDENTIFIER),
                Map.entry("properties_string", TextAttributeKeys.PROPERTIES_STRING_LITERAL_VALUE),
                Map.entry("python_type", TextAttributeKeys.PYTHON_TYPE_ANNOTATION),
                Map.entry("rust_attr", TextAttributeKeys.RUST_ATTRIBUTE_PUNCTUATION),
                Map.entry("rust_boolean", TextAttributeKeys.RUST_BOOLEAN),
                Map.entry("bash_let", TextAttributeKeys.BASH_LET_COMMAND),
                Map.entry("bash_subshell", TextAttributeKeys.BASH_SUBSHELL_COMMAND_PARENTHESES),
                Map.entry("sql_symbolic", TextAttributeKeys.SQL_SYMBOLIC_OPERATORS),
                Map.entry("sql_foreign_key", TextAttributeKeys.SQL_FOREIGN_KEY_DEFINITION),
                Map.entry("sql_primary_key", TextAttributeKeys.SQL_PRIMARY_KEY_DEFINITION),
                Map.entry("sql_reference", TextAttributeKeys.SQL_REFERENCE_IDENTIFIER),
                Map.entry("sql_schema_table", TextAttributeKeys.SQL_SCHEMA_TABLE_IDENTIFIER),
                Map.entry("xml_doctype", TextAttributeKeys.XML_DOCTYPE),
                Map.entry("yaml_alias", TextAttributeKeys.YAML_ALIAS),
                Map.entry("yaml_single_quoted", TextAttributeKeys.YAML_SINGLE_QUOTED_STRING),
                Map.entry("zig_field", TextAttributeKeys.ZIG_FIELD_TYPE),
                Map.entry("zig_return", TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER)
        );
    }

    @NotNull
    @Override
    public AttributesDescriptor [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Armada Additions";
    }

}