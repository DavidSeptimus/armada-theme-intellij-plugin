package com.github.davidseptimus.armada.settings;

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
            new AttributesDescriptor("CSS percent", TextAttributeKeys.CSS_PERCENT),
            new AttributesDescriptor("CSS identifier term", TextAttributeKeys.CSS_IDENTIFIER_TERM),
            new AttributesDescriptor("JavaScript function/method declaration identifier", TextAttributeKeys.JAVASCRIPT_FUNCTION_DECLARATION_IDENTIFIER),
            new AttributesDescriptor("JavaScript this identifier", TextAttributeKeys.JAVASCRIPT_THIS_IDENTIFIER),
            new AttributesDescriptor("JavaScript property reference", TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE),
            new AttributesDescriptor("Kotlin label @", TextAttributeKeys.KOTLIN_LABEL_AT),
            new AttributesDescriptor("PHP alias identifier declaration", TextAttributeKeys.PHP_ALIAS_IDENTIFIER_DECLARATION),
            new AttributesDescriptor("PHP class reference", TextAttributeKeys.PHP_CLASS_REFERENCE),
            new AttributesDescriptor("PHP class self reference", TextAttributeKeys.PHP_CLASS_SELF_REFERENCE),
            new AttributesDescriptor("PHP goto label identifier", TextAttributeKeys.PHP_GOTO_LABEL_IDENTIFIER),
            new AttributesDescriptor("Properties string literal value", TextAttributeKeys.PROPERTIES_STRING_LITERAL_VALUE),
            new AttributesDescriptor("Python type annotation", TextAttributeKeys.PYTHON_TYPE_ANNOTATION),
            new AttributesDescriptor("XML DocType", TextAttributeKeys.XML_DOCTYPE),
            new AttributesDescriptor("YAML alias", TextAttributeKeys.YAML_ALIAS),
            new AttributesDescriptor("YAML single quoted string", TextAttributeKeys.YAML_SINGLE_QUOTED_STRING),
            new AttributesDescriptor("Zig field/argument type", TextAttributeKeys.ZIG_FIELD_TYPE),
            new AttributesDescriptor("Zig return type identifier", TextAttributeKeys.ZIG_RETURN_TYPE_IDENTIFIER),
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
                Sample text for reference. No syntax highlighting is applied.
                
                // CSS
                color: --someColor; // CSS Identifier Term
                width: 100%; // CSS Percent
                
                // JavaScript
                function myFunc() {} // JavaScript Function Declaration Identifier
                this // JavaScript This Identifier
                a.b.c // JavaScript Property Reference
                
                // Kotlin
                ints.forEach lit@ {                 // Kotlin Label @
                    if (it == null) return @lit
                        println(it + ref)
                    }
                }
                
                // PHP
                use SomeType as MyType; // PHP Alias Identifier Declaration
                $def .=  self::magic; // PHP Class Self Reference
                A extends B implements C; // PHP Class Reference
                goto SomeLabel; // PHP Goto Label Identifier
                
                // Properties
                a = "b" // Properties String Literal Value
                
                // Python
                def my_func(arg1: MyType) -> None: // Python Type Annotation
                    pass
                
                // XML
                <!DOCTYPE html> // XML DocType
                
                // YAML
                a: *alias // YAML Alias
                b: 'string' // YAML Single Quoted String
                
                // Zig
                field: ?*Node // Zig Field/Argument Type
                pub fn myFunc() SomeError.Variant!void { // Zig Return Type Identifier
                    return null;
                }
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public String getDisplayName() {
        return "Armada Additions";
    }

}