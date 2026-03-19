package com.rounds.zero.game.combat;

public class CombatStats {
    public static final int DEFAULT_MAX_AMMO = 3;
    public static final long DEFAULT_SHOT_COOLDOWN_TICKS = 8L;
    public static final long DEFAULT_RELOAD_DURATION_TICKS = 30L;
    public static final float DEFAULT_BULLET_DAMAGE = 6.0f;
    public static final double DEFAULT_BULLET_SPEED = 1.6d;
    public static final float DEFAULT_BULLET_SIZE = 0.25f;
    public static final long DEFAULT_SHIELD_DURATION_TICKS = 10L;
    public static final long DEFAULT_SHIELD_COOLDOWN_TICKS = 60L;
    public static final int DEFAULT_RICOCHET_COUNT = 0;

    private int maxAmmo = DEFAULT_MAX_AMMO;
    private long shotCooldownTicks = DEFAULT_SHOT_COOLDOWN_TICKS;
    private long reloadDurationTicks = DEFAULT_RELOAD_DURATION_TICKS;
    private float bulletDamage = DEFAULT_BULLET_DAMAGE;
    private double bulletSpeed = DEFAULT_BULLET_SPEED;
    private float bulletSize = DEFAULT_BULLET_SIZE;
    private long shieldDurationTicks = DEFAULT_SHIELD_DURATION_TICKS;
    private long shieldCooldownTicks = DEFAULT_SHIELD_COOLDOWN_TICKS;
    private int ricochetCount = DEFAULT_RICOCHET_COUNT;

    private boolean burstEnabled;
    private boolean volleyEnabled;
    private boolean homingEnabled;
    private boolean poisonEnabled;
    private boolean freezeEnabled;
    private boolean blindEnabled;
    private boolean secondLifeEnabled;

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
        copy.ricochetCount = this.ricochetCount;
        copy.burstEnabled = this.burstEnabled;
        copy.volleyEnabled = this.volleyEnabled;
        copy.homingEnabled = this.homingEnabled;
        copy.poisonEnabled = this.poisonEnabled;
        copy.freezeEnabled = this.freezeEnabled;
        copy.blindEnabled = this.blindEnabled;
        copy.secondLifeEnabled = this.secondLifeEnabled;
        return copy;
    }

    public int getMaxAmmo() { return maxAmmo; }
    public void setMaxAmmo(int maxAmmo) { this.maxAmmo = Math.max(1, maxAmmo); }
    public long getShotCooldownTicks() { return shotCooldownTicks; }
    public void setShotCooldownTicks(long shotCooldownTicks) { this.shotCooldownTicks = Math.max(0L, shotCooldownTicks); }
    public long getReloadDurationTicks() { return reloadDurationTicks; }
    public void setReloadDurationTicks(long reloadDurationTicks) { this.reloadDurationTicks = Math.max(0L, reloadDurationTicks); }
    public float getBulletDamage() { return bulletDamage; }
    public void setBulletDamage(float bulletDamage) { this.bulletDamage = Math.max(0.0f, bulletDamage); }
    public double getBulletSpeed() { return bulletSpeed; }
    public void setBulletSpeed(double bulletSpeed) { this.bulletSpeed = Math.max(0.0d, bulletSpeed); }
    public float getBulletSize() { return bulletSize; }
    public void setBulletSize(float bulletSize) { this.bulletSize = Math.max(0.01f, bulletSize); }
    public long getShieldDurationTicks() { return shieldDurationTicks; }
    public void setShieldDurationTicks(long shieldDurationTicks) { this.shieldDurationTicks = Math.max(0L, shieldDurationTicks); }
    public long getShieldCooldownTicks() { return shieldCooldownTicks; }
    public void setShieldCooldownTicks(long shieldCooldownTicks) { this.shieldCooldownTicks = Math.max(0L, shieldCooldownTicks); }
    public int getRicochetCount() { return ricochetCount; }
    public void setRicochetCount(int ricochetCount) { this.ricochetCount = Math.max(0, ricochetCount); }
    public boolean isBurstEnabled() { return burstEnabled; }
    public void setBurstEnabled(boolean burstEnabled) { this.burstEnabled = burstEnabled; }
    public boolean isVolleyEnabled() { return volleyEnabled; }
    public void setVolleyEnabled(boolean volleyEnabled) { this.volleyEnabled = volleyEnabled; }
    public boolean isHomingEnabled() { return homingEnabled; }
    public void setHomingEnabled(boolean homingEnabled) { this.homingEnabled = homingEnabled; }
    public boolean isPoisonEnabled() { return poisonEnabled; }
    public void setPoisonEnabled(boolean poisonEnabled) { this.poisonEnabled = poisonEnabled; }
    public boolean isFreezeEnabled() { return freezeEnabled; }
    public void setFreezeEnabled(boolean freezeEnabled) { this.freezeEnabled = freezeEnabled; }
    public boolean isBlindEnabled() { return blindEnabled; }
    public void setBlindEnabled(boolean blindEnabled) { this.blindEnabled = blindEnabled; }
    public boolean isSecondLifeEnabled() { return secondLifeEnabled; }
    public void setSecondLifeEnabled(boolean secondLifeEnabled) { this.secondLifeEnabled = secondLifeEnabled; }
}
