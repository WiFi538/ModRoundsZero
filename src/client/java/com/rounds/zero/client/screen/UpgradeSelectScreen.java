package com.rounds.zero.client.screen;

import com.rounds.zero.client.network.UpgradeClientPackets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class UpgradeSelectScreen extends Screen {

    public record Card(String id, String title, String description, String texture) {}

    private final List<Card> cards;

    private static final int MAX_CARD_WIDTH = 140;
    private static final int MAX_CARD_HEIGHT = 180;
    private static final int GAP = 10;

    public UpgradeSelectScreen(List<Card> cards) {
        super(Text.literal("Выбор улучшения"));
        this.cards = cards;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);

        if (cards == null || cards.isEmpty()) {
            super.render(ctx, mouseX, mouseY, delta);
            return;
        }

        int maxWidth = (int) (this.width * 0.9);
        int cardWidth = Math.min(MAX_CARD_WIDTH, (maxWidth - (cards.size() - 1) * GAP) / cards.size());
        int cardHeight = Math.min(MAX_CARD_HEIGHT, (int) (cardWidth * 1.3));

        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * GAP;
        int startX = (this.width - totalWidth) / 2;
        int y = (this.height - cardHeight) / 2;

        for (int i = 0; i < cards.size(); i++) {
            int x = startX + i * (cardWidth + GAP);
            Card c = cards.get(i);

            boolean hovered = mouseX >= x && mouseX <= x + cardWidth
                    && mouseY >= y && mouseY <= y + cardHeight;

            drawCard(ctx, c, x, y, cardWidth, cardHeight, hovered);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawCard(DrawContext ctx, Card c, int x, int y, int width, int height, boolean hovered) {
        int border = hovered ? 0xFFFFAA00 : 0xFF555555;

        ctx.fill(x - 2, y - 2, x + width + 2, y + height + 2, border);
        ctx.fill(x, y, x + width, y + height, 0xFF222222);

        int imgX = x + 10;
        int imgY = y + 10;
        int imgW = width - 20;
        int imgH = Math.min((int) (imgW * 0.6), height / 2 - 10);

        try {
            if (c.texture != null && !c.texture.isBlank()) {
                Identifier tex = new Identifier(c.texture);
                ctx.drawTexture(tex, imgX, imgY, 0, 0, imgW, imgH, imgW, imgH);
            }
        } catch (Exception ignored) {
        }

        int titleY = imgY + imgH + 10;
        ctx.drawCenteredTextWithShadow(textRenderer, c.title, x + width / 2, titleY, 0xFFFFFF);

        List<OrderedText> lines = textRenderer.wrapLines(Text.literal(c.description), width - 10);
        int textY = titleY + 20;

        for (OrderedText line : lines) {
            if (textY > y + height - 12) {
                break;
            }

            ctx.drawText(textRenderer, line, x + 5, textY, 0xAAAAAA, false);
            textY += 10;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (cards == null || cards.isEmpty()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int maxWidth = (int) (this.width * 0.9);
        int cardWidth = Math.min(MAX_CARD_WIDTH, (maxWidth - (cards.size() - 1) * GAP) / cards.size());
        int cardHeight = Math.min(MAX_CARD_HEIGHT, (int) (cardWidth * 1.3));

        int totalWidth = cards.size() * cardWidth + (cards.size() - 1) * GAP;
        int startX = (this.width - totalWidth) / 2;
        int y = (this.height - cardHeight) / 2;

        for (int i = 0; i < cards.size(); i++) {
            int x = startX + i * (cardWidth + GAP);

            boolean hovered = mouseX >= x && mouseX <= x + cardWidth
                    && mouseY >= y && mouseY <= y + cardHeight;

            if (hovered) {
                UpgradeClientPackets.sendSelect(i + 1);

                if (client != null) {
                    client.setScreen(null);
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}