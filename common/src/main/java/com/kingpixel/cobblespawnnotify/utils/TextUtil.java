package com.kingpixel.cobblespawnnotify.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
  private static final Pattern HEXPATTERN = Pattern.compile("\\{(#[a-fA-F0-9]{6})}");
  private static final String SPLITPATTERN = "((?=\\{#[a-fA-F0-9]{6}}))";

  public static Component parseHexCodes(String text, boolean removeItalics) {
    if (text == null)
      return null;
    MutableComponent comp = Component.empty();

    String[] temp = text.split(SPLITPATTERN);
    Arrays.stream(temp).forEach(s -> {
      Matcher m = HEXPATTERN.matcher(s);
      if (m.find()) {
        TextColor color = TextColor.parseColor(m.group(1));
        s = m.replaceAll(""); // Eliminar el código hexadecimal
        if (removeItalics) {
          comp.append(Component.literal(s).setStyle(Style.EMPTY.withColor(color).withItalic(false)));
        } else {
          comp.append(Component.literal(s).setStyle(Style.EMPTY.withColor(color)));
        }
      } else {
        comp.append(Component.literal(s));
      }
    });

    return comp;
  }


  public static List<String> parseHexCodes(List<String> textList, boolean removeItalics) {
    if (textList == null)
      return null;

    List<String> result = new ArrayList<>();
    int size = textList.size();

    for (int i = 0; i < size; i++) {
      String text = textList.get(i);
      text = HEXPATTERN.matcher(text).replaceAll("");
      Component comp = parseHexCodes(text, removeItalics);
      if (i < size - 1) {
        result.add(comp.getString() + "\n");
      } else {
        result.add(comp.getString());
      }
    }

    return result;
  }

  public static final TextColor BLUE = TextColor.parseColor("#00AFFC");
  public static final TextColor ORANGE = TextColor.parseColor("#FF6700");
}