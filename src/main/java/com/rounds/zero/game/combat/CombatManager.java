package com.rounds.zero.game.combat;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.team.TeamId;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import java.util.*;

public class CombatManager {
    public static final String ROUNDS_BULLET_TAG = "rounds_zero_bullet";
    public static final String SHIELD_BLOCKED_TAG = "rounds_zero_shield_blocked";

    private static final Vector3f POISON_COLOR = new Vector3f(0.10f, 0.35f, 0.10f);
    private static final Vector3f RED_TEAM_COLOR = new Vector3f(0.95f, 0.20f, 0.20f);
    private static final Vector3f BLUE_TEAM_COLOR = new Vector3f(0.20f, 0.45f, 0.95f);
    private static final Vector3f GREEN_TEAM_COLOR = new Vector3f(0.20f, 0.85f, 0.30f);
    private static final Vector3f YELLOW_TEAM_COLOR = new Vector3f(0.95f, 0.90f, 0.20f);
    private static final Vector3f SHIELD_AURA_COLOR = new Vector3f(0.25f, 0.85f, 1.0f);

    private final Map<UUID, PlayerCombatData> playerCombatData = new HashMap<>();
    private final List<ActiveField> activeFields = new ArrayList<>();
    private final Map<UUID, Set<UUID>> summonerZombiesByOwner = new HashMap<>();
    private final Random random = new Random();

    private static final String CURSED_TAG = "rounds_zero_cursed";
    private static final String PARASITE_SILVERFISH_TAG = "rounds_zero_parasite_silverfish";
    private static final String SUMMONER_ZOMBIE_TAG = "rounds_zero_summoner_zombie";
    private static final String KABOOM_TAG = "rounds_zero_kaboom";

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
        data.setHealingFieldCooldownEndTick(0L);
        data.setHealingSurgeActive(false);

        applyResolvedStatsToPlayer(player, resolvedStats);
        sendCombatStatus(player, data, player.getServerWorld().getTime());
    }

    public void clearTemporaryState(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        data.setReloading(false);
        data.setReloadEndTick(0L);
        data.setShieldActive(false);
        data.setShieldEndTick(0L);
        data.setHealingSurgeActive(false);
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
        data.setHealingFieldCooldownEndTick(0L);
        data.setHealingSurgeActive(false);

        applyResolvedStatsToPlayer(player, defaultStats);
    }

    public void clearRoundProjectiles(MinecraftServer server) {
        activeFields.clear();
        summonerZombiesByOwner.clear();

        for (ServerWorld world : server.getWorlds()) {
            List<Entity> toRemove = new ArrayList<>();

            for (Entity entity : world.iterateEntities()) {
                if (entity.getCommandTags().contains(ROUNDS_BULLET_TAG)) {
                    toRemove.add(entity);
                }
            }

            for (Entity entity : toRemove) {
                entity.discard();
            }
        }
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

        if (stats.getHealingFieldLifetimeTicks() > 0 && now < data.getHealingFieldCooldownEndTick()) {
            long remainingTicks = data.getHealingFieldCooldownEndTick() - now;
            double seconds = remainingTicks / 20.0;
            player.sendMessage(
                    Text.literal(String.format("Лечение в кулдауне: %.1f сек.", seconds)).formatted(Formatting.RED),
                    true
            );
            return;
        }

        data.setShieldActive(true);
        data.setShieldEndTick(now + stats.getShieldDurationTicks());
        data.setShieldCooldownEndTick(now + stats.getShieldCooldownTicks());

        if (stats.isBombShield() && player.getServerWorld() != null) {
            // Extra cooldown is applied in addition to normal shield cooldown.
            data.setShieldCooldownEndTick(data.getShieldCooldownEndTick() + stats.getBombShieldExtraCooldownTicks());

            CombatExplosionHelper.explode(
                    player.getServerWorld(),
                    player,
                    player.getPos(),
                    stats.getBombShieldRadius(),
                    stats.getBombShieldDamage(),
                    CombatExplosionHelper.AllyDamageMode.SKIP_ALLIES
            );
        }

        if (stats.isSummoner()) {
            trySummonZombie(player, stats);
        }

        if (stats.getHealingFieldLifetimeTicks() > 0) {
            spawnHealingField(player, stats, now);
            data.setHealingFieldCooldownEndTick(now + stats.getHealingFieldCooldownTicks());
        }

        player.sendMessage(Text.literal("Щит активирован.").formatted(Formatting.AQUA), true);
        sendCombatStatus(player, data, now);
    }

    private void trySummonZombie(ServerPlayerEntity owner, CombatStats stats) {
        int limit = stats.getSummonerLimitPerPlayer();
        if (limit <= 0) {
            return;
        }

        Set<UUID> ids = summonerZombiesByOwner.computeIfAbsent(owner.getUuid(), k -> new HashSet<>());
        ids.removeIf(id -> owner.getServerWorld().getEntity(id) == null);

        if (ids.size() >= limit) {
            owner.sendMessage(Text.literal("Лимит зомби достигнут: " + limit).formatted(Formatting.RED), true);
            return;
        }

        ServerWorld world = owner.getServerWorld();
        ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, world);
        zombie.addCommandTag(SUMMONER_ZOMBIE_TAG);
        zombie.addCommandTag(SUMMONER_ZOMBIE_TAG + ":" + owner.getUuid());
        zombie.refreshPositionAndAngles(owner.getX(), owner.getY(), owner.getZ(), owner.getYaw(), 0.0f);
        zombie.setFireTicks(0);

        var attack = zombie.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attack != null) {
            attack.setBaseValue(stats.getSummonerZombieDamage());
        }

        world.spawnEntity(zombie);
        ids.add(zombie.getUuid());
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
            triggerAutoReload(player, data, now);
            return;
        }

        data.setCurrentAmmo(data.getCurrentAmmo() - 1);
        data.setLastShotTick(now);

        fireBullet(player, stats);
        player.swingHand(Hand.MAIN_HAND, true);
        sendCombatStatus(player, data, now);

        if (data.getCurrentAmmo() == 0) {
            triggerAutoReload(player, data, now);
        }
    }

    private void triggerAutoReload(ServerPlayerEntity player, PlayerCombatData data, long now) {
        if (data.isReloading()) {
            return;
        }
        data.setReloading(true);
        data.setReloadEndTick(now + data.getStats().getReloadDurationTicks());
        player.sendMessage(Text.literal("Патроны закончились. Автоперезарядка...").formatted(Formatting.GOLD), true);
    }

    private static final double TRIPLE_SHOT_SPACING = 0.35;
    private static final double TRIPLE_SHOT_ANGLE_SPREAD = Math.toRadians(6.0);

    private void fireBullet(ServerPlayerEntity player, CombatStats stats) {
        int projectileCount = Math.max(1, stats.getProjectilesPerShot());
        Vec3d lookDirection = player.getRotationVec(1.0f).normalize();
        Vec3d strafeAxis = lookDirection.crossProduct(new Vec3d(0.0, 1.0, 0.0));

        if (strafeAxis.lengthSquared() < 1.0E-6) {
            strafeAxis = lookDirection.crossProduct(new Vec3d(1.0, 0.0, 0.0));
        }

        strafeAxis = strafeAxis.normalize();

        for (int index = 0; index < projectileCount; index++) {
            double lateralOffset = (index - (projectileCount - 1) / 2.0) * TRIPLE_SHOT_SPACING;
            spawnBullet(player, stats, strafeAxis.multiply(lateralOffset), index, projectileCount);
        }

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

    private void spawnBullet(ServerPlayerEntity player, CombatStats stats, Vec3d lateralOffset, int shotIndex, int shotCount) {
        ShulkerBulletEntity bullet = EntityType.SHULKER_BULLET.create(player.getWorld());
        if (bullet == null) {
            return;
        }

        bullet.setOwner(player);
        bullet.setNoGravity(true);
        bullet.addCommandTag(ROUNDS_BULLET_TAG);
        if (stats.isKaboom()) {
            bullet.addCommandTag(KABOOM_TAG);
        }

        if (bullet instanceof com.rounds.zero.entity.RoundsBulletEntityAccess access) {
            access.roundsZero$setBulletDamage((float) stats.getBulletDamage());
        }

        Vec3d lookDirection = player.getRotationVec(1.0f).normalize();
        if (shotCount > 1) {
            double angleOffset = (shotIndex - (shotCount - 1) / 2.0) * TRIPLE_SHOT_ANGLE_SPREAD;
            lookDirection = rotateAroundY(lookDirection, angleOffset);
        }

        Vec3d spawnPos = player.getEyePos().add(lookDirection.multiply(0.35)).add(lateralOffset);
        bullet.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

        double bulletSpeed = stats.getBulletSpeed() * RoundsZero.GAME_MANAGER.getRoundEventManager().getBulletSpeedMultiplier();
        bullet.setVelocity(lookDirection.multiply(bulletSpeed));

        player.getWorld().spawnEntity(bullet);
    }

    private static Vec3d rotateAroundY(Vec3d vector, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3d(
                vector.x * cos - vector.z * sin,
                vector.y,
                vector.x * sin + vector.z * cos
        ).normalize();
    }

    public void handleProjectileHitEntity(Entity projectile, ServerPlayerEntity shooter, LivingEntity target, float baseDamage) {
        if (!projectile.getCommandTags().contains(ROUNDS_BULLET_TAG)) {
            return;
        }

        CombatStats stats = getStats(shooter);
        boolean directHitWasBlockedByShield = projectile.getCommandTags().contains(SHIELD_BLOCKED_TAG);
        boolean kaboom = projectile.getCommandTags().contains(KABOOM_TAG);

        if (!directHitWasBlockedByShield) {
            if (kaboom && shooter.getWorld() instanceof ServerWorld world) {
                CombatExplosionHelper.explode(
                        world,
                        shooter,
                        target.getPos(),
                        stats.getKaboomRadius(),
                        stats.getKaboomDamage(),
                        CombatExplosionHelper.AllyDamageMode.DAMAGE_ALL
                );
            }

            if (stats.isCursedBullet()) {
                target.addCommandTag(CURSED_TAG);
                if (stats.getCursedGlowDurationTicks() > 0) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, stats.getCursedGlowDurationTicks(), 0));
                }
            }

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

            if (stats.getFireOnHitDurationTicks() > 0) {
                target.setFireTicks(Math.max(target.getFireTicks(), stats.getFireOnHitDurationTicks()));
                if (stats.getFireOnHitExtraDamage() > 0.0f) {
                    target.damage(shooter.getDamageSources().inFire(), stats.getFireOnHitExtraDamage());
                }
                if (stats.isFireGhostSynergy() && shooter.getWorld() instanceof ServerWorld world) {
                    igniteArea3x3(world, BlockPos.ofFloored(target.getPos()));
                }
            }

            if (stats.isUnderSpeed() && isEnemyHit(shooter, target)) {
                shooter.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, stats.getUnderSpeedDurationTicks(), stats.getUnderSpeedAmplifier()));
            }

            if (stats.isDep() && roll(stats.getDepChancePercent())) {
                double base = baseDamage;
                if (random.nextBoolean()) {
                    double extra = base * stats.getDepTargetBonusMultiplier();
                    target.damage(shooter.getDamageSources().playerAttack(shooter), (float) extra);
                } else {
                    double selfExtra = base * stats.getDepSelfBonusMultiplier();
                    shooter.damage(shooter.getDamageSources().playerAttack(shooter), (float) selfExtra);
                }
            }

            if (stats.isTimeJump() && roll(stats.getTimeJumpChancePercent())) {
                tryTimeJump(target);
            }

            if (stats.isThor() && roll(stats.getThorChancePercent())) {
                spawnCosmeticLightningAndDamage(shooter, target, stats.getThorDamage());
            }

            if (stats.isJackpot() && roll(stats.getJackpotChancePercent())) {
                applyJackpot(shooter, target, stats.getJackpotDurationTicks());
            }

            if (stats.isDepJackpotSynergy() && target instanceof ServerPlayerEntity && roll(3)) {
                target.damage(shooter.getDamageSources().outOfWorld(), Float.MAX_VALUE);
            }
        }

        if (stats.getPoisonCloudLifetimeTicks() > 0) {
            spawnPoisonField(shooter, projectile.getWorld() instanceof ServerWorld serverWorld ? serverWorld : null, target.getPos(), stats, shooter.getServerWorld().getTime());
        }
    }

    private boolean isEnemyHit(ServerPlayerEntity shooter, LivingEntity target) {
        if (target instanceof ServerPlayerEntity targetPlayer) {
            TeamId shooterTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(shooter);
            TeamId targetTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(targetPlayer);
            return shooterTeam == TeamId.NONE || targetTeam == TeamId.NONE || shooterTeam != targetTeam;
        }
        return true;
    }

    private boolean roll(int percent) {
        return percent > 0 && random.nextInt(100) < percent;
    }

    private void tryTimeJump(LivingEntity target) {
        Vec3d[] offsets = new Vec3d[] {
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(0, 2, 0)
        };

        Vec3d offset = offsets[random.nextInt(offsets.length)];
        Vec3d dest = target.getPos().add(offset);

        if (target.getWorld() instanceof ServerWorld world) {
            BlockPos bp = BlockPos.ofFloored(dest);
            if (!world.getBlockState(bp).getCollisionShape(world, bp).isEmpty()) {
                return;
            }
        }

        target.requestTeleport(dest.x, dest.y, dest.z);
    }

    private void spawnCosmeticLightningAndDamage(ServerPlayerEntity shooter, LivingEntity target, float damage) {
        if (!(target.getWorld() instanceof ServerWorld world)) {
            return;
        }

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setCosmetic(true);
        lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
        world.spawnEntity(lightning);

        if (damage > 0.0f) {
            DamageSource source = shooter.getDamageSources().playerAttack(shooter);
            target.damage(source, damage);
        }
    }

    private void applyJackpot(ServerPlayerEntity shooter, LivingEntity target, int durationTicks) {
        if (durationTicks <= 0) {
            return;
        }

        LivingEntity receiver = random.nextBoolean() ? shooter : target;
        StatusEffectInstance effect = pickJackpotEffect(durationTicks);
        if (effect != null) {
            receiver.addStatusEffect(effect);
        }
    }

    private StatusEffectInstance pickJackpotEffect(int durationTicks) {
        StatusEffectInstance[] effects = new StatusEffectInstance[] {
                new StatusEffectInstance(StatusEffects.SPEED, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.SLOWNESS, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 0),
                new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 0),
                new StatusEffectInstance(StatusEffects.JUMP_BOOST, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.NAUSEA, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.REGENERATION, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.RESISTANCE, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.WATER_BREATHING, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.INVISIBILITY, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.BLINDNESS, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.POISON, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.WITHER, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.HEALTH_BOOST, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.GLOWING, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.LEVITATION, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.SLOW_FALLING, durationTicks, 0),
                new StatusEffectInstance(StatusEffects.DARKNESS, durationTicks, 0)
        };

        return effects[random.nextInt(effects.length)];
    }

    public void spawnParasiteSilverfish(LivingEntity from) {
        spawnParasiteSilverfish(from, null);
    }

    public void spawnParasiteSilverfish(LivingEntity from, ServerPlayerEntity owner) {
        if (!(from.getWorld() instanceof ServerWorld world)) {
            return;
        }

        SilverfishEntity fish = new SilverfishEntity(EntityType.SILVERFISH, world);
        fish.addCommandTag(PARASITE_SILVERFISH_TAG);
        if (owner != null) {
            fish.addCommandTag(PARASITE_SILVERFISH_TAG + ":" + owner.getUuid());
        }
        fish.refreshPositionAndAngles(from.getX(), from.getY(), from.getZ(), world.random.nextFloat() * 360.0f, 0.0f);

        var attack = fish.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attack != null) {
            attack.setBaseValue(4.0);
        }

        world.spawnEntity(fish);
    }

    public void handleSummonerZombieDeath(ZombieEntity zombie) {
        ServerPlayerEntity owner = resolveSummonerOwner(zombie);
        if (owner == null || !RoundsZero.GAME_MANAGER.playerHasParasiteSummonerSynergy(owner)) {
            return;
        }

        spawnParasiteSilverfish(zombie, owner);
    }

    public void handleSummonerZombieKilledPlayer(ZombieEntity zombie, ServerPlayerEntity victim) {
        ServerPlayerEntity owner = resolveSummonerOwner(zombie);
        if (owner == null || !RoundsZero.GAME_MANAGER.playerHasParasiteSummonerSynergy(owner)) {
            return;
        }

        spawnParasiteSilverfish(victim, owner);
    }

    private ServerPlayerEntity resolveSummonerOwner(ZombieEntity zombie) {
        for (String tag : zombie.getCommandTags()) {
            if (!tag.startsWith(SUMMONER_ZOMBIE_TAG + ":")) {
                continue;
            }

            try {
                UUID ownerId = UUID.fromString(tag.substring((SUMMONER_ZOMBIE_TAG + ":").length()));
                if (zombie.getServer() == null) {
                    return null;
                }
                return zombie.getServer().getPlayerManager().getPlayer(ownerId);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return null;
    }

    public void handleProjectileHitBlock(Entity projectile, ServerPlayerEntity shooter, BlockPos blockPos) {
        if (!projectile.getCommandTags().contains(ROUNDS_BULLET_TAG)) {
            return;
        }

        CombatStats stats = getStats(shooter);

        if (projectile.getCommandTags().contains(KABOOM_TAG) && shooter.getWorld() instanceof ServerWorld world) {
            CombatExplosionHelper.explode(
                    world,
                    shooter,
                    Vec3d.ofCenter(blockPos),
                    stats.getKaboomRadius(),
                    stats.getKaboomDamage(),
                    CombatExplosionHelper.AllyDamageMode.DAMAGE_ALL
            );
        }

        if (stats.getPoisonCloudLifetimeTicks() <= 0) {
            if (stats.getFireOnHitDurationTicks() > 0 && stats.isFireGhostSynergy()) {
                igniteArea3x3(shooter.getServerWorld(), blockPos);
            }
            return;
        }

        Vec3d center = Vec3d.ofCenter(blockPos);
        spawnPoisonField(shooter, shooter.getServerWorld(), center, stats, shooter.getServerWorld().getTime());
        if (stats.getFireOnHitDurationTicks() > 0 && stats.isFireGhostSynergy()) {
            igniteArea3x3(shooter.getServerWorld(), blockPos);
        }
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
                RoundsZero.GAME_MANAGER.getPlayerTeam(caster),
                stats.isHealingFieldSurge()
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
                RoundsZero.GAME_MANAGER.getPlayerTeam(caster),
                false
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

            if (data.isShieldActive()) {
                spawnShieldAura(player);
            }

            sendCombatStatus(player, data, now);
        }

        tickSummonerZombies(server);
        tickFields(server, now);
    }

    private void igniteArea3x3(ServerWorld world, BlockPos center) {
        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos basePos = center.add(dx, 0, dz);
                BlockPos firePos = basePos.up();

                if (!world.getBlockState(firePos).isAir()) {
                    continue;
                }

                if (net.minecraft.block.FireBlock.canPlaceAt(world, firePos, net.minecraft.util.math.Direction.UP)) {
                    world.setBlockState(firePos, net.minecraft.block.Blocks.FIRE.getDefaultState(), 3);
                }
            }
        }
    }

    private void spawnShieldAura(ServerPlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }

        DustParticleEffect auraParticle = new DustParticleEffect(SHIELD_AURA_COLOR, 1.2f);
        double centerY = player.getY() + 1.0;
        double radius = 0.75;
        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2.0 * i) / 12.0;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            world.spawnParticles(auraParticle, x, centerY, z, 1, 0.02, 0.1, 0.02, 0.0);
        }
        world.spawnParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.1, player.getZ(), 3, 0.25, 0.35, 0.25, 0.01);
    }

    private void tickSummonerZombies(MinecraftServer server) {
        if (summonerZombiesByOwner.isEmpty()) {
            return;
        }

        for (var entry : summonerZombiesByOwner.entrySet()) {
            UUID ownerId = entry.getKey();
            ServerPlayerEntity owner = server.getPlayerManager().getPlayer(ownerId);

            Set<UUID> zombieIds = entry.getValue();
            zombieIds.removeIf(id -> {
                for (ServerWorld world : server.getWorlds()) {
                    Entity e = world.getEntity(id);
                    if (e == null) {
                        continue;
                    }
                    if (!(e instanceof ZombieEntity zombie) || !zombie.isAlive()) {
                        return true;
                    }
                    if (zombie.getTarget() != null && zombie.getTarget().getUuid().equals(ownerId)) {
                        zombie.setTarget(null);
                    }
                    zombie.setFireTicks(0);
                    return false;
                }
                return true;
            });
        }

        summonerZombiesByOwner.entrySet().removeIf(e -> e.getValue().isEmpty());
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
                    if (field.healingSurge) {
                        // Health boost should persist until round end or death.
                        PlayerCombatData data = playerCombatData.get(player.getUuid());
                        if (data != null && !data.isHealingSurgeActive()) {
                            data.setHealingSurgeActive(true);
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, Integer.MAX_VALUE, 0));
                            player.heal(4.0f); // immediate +2 hearts as a "surge" feeling
                        }
                    }
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
        return switch (teamId) {
            case BLUE -> BLUE_TEAM_COLOR;
            case GREEN -> GREEN_TEAM_COLOR;
            case YELLOW -> YELLOW_TEAM_COLOR;
            default -> RED_TEAM_COLOR;
        };
    }

    private void sendCombatStatus(ServerPlayerEntity player, PlayerCombatData data, long now) {
        player.sendMessage(buildCombatStatusText(data, now), true);
    }

    private MutableText buildCombatStatusText(PlayerCombatData data, long now) {
        MutableText ammoText;
        if (data.isReloading()) {
            ammoText = Text.literal("Перезарядка: ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(formatSecondsRemaining(data.getReloadEndTick(), now) + "с").formatted(Formatting.GOLD));
        } else {
            ammoText = Text.literal("Патроны: " + data.getCurrentAmmo() + "/" + data.getStats().getMaxAmmo())
                    .formatted(Formatting.GOLD);
        }

        MutableText separator = Text.literal(" | ").formatted(Formatting.DARK_GRAY);
        MutableText shieldText = buildShieldStatusText(data, now);

        return ammoText.append(separator).append(shieldText);
    }

    private MutableText buildShieldStatusText(PlayerCombatData data, long now) {
        CombatStats stats = data.getStats();

        if (data.isShieldActive()) {
            return Text.literal("Щит: ")
                    .formatted(Formatting.AQUA)
                    .append(Text.literal(formatSecondsRemaining(data.getShieldEndTick(), now) + "с").formatted(Formatting.WHITE));
        }

        long shieldReadyTick = data.getShieldCooldownEndTick();
        if (stats.getHealingFieldLifetimeTicks() > 0) {
            shieldReadyTick = Math.max(shieldReadyTick, data.getHealingFieldCooldownEndTick());
        }

        if (now < shieldReadyTick) {
            return Text.literal("Щит: ")
                    .formatted(Formatting.RED)
                    .append(Text.literal(formatSecondsRemaining(shieldReadyTick, now) + "с").formatted(Formatting.GOLD));
        }

        return Text.literal("Щит: ГОТОВ").formatted(Formatting.GREEN);
    }

    private static String formatSecondsRemaining(long endTick, long now) {
        long remainingTicks = Math.max(0L, endTick - now);
        return String.format("%.1f", remainingTicks / 20.0);
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
        private final boolean healingSurge;

        private ActiveField(
                net.minecraft.registry.RegistryKey<World> worldKey,
                Vec3d center,
                double radius,
                long expireTick,
                int effectDurationTicks,
                int amplifier,
                FieldTargetMode targetMode,
                FieldEffectType effectType,
                TeamId ownerTeam,
                boolean healingSurge
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
            this.healingSurge = healingSurge;
        }
    }
}
