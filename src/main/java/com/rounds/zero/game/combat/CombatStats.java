package com.rounds.zero.game.combat;

public class CombatStats {
    private int maxAmmo = 3;

    private long shotCooldownTicks = 20L;
    private long reloadDurationTicks = 60L;

    private double bulletDamage = 4.0;
    private double bulletSpeed = 1.65;
    private double bulletSize = 1.0;

    private long shieldDurationTicks = 40L;
    private long shieldCooldownTicks = 100L;

    private double maxHealth = 20.0;

    private int iceBulletDurationTicks = 0;
    private int poisonBulletDurationTicks = 0;
    private int poisonBulletAmplifier = 0;

    private int poisonCloudRadius = 0;
    private int poisonCloudLifetimeTicks = 0;
    private int poisonCloudEffectDurationTicks = 0;
    private int poisonCloudAmplifier = 0;

    private int blindnessChancePercent = 0;
    private int blindnessDurationTicks = 0;

    private int healingFieldRadius = 0;
    private int healingFieldLifetimeTicks = 0;
    private int healingFieldEffectDurationTicks = 0;
    private int healingFieldAmplifier = 0;
    private long healingFieldCooldownTicks = 0L;

    private int projectilesPerShot = 1;
    private int fireOnHitDurationTicks = 0;
    private float fireOnHitExtraDamage = 0.0f;

    private boolean cursedBullet = false;
    private int cursedGlowDurationTicks = 0;
    private float cursedExplosionPower = 0.0f;

    private boolean parasite = false;

    private boolean jackpot = false;
    private int jackpotChancePercent = 0;
    private int jackpotDurationTicks = 0;

    private boolean thor = false;
    private int thorChancePercent = 0;
    private float thorDamage = 0.0f;

    private boolean underSpeed = false;
    private int underSpeedDurationTicks = 0;
    private int underSpeedAmplifier = 0;

    private boolean dep = false;
    private int depChancePercent = 0;
    private double depTargetBonusMultiplier = 1.0;
    private double depSelfBonusMultiplier = 0.0;

    private boolean timeJump = false;
    private int timeJumpChancePercent = 0;

    private boolean summoner = false;
    private int summonerLimitPerPlayer = 0;
    private float summonerZombieDamage = 0.0f;

    private boolean parasiteSummonerSynergy = false;
    private boolean depJackpotSynergy = false;
    private boolean healingFieldSurge = false;

    private boolean ghostRider = false;

    private boolean bombShield = false;
    private float bombShieldDamage = 10.0f;
    private double bombShieldRadius = 1.5;
    private long bombShieldExtraCooldownTicks = 100L;

    private boolean kaboom = false;
    private float kaboomDamage = 2.0f;
    private double kaboomRadius = 1.5;
    private boolean fireGhostSynergy = false;

    public static CombatStats createDefault() {
        return new CombatStats();
    }

    public CombatStats copy() {
        CombatStats copy = new CombatStats();
        copy.maxAmmo = this.maxAmmo;
        copy.shotCooldownTicks = this.shotCooldownTicks;
        copy.reloadDurationTicks = this.reloadDurationTicks;
        copy.bulletDamage = this.bulletDamage;
        copy.bulletSpeed = this.bulletSpeed;
        copy.bulletSize = this.bulletSize;
        copy.shieldDurationTicks = this.shieldDurationTicks;
        copy.shieldCooldownTicks = this.shieldCooldownTicks;
        copy.maxHealth = this.maxHealth;
        copy.iceBulletDurationTicks = this.iceBulletDurationTicks;
        copy.poisonBulletDurationTicks = this.poisonBulletDurationTicks;
        copy.poisonBulletAmplifier = this.poisonBulletAmplifier;
        copy.poisonCloudRadius = this.poisonCloudRadius;
        copy.poisonCloudLifetimeTicks = this.poisonCloudLifetimeTicks;
        copy.poisonCloudEffectDurationTicks = this.poisonCloudEffectDurationTicks;
        copy.poisonCloudAmplifier = this.poisonCloudAmplifier;
        copy.blindnessChancePercent = this.blindnessChancePercent;
        copy.blindnessDurationTicks = this.blindnessDurationTicks;
        copy.healingFieldRadius = this.healingFieldRadius;
        copy.healingFieldLifetimeTicks = this.healingFieldLifetimeTicks;
        copy.healingFieldEffectDurationTicks = this.healingFieldEffectDurationTicks;
        copy.healingFieldAmplifier = this.healingFieldAmplifier;
        copy.healingFieldCooldownTicks = this.healingFieldCooldownTicks;
        copy.projectilesPerShot = this.projectilesPerShot;
        copy.fireOnHitDurationTicks = this.fireOnHitDurationTicks;
        copy.fireOnHitExtraDamage = this.fireOnHitExtraDamage;
        copy.cursedBullet = this.cursedBullet;
        copy.cursedGlowDurationTicks = this.cursedGlowDurationTicks;
        copy.cursedExplosionPower = this.cursedExplosionPower;
        copy.parasite = this.parasite;
        copy.jackpot = this.jackpot;
        copy.jackpotChancePercent = this.jackpotChancePercent;
        copy.jackpotDurationTicks = this.jackpotDurationTicks;
        copy.thor = this.thor;
        copy.thorChancePercent = this.thorChancePercent;
        copy.thorDamage = this.thorDamage;
        copy.underSpeed = this.underSpeed;
        copy.underSpeedDurationTicks = this.underSpeedDurationTicks;
        copy.underSpeedAmplifier = this.underSpeedAmplifier;
        copy.dep = this.dep;
        copy.depChancePercent = this.depChancePercent;
        copy.depTargetBonusMultiplier = this.depTargetBonusMultiplier;
        copy.depSelfBonusMultiplier = this.depSelfBonusMultiplier;
        copy.timeJump = this.timeJump;
        copy.timeJumpChancePercent = this.timeJumpChancePercent;
        copy.summoner = this.summoner;
        copy.summonerLimitPerPlayer = this.summonerLimitPerPlayer;
        copy.summonerZombieDamage = this.summonerZombieDamage;
        copy.parasiteSummonerSynergy = this.parasiteSummonerSynergy;
        copy.depJackpotSynergy = this.depJackpotSynergy;
        copy.healingFieldSurge = this.healingFieldSurge;
        copy.ghostRider = this.ghostRider;
        copy.bombShield = this.bombShield;
        copy.bombShieldDamage = this.bombShieldDamage;
        copy.bombShieldRadius = this.bombShieldRadius;
        copy.bombShieldExtraCooldownTicks = this.bombShieldExtraCooldownTicks;
        copy.kaboom = this.kaboom;
        copy.kaboomDamage = this.kaboomDamage;
        copy.kaboomRadius = this.kaboomRadius;
        copy.fireGhostSynergy = this.fireGhostSynergy;
        return copy;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public long getShotCooldownTicks() {
        return shotCooldownTicks;
    }

    public void setShotCooldownTicks(long shotCooldownTicks) {
        this.shotCooldownTicks = shotCooldownTicks;
    }

    public long getReloadDurationTicks() {
        return reloadDurationTicks;
    }

    public void setReloadDurationTicks(long reloadDurationTicks) {
        this.reloadDurationTicks = reloadDurationTicks;
    }

    public double getBulletDamage() {
        return bulletDamage;
    }

    public void setBulletDamage(double bulletDamage) {
        this.bulletDamage = bulletDamage;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(double bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public double getBulletSize() {
        return bulletSize;
    }

    public void setBulletSize(double bulletSize) {
        this.bulletSize = bulletSize;
    }

    public long getShieldDurationTicks() {
        return shieldDurationTicks;
    }

    public void setShieldDurationTicks(long shieldDurationTicks) {
        this.shieldDurationTicks = shieldDurationTicks;
    }

    public long getShieldCooldownTicks() {
        return shieldCooldownTicks;
    }

    public void setShieldCooldownTicks(long shieldCooldownTicks) {
        this.shieldCooldownTicks = shieldCooldownTicks;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getIceBulletDurationTicks() {
        return iceBulletDurationTicks;
    }

    public void setIceBulletDurationTicks(int iceBulletDurationTicks) {
        this.iceBulletDurationTicks = iceBulletDurationTicks;
    }

    public int getPoisonBulletDurationTicks() {
        return poisonBulletDurationTicks;
    }

    public void setPoisonBulletDurationTicks(int poisonBulletDurationTicks) {
        this.poisonBulletDurationTicks = poisonBulletDurationTicks;
    }

    public int getPoisonBulletAmplifier() {
        return poisonBulletAmplifier;
    }

    public void setPoisonBulletAmplifier(int poisonBulletAmplifier) {
        this.poisonBulletAmplifier = poisonBulletAmplifier;
    }

    public int getPoisonCloudRadius() {
        return poisonCloudRadius;
    }

    public void setPoisonCloudRadius(int poisonCloudRadius) {
        this.poisonCloudRadius = poisonCloudRadius;
    }

    public int getPoisonCloudLifetimeTicks() {
        return poisonCloudLifetimeTicks;
    }

    public void setPoisonCloudLifetimeTicks(int poisonCloudLifetimeTicks) {
        this.poisonCloudLifetimeTicks = poisonCloudLifetimeTicks;
    }

    public int getPoisonCloudEffectDurationTicks() {
        return poisonCloudEffectDurationTicks;
    }

    public void setPoisonCloudEffectDurationTicks(int poisonCloudEffectDurationTicks) {
        this.poisonCloudEffectDurationTicks = poisonCloudEffectDurationTicks;
    }

    public int getPoisonCloudAmplifier() {
        return poisonCloudAmplifier;
    }

    public void setPoisonCloudAmplifier(int poisonCloudAmplifier) {
        this.poisonCloudAmplifier = poisonCloudAmplifier;
    }

    public int getBlindnessChancePercent() {
        return blindnessChancePercent;
    }

    public void setBlindnessChancePercent(int blindnessChancePercent) {
        this.blindnessChancePercent = blindnessChancePercent;
    }

    public int getBlindnessDurationTicks() {
        return blindnessDurationTicks;
    }

    public void setBlindnessDurationTicks(int blindnessDurationTicks) {
        this.blindnessDurationTicks = blindnessDurationTicks;
    }

    public int getHealingFieldRadius() {
        return healingFieldRadius;
    }

    public void setHealingFieldRadius(int healingFieldRadius) {
        this.healingFieldRadius = healingFieldRadius;
    }

    public int getHealingFieldLifetimeTicks() {
        return healingFieldLifetimeTicks;
    }

    public void setHealingFieldLifetimeTicks(int healingFieldLifetimeTicks) {
        this.healingFieldLifetimeTicks = healingFieldLifetimeTicks;
    }

    public int getHealingFieldEffectDurationTicks() {
        return healingFieldEffectDurationTicks;
    }

    public void setHealingFieldEffectDurationTicks(int healingFieldEffectDurationTicks) {
        this.healingFieldEffectDurationTicks = healingFieldEffectDurationTicks;
    }

    public int getHealingFieldAmplifier() {
        return healingFieldAmplifier;
    }

    public void setHealingFieldAmplifier(int healingFieldAmplifier) {
        this.healingFieldAmplifier = healingFieldAmplifier;
    }

    public long getHealingFieldCooldownTicks() {
        return healingFieldCooldownTicks;
    }

    public void setHealingFieldCooldownTicks(long healingFieldCooldownTicks) {
        this.healingFieldCooldownTicks = healingFieldCooldownTicks;
    }

    public int getProjectilesPerShot() {
        return projectilesPerShot;
    }

    public void setProjectilesPerShot(int projectilesPerShot) {
        this.projectilesPerShot = projectilesPerShot;
    }

    public int getFireOnHitDurationTicks() {
        return fireOnHitDurationTicks;
    }

    public void setFireOnHitDurationTicks(int fireOnHitDurationTicks) {
        this.fireOnHitDurationTicks = fireOnHitDurationTicks;
    }

    public float getFireOnHitExtraDamage() {
        return fireOnHitExtraDamage;
    }

    public void setFireOnHitExtraDamage(float fireOnHitExtraDamage) {
        this.fireOnHitExtraDamage = fireOnHitExtraDamage;
    }

    public boolean isCursedBullet() {
        return cursedBullet;
    }

    public void setCursedBullet(boolean cursedBullet) {
        this.cursedBullet = cursedBullet;
    }

    public int getCursedGlowDurationTicks() {
        return cursedGlowDurationTicks;
    }

    public void setCursedGlowDurationTicks(int cursedGlowDurationTicks) {
        this.cursedGlowDurationTicks = cursedGlowDurationTicks;
    }

    public float getCursedExplosionPower() {
        return cursedExplosionPower;
    }

    public void setCursedExplosionPower(float cursedExplosionPower) {
        this.cursedExplosionPower = cursedExplosionPower;
    }

    public boolean isParasite() {
        return parasite;
    }

    public void setParasite(boolean parasite) {
        this.parasite = parasite;
    }

    public boolean isJackpot() {
        return jackpot;
    }

    public void setJackpot(boolean jackpot) {
        this.jackpot = jackpot;
    }

    public int getJackpotChancePercent() {
        return jackpotChancePercent;
    }

    public void setJackpotChancePercent(int jackpotChancePercent) {
        this.jackpotChancePercent = jackpotChancePercent;
    }

    public int getJackpotDurationTicks() {
        return jackpotDurationTicks;
    }

    public void setJackpotDurationTicks(int jackpotDurationTicks) {
        this.jackpotDurationTicks = jackpotDurationTicks;
    }

    public boolean isThor() {
        return thor;
    }

    public void setThor(boolean thor) {
        this.thor = thor;
    }

    public int getThorChancePercent() {
        return thorChancePercent;
    }

    public void setThorChancePercent(int thorChancePercent) {
        this.thorChancePercent = thorChancePercent;
    }

    public float getThorDamage() {
        return thorDamage;
    }

    public void setThorDamage(float thorDamage) {
        this.thorDamage = thorDamage;
    }

    public boolean isUnderSpeed() {
        return underSpeed;
    }

    public void setUnderSpeed(boolean underSpeed) {
        this.underSpeed = underSpeed;
    }

    public int getUnderSpeedDurationTicks() {
        return underSpeedDurationTicks;
    }

    public void setUnderSpeedDurationTicks(int underSpeedDurationTicks) {
        this.underSpeedDurationTicks = underSpeedDurationTicks;
    }

    public int getUnderSpeedAmplifier() {
        return underSpeedAmplifier;
    }

    public void setUnderSpeedAmplifier(int underSpeedAmplifier) {
        this.underSpeedAmplifier = underSpeedAmplifier;
    }

    public boolean isDep() {
        return dep;
    }

    public void setDep(boolean dep) {
        this.dep = dep;
    }

    public int getDepChancePercent() {
        return depChancePercent;
    }

    public void setDepChancePercent(int depChancePercent) {
        this.depChancePercent = depChancePercent;
    }

    public double getDepTargetBonusMultiplier() {
        return depTargetBonusMultiplier;
    }

    public void setDepTargetBonusMultiplier(double depTargetBonusMultiplier) {
        this.depTargetBonusMultiplier = depTargetBonusMultiplier;
    }

    public double getDepSelfBonusMultiplier() {
        return depSelfBonusMultiplier;
    }

    public void setDepSelfBonusMultiplier(double depSelfBonusMultiplier) {
        this.depSelfBonusMultiplier = depSelfBonusMultiplier;
    }

    public boolean isTimeJump() {
        return timeJump;
    }

    public void setTimeJump(boolean timeJump) {
        this.timeJump = timeJump;
    }

    public int getTimeJumpChancePercent() {
        return timeJumpChancePercent;
    }

    public void setTimeJumpChancePercent(int timeJumpChancePercent) {
        this.timeJumpChancePercent = timeJumpChancePercent;
    }

    public boolean isSummoner() {
        return summoner;
    }

    public void setSummoner(boolean summoner) {
        this.summoner = summoner;
    }

    public int getSummonerLimitPerPlayer() {
        return summonerLimitPerPlayer;
    }

    public void setSummonerLimitPerPlayer(int summonerLimitPerPlayer) {
        this.summonerLimitPerPlayer = summonerLimitPerPlayer;
    }

    public float getSummonerZombieDamage() {
        return summonerZombieDamage;
    }

    public void setSummonerZombieDamage(float summonerZombieDamage) {
        this.summonerZombieDamage = summonerZombieDamage;
    }

    public boolean isParasiteSummonerSynergy() {
        return parasiteSummonerSynergy;
    }

    public void setParasiteSummonerSynergy(boolean parasiteSummonerSynergy) {
        this.parasiteSummonerSynergy = parasiteSummonerSynergy;
    }

    public boolean isDepJackpotSynergy() {
        return depJackpotSynergy;
    }

    public void setDepJackpotSynergy(boolean depJackpotSynergy) {
        this.depJackpotSynergy = depJackpotSynergy;
    }

    public boolean isHealingFieldSurge() {
        return healingFieldSurge;
    }

    public void setHealingFieldSurge(boolean healingFieldSurge) {
        this.healingFieldSurge = healingFieldSurge;
    }

    public boolean isGhostRider() {
        return ghostRider;
    }

    public void setGhostRider(boolean ghostRider) {
        this.ghostRider = ghostRider;
    }

    public boolean isBombShield() {
        return bombShield;
    }

    public void setBombShield(boolean bombShield) {
        this.bombShield = bombShield;
    }

    public float getBombShieldDamage() {
        return bombShieldDamage;
    }

    public void setBombShieldDamage(float bombShieldDamage) {
        this.bombShieldDamage = bombShieldDamage;
    }

    public double getBombShieldRadius() {
        return bombShieldRadius;
    }

    public void setBombShieldRadius(double bombShieldRadius) {
        this.bombShieldRadius = bombShieldRadius;
    }

    public long getBombShieldExtraCooldownTicks() {
        return bombShieldExtraCooldownTicks;
    }

    public void setBombShieldExtraCooldownTicks(long bombShieldExtraCooldownTicks) {
        this.bombShieldExtraCooldownTicks = bombShieldExtraCooldownTicks;
    }

    public boolean isKaboom() {
        return kaboom;
    }

    public void setKaboom(boolean kaboom) {
        this.kaboom = kaboom;
    }

    public float getKaboomDamage() {
        return kaboomDamage;
    }

    public void setKaboomDamage(float kaboomDamage) {
        this.kaboomDamage = kaboomDamage;
    }

    public double getKaboomRadius() {
        return kaboomRadius;
    }

    public void setKaboomRadius(double kaboomRadius) {
        this.kaboomRadius = kaboomRadius;
    }

    public boolean isFireGhostSynergy() {
        return fireGhostSynergy;
    }

    public void setFireGhostSynergy(boolean fireGhostSynergy) {
        this.fireGhostSynergy = fireGhostSynergy;
    }
}
