package com.github.davidseptimus.armada.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.NotNull;

public final class TextAttributeKeys {
    public static final @NotNull TextAttributesKey CSS_IDENTIFIER_TERM = TextAttributesKey.createTextAttributesKey("CSS.TERM_IDENTIFIER");
    public static final @NotNull TextAttributesKey CSS_PERCENT = TextAttributesKey.createTextAttributesKey("CSS.PERCENT");
    public static final @NotNull TextAttributesKey JAVASCRIPT_FUNCTION_DECLARATION_IDENTIFIER = TextAttributesKey.createTextAttributesKey("JS.FUNCTION_DECLARATION_IDENTIFIER");
    public static final @NotNull TextAttributesKey JAVASCRIPT_PROPERTY_REFERENCE = TextAttributesKey.createTextAttributesKey("JS.PROPERTY_REFERENCE");
    public static final @NotNull TextAttributesKey JAVASCRIPT_THIS_IDENTIFIER = TextAttributesKey.createTextAttributesKey("JS.THIS_IDENTIFIER");
    public static final @NotNull TextAttributesKey KOTLIN_LABEL_AT = TextAttributesKey.createTextAttributesKey("KOTLIN_LABEL_AT");
    public static final @NotNull TextAttributesKey PHP_CLASS_REFERENCE = TextAttributesKey.createTextAttributesKey("PHP_CLASS_REFERENCE");
    public static final @NotNull TextAttributesKey PHP_ALIAS_IDENTIFIER_DECLARATION = TextAttributesKey.createTextAttributesKey("PHP_ALIAS_IDENTIFIER_DECLARATION");
    public static final @NotNull TextAttributesKey PHP_CLASS_SELF_REFERENCE = TextAttributesKey.createTextAttributesKey("PHP_CLASS_SELF_REFERENCE");
    public static final @NotNull TextAttributesKey PHP_GOTO_LABEL_IDENTIFIER =  TextAttributesKey.createTextAttributesKey("PHP_GOTO_LABEL_IDENTIFIER");
    public static final @NotNull TextAttributesKey PROPERTIES_STRING_LITERAL_VALUE = TextAttributesKey.createTextAttributesKey("PROPERTIES.STRING_LITERAL_VALUE");
    public static final @NotNull TextAttributesKey PYTHON_TYPE_ANNOTATION = TextAttributesKey.createTextAttributesKey("PY.TYPE_ANNOTATION");
    // PYTHON_TYPE_PARAMETER is an existing attribute, but doesn't seem to get highlighted by the python highlighter
    public static final @NotNull TextAttributesKey PYTHON_TYPE_PARAMETER = TextAttributesKey.createTextAttributesKey("PY.TYPE_PARAMETER");
    public static final @NotNull TextAttributesKey XML_DOCTYPE = TextAttributesKey.createTextAttributesKey("XML_DOCTYPE");
    public static final @NotNull TextAttributesKey YAML_ALIAS = TextAttributesKey.createTextAttributesKey("YAML_ALIAS");
    public static final @NotNull TextAttributesKey YAML_SINGLE_QUOTED_STRING = TextAttributesKey.createTextAttributesKey("YAML_SINGLE_QUOTED_STRING");
    public static final @NotNull TextAttributesKey ZIG_FIELD_TYPE = TextAttributesKey.createTextAttributesKey("ZIG_FIELD_TYPE");
    public static final @NotNull TextAttributesKey ZIG_RETURN_TYPE_IDENTIFIER = TextAttributesKey.createTextAttributesKey("ZIG_RETURN_TYPE_IDENTIFIER", ZIG_FIELD_TYPE);
}
