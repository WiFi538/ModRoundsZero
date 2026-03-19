package com.rounds.zero.game.combat;

public class PlayerCombatData {
    private CombatStats combatStats = CombatStats.createDefault();
    private int currentAmmo = combatStats.getMaxAmmo();
    private boolean reloading;
    private long reloadFinishTick;
    private long lastShotTick = Long.MIN_VALUE / 4;
    private boolean shieldActive;
    private long shieldEndTick;
    private long shieldCooldownEndTick;

    public CombatStats getCombatStats() {
        return combatStats;
    }

    public void setCombatStats(CombatStats combatStats) {
        this.combatStats = combatStats.copy();
        this.currentAmmo = Math.min(this.currentAmmo, this.combatStats.getMaxAmmo());
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = Math.max(0, Math.min(currentAmmo, combatStats.getMaxAmmo()));
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public long getReloadFinishTick() {
        return reloadFinishTick;
    }

    public void setReloadFinishTick(long reloadFinishTick) {
        this.reloadFinishTick = reloadFinishTick;
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

    public void resetForRound() {
        this.currentAmmo = combatStats.getMaxAmmo();
        this.reloading = false;
        this.reloadFinishTick = 0L;
        this.lastShotTick = Long.MIN_VALUE / 4;
        this.shieldActive = false;
        this.shieldEndTick = 0L;
        this.shieldCooldownEndTick = 0L;
    }
}
