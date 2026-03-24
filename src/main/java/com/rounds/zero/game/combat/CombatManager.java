package com.rounds.zero.game.combat;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.team.TeamId;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.*;

public class CombatManager {
    public static final String ROUNDS_BULLET_TAG = "rounds_zero_bullet";
    public static final String SHIELD_BLOCKED_TAG = "rounds_zero_shield_blocked";

    private static final Vector3f POISON_COLOR = new Vector3f(0.10f, 0.35f, 0.10f);
    private static final Vector3f RED_TEAM_COLOR = new Vector3f(0.95f, 0.20f, 0.20f);
    private static final Vector3f BLUE_TEAM_COLOR = new Vector3f(0.20f, 0.45f, 0.95f);

    private final Map<UUID, PlayerCombatData> playerCombatData = new HashMap<>();
    private final List<ActiveField> activeFields = new ArrayList<>();
    private final Random random = new Random();

    public PlayerCombatData getOrCreate(ServerPlayerEntity player) {
        return playerCombatData.computeIfAbsent(player.getUuid(), uuid -> new PlayerCombatData());
    }

    public void removePlayer(ServerPlayerEntity player) {
        playerCombatData.remove(player.getUuid());
    }

    public boolean isShieldActive(ServerPlayerEntity player) {
        PlayerCombatData data = playerCombatData.get(player.getUuid());
        return data != null && data.isShieldActive();
    }

    public CombatStats getStats(ServerPlayerEntity player) {
        return getOrCreate(player).getStats();
    }

    public boolean blockDamageWithShield(ServerPlayerEntity player) {
        PlayerCombatData data = playerCombatData.get(player.getUuid());
        if (data == null || !data.isShieldActive()) {
            return false;
        }

        data.setShieldActive(false);
        data.setShieldEndTick(0L);
        player.sendMessage(Text.literal("Щит заблокировал попадание.").formatted(Formatting.AQUA), true);
        return true;
    }

    public void preparePlayerForNewRound(ServerPlayerEntity player, CombatStats resolvedStats) {
        PlayerCombatData data = getOrCreate(player);

        data.setStats(resolvedStats);
        data.setCurrentAmmo(resolvedStats.getMaxAmmo());
        data.setReloading(false);
        data.setReloadEndTick(0L);
        data.setLastShotTick(-9999L);
        data.setShieldActive(false);
        data.setShieldEndTick(0L);
        data.setShieldCooldownEndTick(0L);

        applyResolvedStatsToPlayer(player, resolvedStats);
        sendCombatStatus(player, data, player.getServerWorld().getTime());
    }

    public void clearTemporaryState(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        data.setReloading(false);
        data.setReloadEndTick(0L);
        data.setShieldActive(false);
        data.setShieldEndTick(0L);
    }

    public void resetPlayerToDefault(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        CombatStats defaultStats = CombatStats.createDefault();

        data.setStats(defaultStats);
        data.setCurrentAmmo(defaultStats.getMaxAmmo());
        data.setReloading(false);
        data.setReloadEndTick(0L);
        data.setLastShotTick(-9999L);
        data.setShieldActive(false);
        data.setShieldEndTick(0L);
        data.setShieldCooldownEndTick(0L);

        applyResolvedStatsToPlayer(player, defaultStats);
    }

    private void applyResolvedStatsToPlayer(ServerPlayerEntity player, CombatStats stats) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(stats.getMaxHealth());
        }

        player.setHealth((float) stats.getMaxHealth());
    }

    public void handleShieldRequest(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        long now = player.getServerWorld().getTime();

        if (data.isShieldActive()) {
            player.sendMessage(Text.literal("Щит уже активен.").formatted(Formatting.AQUA), true);
            return;
        }

        if (now < data.getShieldCooldownEndTick()) {
            long remainingTicks = data.getShieldCooldownEndTick() - now;
            double seconds = remainingTicks / 20.0;
            player.sendMessage(
                    Text.literal(String.format("Щит в кулдауне: %.1f сек.", seconds)).formatted(Formatting.RED),
                    true
            );
            return;
        }

        CombatStats stats = data.getStats();
        data.setShieldActive(true);
        data.setShieldEndTick(now + stats.getShieldDurationTicks());
        data.setShieldCooldownEndTick(now + stats.getShieldCooldownTicks());

        if (stats.getHealingFieldLifetimeTicks() > 0) {
            spawnHealingField(player, stats, now);
        }

        player.sendMessage(Text.literal("Щит активирован.").formatted(Formatting.AQUA), true);
        sendCombatStatus(player, data, now);
    }

    public void handleReloadRequest(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        CombatStats stats = data.getStats();

        if (data.isReloading()) {
            player.sendMessage(Text.literal("Перезарядка уже идёт.").formatted(Formatting.YELLOW), true);
            return;
        }

        if (data.getCurrentAmmo() >= stats.getMaxAmmo()) {
            player.sendMessage(Text.literal("Магазин уже полный.").formatted(Formatting.GRAY), true);
            return;
        }

        long now = player.getServerWorld().getTime();
        data.setReloading(true);
        data.setReloadEndTick(now + stats.getReloadDurationTicks());

        player.sendMessage(Text.literal("Началась перезарядка...").formatted(Formatting.YELLOW), true);
        sendCombatStatus(player, data, now);
    }

    public void handleShootRequest(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        CombatStats stats = data.getStats();
        long now = player.getServerWorld().getTime();

        if (data.isReloading()) {
            player.sendMessage(Text.literal("Сейчас идёт перезарядка.").formatted(Formatting.YELLOW), true);
            return;
        }

        long ticksSinceLastShot = now - data.getLastShotTick();
        if (ticksSinceLastShot < stats.getShotCooldownTicks()) {
            return;
        }

        if (data.getCurrentAmmo() <= 0) {
            player.sendMessage(Text.literal("Магазин пуст. Нажми R для перезарядки.").formatted(Formatting.RED), true);
            return;
        }

        data.setCurrentAmmo(data.getCurrentAmmo() - 1);
        data.setLastShotTick(now);

        fireBullet(player, stats);
        player.swingHand(Hand.MAIN_HAND, true);
        sendCombatStatus(player, data, now);

        if (data.getCurrentAmmo() == 0) {
            player.sendMessage(Text.literal("Патроны закончились. Нажми R.").formatted(Formatting.GOLD), true);
        }
    }

    private void fireBullet(ServerPlayerEntity player, CombatStats stats) {
        ArrowEntity bullet = new ArrowEntity(player.getWorld(), player);
        bullet.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        bullet.setNoGravity(true);
        bullet.setCritical(false);
        bullet.setDamage(stats.getBulletDamage());
        bullet.addCommandTag(ROUNDS_BULLET_TAG);

        Vec3d rotation = player.getRotationVec(1.0f).normalize();
        Vec3d spawnPos = player.getEyePos().add(rotation.multiply(0.35));
        bullet.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
        bullet.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, (float) stats.getBulletSpeed(), 0.0f);

        player.getWorld().spawnEntity(bullet);
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                0.9f,
                1.4f
        );
    }

    public void handleProjectileHitEntity(PersistentProjectileEntity projectile, ServerPlayerEntity shooter, ServerPlayerEntity target) {
        if (!projectile.getCommandTags().contains(ROUNDS_BULLET_TAG)) {
            return;
        }

        CombatStats stats = getStats(shooter);
        boolean directHitWasBlockedByShield = projectile.getCommandTags().contains(SHIELD_BLOCKED_TAG);

        if (!directHitWasBlockedByShield) {
            if (stats.getIceBulletDurationTicks() > 0) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, stats.getIceBulletDurationTicks(), 1));
            }

            if (stats.getPoisonBulletDurationTicks() > 0) {
                target.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.POISON,
                        stats.getPoisonBulletDurationTicks(),
                        stats.getPoisonBulletAmplifier()
                ));
            }

            if (stats.getBlindnessChancePercent() > 0 && random.nextInt(100) < stats.getBlindnessChancePercent()) {
                target.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS,
                        stats.getBlindnessDurationTicks(),
                        0
                ));
            }
        }

        if (stats.getPoisonCloudLifetimeTicks() > 0) {
            spawnPoisonField(shooter, projectile.getWorld() instanceof ServerWorld serverWorld ? serverWorld : null, target.getPos(), stats, shooter.getServerWorld().getTime());
        }
    }

    public void handleProjectileHitBlock(PersistentProjectileEntity projectile, ServerPlayerEntity shooter, BlockPos blockPos) {
        if (!projectile.getCommandTags().contains(ROUNDS_BULLET_TAG)) {
            return;
        }

        CombatStats stats = getStats(shooter);
        if (stats.getPoisonCloudLifetimeTicks() <= 0) {
            return;
        }

        Vec3d center = Vec3d.ofCenter(blockPos);
        spawnPoisonField(shooter, shooter.getServerWorld(), center, stats, shooter.getServerWorld().getTime());
    }

    private void spawnHealingField(ServerPlayerEntity caster, CombatStats stats, long now) {
        activeFields.add(new ActiveField(
                caster.getServerWorld().getRegistryKey(),
                caster.getPos(),
                stats.getHealingFieldRadius(),
                now + stats.getHealingFieldLifetimeTicks(),
                stats.getHealingFieldEffectDurationTicks(),
                stats.getHealingFieldAmplifier(),
                FieldTargetMode.SELF_AND_ALLIES,
                FieldEffectType.HEALING,
                RoundsZero.GAME_MANAGER.getPlayerTeam(caster)
        ));
    }

    private void spawnPoisonField(ServerPlayerEntity caster, ServerWorld world, Vec3d center, CombatStats stats, long now) {
        if (world == null) {
            return;
        }

        activeFields.add(new ActiveField(
                world.getRegistryKey(),
                center,
                stats.getPoisonCloudRadius(),
                now + stats.getPoisonCloudLifetimeTicks(),
                stats.getPoisonCloudEffectDurationTicks(),
                stats.getPoisonCloudAmplifier(),
                FieldTargetMode.ALL,
                FieldEffectType.POISON,
                RoundsZero.GAME_MANAGER.getPlayerTeam(caster)
        ));
    }

    public void tick(MinecraftServer server) {
        long now = server.getOverworld().getTime();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerCombatData data = playerCombatData.get(player.getUuid());
            if (data == null) {
                continue;
            }

            if (data.isShieldActive() && now >= data.getShieldEndTick()) {
                data.setShieldActive(false);
                data.setShieldEndTick(0L);
                player.sendMessage(Text.literal("Щит закончился.").formatted(Formatting.DARK_AQUA), true);
            }

            if (data.isReloading() && now >= data.getReloadEndTick()) {
                data.setReloading(false);
                data.setReloadEndTick(0L);
                data.setCurrentAmmo(data.getStats().getMaxAmmo());
                player.sendMessage(
                        Text.literal("Перезарядка завершена. Патроны: " + data.getCurrentAmmo())
                                .formatted(Formatting.GREEN),
                        true
                );
            }

            sendCombatStatus(player, data, now);
        }

        tickFields(server, now);
    }

    private void tickFields(MinecraftServer server, long now) {
        Iterator<ActiveField> iterator = activeFields.iterator();
        while (iterator.hasNext()) {
            ActiveField field = iterator.next();
            ServerWorld world = server.getWorld(field.worldKey);
            if (world == null || now >= field.expireTick) {
                iterator.remove();
                continue;
            }

            spawnFieldParticles(world, field);

            if (now % 5L != 0L) {
                continue;
            }

            double radiusSquared = field.radius * field.radius;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getWorld() != world) {
                    continue;
                }

                if (player.squaredDistanceTo(field.center) > radiusSquared) {
                    continue;
                }

                if (!shouldAffect(field, player)) {
                    continue;
                }

                if (field.effectType == FieldEffectType.HEALING) {
                    applyEffectIfAbsent(player, new StatusEffectInstance(
                            StatusEffects.REGENERATION,
                            field.effectDurationTicks,
                            field.amplifier
                    ));
                } else if (field.effectType == FieldEffectType.POISON) {
                    applyEffectIfAbsent(player, new StatusEffectInstance(
                            StatusEffects.POISON,
                            field.effectDurationTicks,
                            field.amplifier
                    ));
                }
            }
        }
    }

    private void applyEffectIfAbsent(ServerPlayerEntity player, StatusEffectInstance effect) {
        StatusEffectInstance current = player.getStatusEffect(effect.getEffectType());

        if (current == null) {
            player.addStatusEffect(effect);
        }
    }

    private boolean shouldAffect(ActiveField field, ServerPlayerEntity player) {
        TeamId playerTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(player);

        return switch (field.targetMode) {
            case ALL -> true;
            case SELF_AND_ALLIES -> playerTeam == field.ownerTeam;
        };
    }

    private void spawnFieldParticles(ServerWorld world, ActiveField field) {
        Vector3f color = field.effectType == FieldEffectType.HEALING
                ? getTeamColor(field.ownerTeam)
                : POISON_COLOR;

        DustParticleEffect particle = new DustParticleEffect(color, 1.3f);
        for (int i = 0; i < 10; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = random.nextDouble() * field.radius;
            double x = field.center.x + Math.cos(angle) * distance;
            double y = field.center.y + 0.15 + (random.nextDouble() * 0.8);
            double z = field.center.z + Math.sin(angle) * distance;
            world.spawnParticles(particle, x, y, z, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private Vector3f getTeamColor(TeamId teamId) {
        if (teamId == TeamId.BLUE) {
            return BLUE_TEAM_COLOR;
        }
        return RED_TEAM_COLOR;
    }

    private void sendCombatStatus(ServerPlayerEntity player, PlayerCombatData data, long now) {
        player.sendMessage(buildCombatStatusText(data, now), true);
    }

    private MutableText buildCombatStatusText(PlayerCombatData data, long now) {
        MutableText ammoText;
        if (data.isReloading()) {
            ammoText = Text.literal("ПЕРЕЗАРЯДКА").formatted(Formatting.YELLOW);
        } else {
            ammoText = Text.literal("Патроны: " + data.getCurrentAmmo() + "/" + data.getStats().getMaxAmmo())
                    .formatted(Formatting.GOLD);
        }

        MutableText separator = Text.literal(" | ").formatted(Formatting.DARK_GRAY);

        MutableText shieldText;
        if (data.isShieldActive()) {
            shieldText = Text.literal("АКТИВЕН").formatted(Formatting.AQUA);
        } else if (now < data.getShieldCooldownEndTick()) {
            shieldText = Text.literal("ПЕРЕЗАРЯДКА").formatted(Formatting.RED);
        } else {
            shieldText = Text.literal("Щит: ГОТОВ").formatted(Formatting.GREEN);
        }

        return ammoText.append(separator).append(shieldText);
    }

    private enum FieldEffectType {
        HEALING,
        POISON
    }

    private enum FieldTargetMode {
        ALL,
        SELF_AND_ALLIES
    }

    private static final class ActiveField {
        private final net.minecraft.registry.RegistryKey<World> worldKey;
        private final Vec3d center;
        private final double radius;
        private final long expireTick;
        private final int effectDurationTicks;
        private final int amplifier;
        private final FieldTargetMode targetMode;
        private final FieldEffectType effectType;
        private final TeamId ownerTeam;

        private ActiveField(
                net.minecraft.registry.RegistryKey<World> worldKey,
                Vec3d center,
                double radius,
                long expireTick,
                int effectDurationTicks,
                int amplifier,
                FieldTargetMode targetMode,
                FieldEffectType effectType,
                TeamId ownerTeam
        ) {
            this.worldKey = worldKey;
            this.center = center;
            this.radius = radius;
            this.expireTick = expireTick;
            this.effectDurationTicks = effectDurationTicks;
            this.amplifier = amplifier;
            this.targetMode = targetMode;
            this.effectType = effectType;
            this.ownerTeam = ownerTeam;
        }
    }
}
