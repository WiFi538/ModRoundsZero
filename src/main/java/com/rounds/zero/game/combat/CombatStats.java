package com.rounds.zero.game.combat;

public class CombatStats {
    private int maxAmmo = 3;

    private long shotCooldownTicks = 10L;
    private long reloadDurationTicks = 40L;

    private double bulletDamage = 3.0;
    private double bulletSpeed = 1.0;
    private double bulletSize = 1.0;

    private long shieldDurationTicks = 10L;
    private long shieldCooldownTicks = 60L;

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
}
