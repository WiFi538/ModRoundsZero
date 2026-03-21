package com.rounds.zero.game.upgrade;

public class UpgradeCard {
    private final String id;
    private final String title;
    private final String description;
    private final String texturePath;

    private final int flatAmmoBonus;

    private final double damageMultiplier;
    private final double bulletSpeedMultiplier;
    private final double bulletSizeMultiplier;
    private final double maxHealthMultiplier;

    private final double fireRatePercent;
    private final double shieldCooldownPercent;
    private final double reloadPercent;

    private final long reloadTicksFlat;

    private UpgradeCard(
            String id,
            String title,
            String description,
            String texturePath,
            int flatAmmoBonus,
            double damageMultiplier,
            double bulletSpeedMultiplier,
            double bulletSizeMultiplier,
            double maxHealthMultiplier,
            double fireRatePercent,
            double shieldCooldownPercent,
            double reloadPercent,
            long reloadTicksFlat
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.texturePath = texturePath;
        this.flatAmmoBonus = flatAmmoBonus;
        this.damageMultiplier = damageMultiplier;
        this.bulletSpeedMultiplier = bulletSpeedMultiplier;
        this.bulletSizeMultiplier = bulletSizeMultiplier;
        this.maxHealthMultiplier = maxHealthMultiplier;
        this.fireRatePercent = fireRatePercent;
        this.shieldCooldownPercent = shieldCooldownPercent;
        this.reloadPercent = reloadPercent;
        this.reloadTicksFlat = reloadTicksFlat;
    }

    public static Builder builder(String id, String title, String description) {
        return new Builder(id, title, description);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getFlatAmmoBonus() {
        return flatAmmoBonus;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getBulletSpeedMultiplier() {
        return bulletSpeedMultiplier;
    }

    public double getBulletSizeMultiplier() {
        return bulletSizeMultiplier;
    }

    public double getMaxHealthMultiplier() {
        return maxHealthMultiplier;
    }

    public double getFireRatePercent() {
        return fireRatePercent;
    }

    public double getShieldCooldownPercent() {
        return shieldCooldownPercent;
    }

    public double getReloadPercent() {
        return reloadPercent;
    }

    public long getReloadTicksFlat() {
        return reloadTicksFlat;
    }

    public static class Builder {
        private final String id;
        private final String title;
        private final String description;

        private String texturePath = "gui/cards/default.png";

        private int flatAmmoBonus = 0;
        private double damageMultiplier = 1.0;
        private double bulletSpeedMultiplier = 1.0;
        private double bulletSizeMultiplier = 1.0;
        private double maxHealthMultiplier = 1.0;
        private double fireRatePercent = 0.0;
        private double shieldCooldownPercent = 0.0;
        private double reloadPercent = 0.0;
        private long reloadTicksFlat = 0L;

        private Builder(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public Builder texturePath(String value) {
            this.texturePath = value;
            return this;
        }

        public Builder flatAmmoBonus(int value) {
            this.flatAmmoBonus = value;
            return this;
        }

        public Builder damageMultiplier(double value) {
            this.damageMultiplier = value;
            return this;
        }

        public Builder bulletSpeedMultiplier(double value) {
            this.bulletSpeedMultiplier = value;
            return this;
        }

        public Builder bulletSizeMultiplier(double value) {
            this.bulletSizeMultiplier = value;
            return this;
        }

        public Builder maxHealthMultiplier(double value) {
            this.maxHealthMultiplier = value;
            return this;
        }

        public Builder fireRatePercent(double value) {
            this.fireRatePercent = value;
            return this;
        }

        public Builder shieldCooldownPercent(double value) {
            this.shieldCooldownPercent = value;
            return this;
        }

        public Builder reloadPercent(double value) {
            this.reloadPercent = value;
            return this;
        }

        public Builder reloadTicksFlat(long value) {
            this.reloadTicksFlat = value;
            return this;
        }

        public UpgradeCard build() {
            return new UpgradeCard(
                    id,
                    title,
                    description,
                    texturePath,
                    flatAmmoBonus,
                    damageMultiplier,
                    bulletSpeedMultiplier,
                    bulletSizeMultiplier,
                    maxHealthMultiplier,
                    fireRatePercent,
                    shieldCooldownPercent,
                    reloadPercent,
                    reloadTicksFlat
            );
        }
    }
}