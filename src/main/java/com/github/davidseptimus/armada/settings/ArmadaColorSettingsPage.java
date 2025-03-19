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
      new AttributesDescriptor("JavaScript Property Reference", TextAttributeKeys.JAVASCRIPT_PROPERTY_REFERENCE),
      new AttributesDescriptor("Properties String Literal Value", TextAttributeKeys.PROPERTIES_STRING_LITERAL_VALUE),
      new AttributesDescriptor("XML DocType", TextAttributeKeys.XML_DOCTYPE),
      new AttributesDescriptor("YAML Alias", TextAttributeKeys.YAML_ALIAS),
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
    return "No demo text available";
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @Override
  public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @Override
  public ColorDescriptor @NotNull [] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Armada Additions";
  }

}