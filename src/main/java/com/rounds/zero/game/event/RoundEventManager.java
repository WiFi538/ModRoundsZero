package com.rounds.zero.game.event;

import com.rounds.zero.game.GameManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RoundEventManager {
    private static final int ROUND_EVENT_CHANCE_PERCENT = 10;
    private static final int EFFECT_DURATION_TICKS = 20 * 60 * 30;
    private static final int OLYMPUS_STRIKE_INTERVAL_TICKS = 20 * 7;

    private final Random random = new Random();
    private RoundEventType activeEvent;
    private double bulletSpeedMultiplier = 1.0;
    private long lastOlympusStrikeTick = 0L;
    private long lastInvasionSpawnTick = 0L;

    public RoundEventType getActiveEvent() {
        return activeEvent;
    }

    public boolean hasActiveEvent() {
        return activeEvent != null;
    }

    public double getBulletSpeedMultiplier() {
        return bulletSpeedMultiplier;
    }

    public void onRoundStart(MinecraftServer server, GameManager gameManager) {
        activeEvent = null;
        bulletSpeedMultiplier = 1.0;
        lastOlympusStrikeTick = 0L;
        lastInvasionSpawnTick = 0L;

        if (random.nextInt(100) >= ROUND_EVENT_CHANCE_PERCENT) {
            return;
        }

        RoundEventType[] pool = RoundEventType.values();
        activeEvent = pool[random.nextInt(pool.length)];

        if (activeEvent == RoundEventType.PARTICLE_ACCELERATION) {
            bulletSpeedMultiplier = 2.0;
        }

        long now = server.getOverworld().getTime();
        if (activeEvent == RoundEventType.OLYMPUS_WRATH) {
            lastOlympusStrikeTick = now;
        } else if (activeEvent == RoundEventType.INVASION) {
            lastInvasionSpawnTick = now;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!gameManager.hasTeam(player)) {
                continue;
            }
            sendEventAnnouncement(player, activeEvent);
        }
    }

    public void onRoundEnd(MinecraftServer server, GameManager gameManager) {
        if (activeEvent == null) {
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (gameManager.hasTeam(player)) {
                player.clearStatusEffects();
                gameManager.applyPersistentUpgradeEffects(player);
            }
        }

        activeEvent = null;
        bulletSpeedMultiplier = 1.0;
    }

    public void applyEventEffects(ServerPlayerEntity player) {
        if (activeEvent == null) {
            return;
        }

        switch (activeEvent) {
            case PARTICLE_ACCELERATION ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, EFFECT_DURATION_TICKS, 2));
            case DARKNESS ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_DURATION_TICKS, 0));
            case RADIATION ->
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, EFFECT_DURATION_TICKS, 0));
            case COSMONAUTS -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, EFFECT_DURATION_TICKS, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, EFFECT_DURATION_TICKS, 0));
            }
            case AURA -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, EFFECT_DURATION_TICKS, 0));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, EFFECT_DURATION_TICKS, 0));
            }
            case OLYMPUS_WRATH, INVASION -> {
            }
            default -> {
            }
        }
    }

    public void tick(MinecraftServer server, GameManager gameManager, long now) {
        if (activeEvent == null) {
            return;
        }

        switch (activeEvent) {
            case OLYMPUS_WRATH -> tickOlympusWrath(server, gameManager, now);
            case INVASION -> tickInvasion(server, gameManager, now);
            default -> {
            }
        }
    }

    private void tickOlympusWrath(MinecraftServer server, GameManager gameManager, long now) {
        if (now - lastOlympusStrikeTick < OLYMPUS_STRIKE_INTERVAL_TICKS) {
            return;
        }

        List<ServerPlayerEntity> alivePlayers = new ArrayList<>();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (gameManager.hasTeam(player) && gameManager.isAliveInRound(player) && !player.isSpectator()) {
                alivePlayers.add(player);
            }
        }

        if (alivePlayers.isEmpty()) {
            return;
        }

        ServerPlayerEntity target = alivePlayers.get(random.nextInt(alivePlayers.size()));
        ServerWorld world = target.getServerWorld();

        net.minecraft.entity.LightningEntity lightning = new net.minecraft.entity.LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setCosmetic(true);
        lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
        world.spawnEntity(lightning);

        target.damage(target.getDamageSources().magic(), 4.0f);
        lastOlympusStrikeTick = now;
    }

    private void tickInvasion(MinecraftServer server, GameManager gameManager, long now) {
        if (now - lastInvasionSpawnTick < 60L) {
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!gameManager.hasTeam(player) || !gameManager.isAliveInRound(player) || player.isSpectator()) {
                continue;
            }

            ServerWorld world = player.getServerWorld();
            ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, world);
            Vec3d pos = player.getPos().add(randomOffset3(), 0.0, randomOffset3());
            zombie.refreshPositionAndAngles(pos.x, pos.y, pos.z, world.random.nextFloat() * 360.0f, 0.0f);
            world.spawnEntity(zombie);
        }

        lastInvasionSpawnTick = now;
    }

    private double randomOffset3() {
        return (random.nextDouble() * 6.0) - 3.0;
    }

    private void sendEventAnnouncement(ServerPlayerEntity player, RoundEventType event) {
        Text title = Text.literal(event.getDisplayName()).formatted(Formatting.BOLD, Formatting.GOLD);
        TitleFadeS2CPacket fade = new TitleFadeS2CPacket(10, 70, 20);
        TitleS2CPacket titlePacket = new TitleS2CPacket(title);

        player.networkHandler.sendPacket(fade);
        player.networkHandler.sendPacket(titlePacket);
        player.sendMessage(
                Text.literal("Событие раунда: ")
                        .formatted(Formatting.YELLOW)
                        .append(Text.literal(event.getDisplayName()).formatted(Formatting.GOLD))
                        .append(Text.literal(" — ").formatted(Formatting.GRAY))
                        .append(Text.literal(event.getChatDescription()).formatted(Formatting.WHITE)),
                false
        );
    }
}
