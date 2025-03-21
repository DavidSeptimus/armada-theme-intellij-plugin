package com.github.davidseptimus.armada.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;

public final class TextAttributeKeys {
    public static final TextAttributesKey YAML_ALIAS = TextAttributesKey.createTextAttributesKey("YAML_ALIAS");
    public static final TextAttributesKey XML_DOCTYPE = TextAttributesKey.createTextAttributesKey("XML_DOCTYPE");
    public static final TextAttributesKey PROPERTIES_STRING_LITERAL_VALUE = TextAttributesKey.createTextAttributesKey("PROPERTIES.STRING_LITERAL_VALUE");
    public static final TextAttributesKey JAVASCRIPT_PROPERTY_REFERENCE = TextAttributesKey.createTextAttributesKey("JS.PROPERTY_REFERENCE");
    public static final TextAttributesKey JAVASCRIPT_THIS_IDENTIFIER = TextAttributesKey.createTextAttributesKey("JS.THIS_IDENTIFIER");
    public static final TextAttributesKey ZIG_FIELD_TYPE = TextAttributesKey.createTextAttributesKey("ZIG_FIELD_TYPE");
    public static final TextAttributesKey ZIG_RETURN_TYPE_IDENTIFIER = TextAttributesKey.createTextAttributesKey("ZIG_RETURN_TYPE_IDENTIFIER", ZIG_FIELD_TYPE);
}
