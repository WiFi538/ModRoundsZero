package com.rounds.zero.client.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public final class MarkupTextParser {
    private MarkupTextParser() {
    }

    public static Text parse(String markup) {
        if (markup == null || markup.isEmpty()) {
            return Text.empty();
        }

        String[] lines = markup.split("\n", -1);
        MutableText result = Text.empty();

        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                result.append(Text.literal("\n"));
            }
            result.append(parseLine(lines[i]));
        }

        return result;
    }

    private static Text parseLine(String line) {
        if (!line.contains("§")) {
            return Text.literal(line).formatted(Formatting.GRAY);
        }

        List<Text> parts = new ArrayList<>();
        Formatting current = Formatting.GRAY;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '§' && i + 1 < line.length()) {
                flush(buffer, current, parts);
                current = formattingForCode(line.charAt(i + 1));
                i++;
                continue;
            }
            buffer.append(ch);
        }

        flush(buffer, current, parts);

        MutableText combined = Text.empty();
        for (Text part : parts) {
            combined.append(part);
        }
        return combined;
    }

    private static void flush(StringBuilder buffer, Formatting formatting, List<Text> parts) {
        if (buffer.isEmpty()) {
            return;
        }
        parts.add(Text.literal(buffer.toString()).formatted(formatting));
        buffer.setLength(0);
    }

    private static Formatting formattingForCode(char code) {
        return switch (code) {
            case 'a' -> Formatting.GREEN;
            case 'c' -> Formatting.RED;
            case 'f', '7' -> Formatting.GRAY;
            case 'r' -> Formatting.RESET;
            default -> Formatting.GRAY;
        };
    }
}
