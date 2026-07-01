package com.rounds.zero.client.screen;

import com.rounds.zero.network.PlayerStatsSnapshot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsScreen extends Screen {
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 220;
    private static final int PADDING = 12;
    private static final int LINE_HEIGHT = 10;
    private static final int BORDER = 2;

    private final PlayerStatsSnapshot snapshot;
    private final Screen parent;
    private final List<RenderedLine> lines = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int panelLeft;
    private int panelTop;
    private int contentTop;
    private int contentBottom;

    private record RenderedLine(String text, int color, boolean shadow) {
    }

    public PlayerStatsScreen(PlayerStatsSnapshot snapshot) {
        this(snapshot, null);
    }

    public PlayerStatsScreen(PlayerStatsSnapshot snapshot, Screen parent) {
        super(Text.literal("Характеристики"));
        this.snapshot = snapshot;
        this.parent = parent;
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    protected void init() {
        rebuildLines();
        layoutPanel();
    }

    @Override
    public void resize(net.minecraft.client.MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        layoutPanel();
    }

    private void rebuildLines() {
        lines.clear();

        lines.add(new RenderedLine("Карточки", 0xFFFFD966, true));

        if (snapshot.cards().isEmpty()) {
            lines.add(new RenderedLine("Пока нет карточек", 0xFFAAAAAA, false));
        } else {
            for (PlayerStatsSnapshot.CardEntry card : snapshot.cards()) {
                String suffix = card.count() > 1 ? " x" + card.count() : "";
                lines.add(new RenderedLine("• " + card.title() + suffix, 0xFFFFFFFF, false));
            }
        }

        lines.add(new RenderedLine("", 0xFFFFFFFF, false));

        for (String line : snapshot.statLines()) {
            if (line.isEmpty()) {
                lines.add(new RenderedLine("", 0xFFFFFFFF, false));
                continue;
            }

            boolean header = line.startsWith("== ");
            lines.add(new RenderedLine(
                    line,
                    header ? 0xFFFFD966 : 0xFFE8E8E8,
                    header
            ));
        }

        updateScrollBounds();
    }

    private void layoutPanel() {
        panelLeft = (width - PANEL_WIDTH) / 2;
        panelTop = (height - PANEL_HEIGHT) / 2;
        contentTop = panelTop + 28;
        contentBottom = panelTop + PANEL_HEIGHT - PADDING;
        updateScrollBounds();
    }

    private void updateScrollBounds() {
        int contentHeight = lines.size() * LINE_HEIGHT;
        int visibleHeight = Math.max(0, contentBottom - contentTop);
        maxScroll = Math.max(0, contentHeight - visibleHeight);
        scrollOffset = MathHelper.clamp(scrollOffset, 0, maxScroll);
    }

    @Override
    public boolean shouldPause() {
        return parent == null || parent.shouldPause();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseOverPanel(mouseX, mouseY)) {
            scrollOffset = MathHelper.clamp(scrollOffset - (int) (amount * LINE_HEIGHT), 0, maxScroll);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    private boolean isMouseOverPanel(double mouseX, double mouseY) {
        return mouseX >= panelLeft && mouseX <= panelLeft + PANEL_WIDTH
                && mouseY >= panelTop && mouseY <= panelTop + PANEL_HEIGHT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        context.fill(
                panelLeft - BORDER,
                panelTop - BORDER,
                panelLeft + PANEL_WIDTH + BORDER,
                panelTop + PANEL_HEIGHT + BORDER,
                0xFF555555
        );
        context.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + PANEL_HEIGHT, 0xF0101010);

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("Характеристики").formatted(Formatting.GOLD),
                width / 2,
                panelTop + 8,
                0xFFFFFF
        );

        context.enableScissor(panelLeft + PADDING, contentTop, panelLeft + PANEL_WIDTH - PADDING, contentBottom);

        int y = contentTop - scrollOffset;
        for (RenderedLine line : lines) {
            if (!line.text().isEmpty() && y + LINE_HEIGHT >= contentTop && y <= contentBottom) {
                if (line.shadow()) {
                    context.drawText(textRenderer, line.text(), panelLeft + PADDING, y, line.color(), true);
                } else {
                    context.drawText(textRenderer, line.text(), panelLeft + PADDING, y, line.color(), false);
                }
            }

            y += LINE_HEIGHT;
        }

        context.disableScissor();

        if (maxScroll > 0) {
            context.drawText(
                    textRenderer,
                    "Колёсико — прокрутка",
                    panelLeft + PADDING,
                    panelTop + PANEL_HEIGHT - 10,
                    0xFF777777,
                    false
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
