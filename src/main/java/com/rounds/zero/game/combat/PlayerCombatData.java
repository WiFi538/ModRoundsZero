package com.rounds.zero.game.combat;

public class PlayerCombatData {
    private CombatStats stats = new CombatStats();

    private int currentAmmo = stats.getMaxAmmo();

    private boolean reloading = false;
    private long reloadEndTick = 0L;

    private long lastShotTick = -9999L;

    private boolean shieldActive = false;
    private long shieldEndTick = 0L;
    private long shieldCooldownEndTick = 0L;

    public CombatStats getStats() {
        return stats;
    }

    public void setStats(CombatStats stats) {
        this.stats = stats;
        if (currentAmmo > stats.getMaxAmmo()) {
            currentAmmo = stats.getMaxAmmo();
        }
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public long getReloadEndTick() {
        return reloadEndTick;
    }

    public void setReloadEndTick(long reloadEndTick) {
        this.reloadEndTick = reloadEndTick;
    }

    public long getLastShotTick() {
        return lastShotTick;
    }

    public void setLastShotTick(long lastShotTick) {
        this.lastShotTick = lastShotTick;
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public void setShieldActive(boolean shieldActive) {
        this.shieldActive = shieldActive;
    }

    public long getShieldEndTick() {
        return shieldEndTick;
    }

    public void setShieldEndTick(long shieldEndTick) {
        this.shieldEndTick = shieldEndTick;
    }

    public long getShieldCooldownEndTick() {
        return shieldCooldownEndTick;
    }

    public void setShieldCooldownEndTick(long shieldCooldownEndTick) {
        this.shieldCooldownEndTick = shieldCooldownEndTick;
    }
}