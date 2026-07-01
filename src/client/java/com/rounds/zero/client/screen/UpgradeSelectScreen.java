package com.rounds.zero.client.screen;

import com.rounds.zero.client.network.StatsClientPackets;
import com.rounds.zero.client.network.UpgradeClientPackets;
import com.rounds.zero.client.util.MarkupTextParser;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class UpgradeSelectScreen extends Screen {

    public record Card(String id, String title, String description, CardCategory category) {
    }

    public enum CardCategory {
        WEAPON(0xFFE6B800, 0xFFFFE066),
        HEALTH(0xFF44CC44, 0xFF66FF66),
        BULLET_EFFECT(0xFFAA55FF, 0xFFCC99FF),
        SHIELD_EFFECT(0xFF44BBFF, 0xFF77DDFF);

        private final int borderColor;
        private final int hoverBorderColor;

        CardCategory(int borderColor, int hoverBorderColor) {
            this.borderColor = borderColor;
            this.hoverBorderColor = hoverBorderColor;
        }

        public int getBorderColor(boolean hovered) {
            return hovered ? hoverBorderColor : borderColor;
        }

        public static CardCategory fromWireId(String wireId) {
            if (wireId == null) {
                return WEAPON;
            }

            return switch (wireId) {
                case "health" -> HEALTH;
                case "bullet" -> BULLET_EFFECT;
                case "shield" -> SHIELD_EFFECT;
                default -> WEAPON;
            };
        }
    }

    private static final int BORDER = 2;
    private static final int ABSOLUTE_MIN_CARD_WIDTH = 52;
    private static final int PREFERRED_MAX_CARD_WIDTH = 200;
    private static final int MIN_CARD_HEIGHT = 108;
    private static final int EXTRA_CARD_HEIGHT_PADDING = 14;

    private final List<Card> cards;
    private final long choiceUnlockTick;
    private List<CardLayout> layouts = List.of();
    private RowMetrics rowMetrics = RowMetrics.empty();
    private int lastLayoutWidth = -1;
    private int lastLayoutHeight = -1;

    private record CardLayout(int index, int x, int y, int width, int height) {
    }

    private record RowMetrics(int gap, int padding, int lineHeight, int cardWidth, int cardHeight, int startX, int startY) {
        static RowMetrics empty() {
            return new RowMetrics(0, 0, 10, 0, 0, 0, 0);
        }
    }

    public UpgradeSelectScreen(List<Card> cards, long choiceUnlockTick) {
        super(Text.literal("Выбор улучшения"));
        this.cards = cards;
        this.choiceUnlockTick = choiceUnlockTick;
    }

    private boolean isChoiceLocked() {
        if (client == null || client.world == null) {
            return true;
        }

        return client.world.getTime() < choiceUnlockTick;
    }

    private int getSecondsUntilUnlock() {
        if (client == null || client.world == null) {
            return 2;
        }

        long ticksLeft = choiceUnlockTick - client.world.getTime();
        return Math.max(1, (int) Math.ceil(ticksLeft / 20.0));
    }

    @Override
    protected void init() {
        rebuildLayouts();

        int buttonWidth = 136;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Характеристики (I)"),
                button -> StatsClientPackets.requestStats(this)
        ).dimensions(width - buttonWidth - 8, 6, buttonWidth, 20).build());
    }

    @Override
    public void resize(net.minecraft.client.MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        rebuildLayouts();
    }

    private void rebuildLayouts() {
        rowMetrics = computeRowMetrics();
        layouts = buildCardLayouts(rowMetrics);
        lastLayoutWidth = width;
        lastLayoutHeight = height;
    }

    private void ensureLayoutsUpToDate() {
        if (width != lastLayoutWidth || height != lastLayoutHeight) {
            rebuildLayouts();
        }
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

        ensureLayoutsUpToDate();

        int headerY = Math.max(12, rowMetrics.startY() - 24);
        if (isChoiceLocked()) {
            ctx.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.literal("Выбор через " + getSecondsUntilUnlock() + " сек...")
                            .formatted(Formatting.YELLOW),
                    width / 2,
                    headerY,
                    0xFFFFFF
            );
        } else {
            ctx.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.literal("Выбор улучшения").formatted(Formatting.GOLD),
                    width / 2,
                    headerY,
                    0xFFFFFF
            );
        }

        ctx.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("I — ваши характеристики").formatted(Formatting.GRAY),
                width / 2,
                headerY + 12,
                0xFFAAAAAA
        );

        if (cards == null || cards.isEmpty()) {
            super.render(ctx, mouseX, mouseY, delta);
            return;
        }

        for (CardLayout layout : layouts) {
            Card card = cards.get(layout.index);
            boolean hovered = mouseX >= layout.x
                    && mouseX <= layout.x + layout.width
                    && mouseY >= layout.y
                    && mouseY <= layout.y + layout.height;
            drawCard(ctx, card, layout, hovered);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawCard(DrawContext ctx, Card card, CardLayout layout, boolean hovered) {
        int borderColor = card.category().getBorderColor(hovered);

        ctx.fill(
                layout.x - BORDER,
                layout.y - BORDER,
                layout.x + layout.width + BORDER,
                layout.y + layout.height + BORDER,
                borderColor
        );
        ctx.fill(layout.x, layout.y, layout.x + layout.width, layout.y + layout.height, 0xF0101010);

        int innerWidth = Math.max(8, layout.width - rowMetrics.padding() * 2);
        List<OrderedText> titleLines = textRenderer.wrapLines(Text.literal(card.title()), innerWidth);
        List<OrderedText> descriptionLines = textRenderer.wrapLines(MarkupTextParser.parse(card.description()), innerWidth);

        int titleHeight = titleLines.size() * rowMetrics.lineHeight();
        int descriptionHeight = descriptionLines.size() * rowMetrics.lineHeight();
        int contentHeight = titleHeight + 6 + descriptionHeight;
        int contentStartY = layout.y + (layout.height - contentHeight) / 2;

        int titleY = contentStartY;
        for (OrderedText line : titleLines) {
            int lineWidth = textRenderer.getWidth(line);
            ctx.drawText(
                    textRenderer,
                    line,
                    layout.x + (layout.width - lineWidth) / 2,
                    titleY,
                    0xFFFFFF,
                    true
            );
            titleY += rowMetrics.lineHeight();
        }

        int descriptionY = titleY + 6;
        for (OrderedText line : descriptionLines) {
            int lineWidth = textRenderer.getWidth(line);
            ctx.drawText(
                    textRenderer,
                    line,
                    layout.x + (layout.width - lineWidth) / 2,
                    descriptionY,
                    0xFFBBBBBB,
                    false
            );
            descriptionY += rowMetrics.lineHeight();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || cards == null || cards.isEmpty() || isChoiceLocked()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        ensureLayoutsUpToDate();

        for (CardLayout layout : layouts) {
            if (mouseX >= layout.x
                    && mouseX <= layout.x + layout.width
                    && mouseY >= layout.y
                    && mouseY <= layout.y + layout.height) {
                UpgradeClientPackets.sendSelect(layout.index + 1);

                if (client != null) {
                    client.setScreen(null);
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private RowMetrics computeRowMetrics() {
        if (cards == null || cards.isEmpty()) {
            return RowMetrics.empty();
        }

        int count = cards.size();
        int sideMargin = Math.max(8, width / 48);
        int availableWidth = width - sideMargin * 2;

        int gap = count >= 5 ? 6 : (count >= 4 ? 8 : 10);
        int cardWidth = fitCardWidth(count, availableWidth, gap);

        while (cardWidth < ABSOLUTE_MIN_CARD_WIDTH && gap > 2) {
            gap -= 2;
            cardWidth = fitCardWidth(count, availableWidth, gap);
        }

        cardWidth = Math.max(ABSOLUTE_MIN_CARD_WIDTH, cardWidth);

        int padding = cardWidth < 72 ? 5 : (cardWidth < 100 ? 7 : (cardWidth < 130 ? 9 : 11));
        int lineHeight = cardWidth < 72 ? 9 : 10;

        int cardHeight = estimateUniformCardHeight(cardWidth, padding, lineHeight) + EXTRA_CARD_HEIGHT_PADDING;
        int maxHeight = Math.max(MIN_CARD_HEIGHT + 24, (int) (height * 0.62));
        cardHeight = Math.max(MIN_CARD_HEIGHT, Math.min(cardHeight, maxHeight));

        int totalRowWidth = count * cardWidth + (count - 1) * gap;
        int startX = (width - totalRowWidth) / 2;
        int startY = (height - cardHeight) / 2 + 8;

        return new RowMetrics(gap, padding, lineHeight, cardWidth, cardHeight, startX, startY);
    }

    private static int fitCardWidth(int count, int availableWidth, int gap) {
        int usable = availableWidth - (count - 1) * gap - BORDER * 2;
        return Math.max(ABSOLUTE_MIN_CARD_WIDTH, usable / count);
    }

    private List<CardLayout> buildCardLayouts(RowMetrics metrics) {
        if (cards == null || cards.isEmpty()) {
            return List.of();
        }

        List<CardLayout> result = new ArrayList<>(cards.size());
        for (int i = 0; i < cards.size(); i++) {
            int x = metrics.startX() + i * (metrics.cardWidth() + metrics.gap());
            result.add(new CardLayout(i, x, metrics.startY(), metrics.cardWidth(), metrics.cardHeight()));
        }

        return result;
    }

    private int estimateUniformCardHeight(int cardWidth, int padding, int lineHeight) {
        int innerWidth = Math.max(8, cardWidth - padding * 2);
        int maxContentHeight = 0;

        for (Card card : cards) {
            int titleLines = textRenderer.wrapLines(Text.literal(card.title()), innerWidth).size();
            int descriptionLines = textRenderer.wrapLines(MarkupTextParser.parse(card.description()), innerWidth).size();
            int contentHeight = titleLines * lineHeight + 8 + descriptionLines * lineHeight;
            maxContentHeight = Math.max(maxContentHeight, contentHeight);
        }

        return maxContentHeight + padding * 2 + 6;
    }
}
