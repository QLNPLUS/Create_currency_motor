package com.qln.currencymotor.config;

import com.qln.currencymotor.CurrencyMotorMod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class CurrencyMotorConfig {

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue CURRENCY_PER_RPM;
    private static final ModConfigSpec.DoubleValue MAX_STRESS;
    private static final ModConfigSpec.IntValue CHARGE_INTERVAL_TICKS;
    private static final ModConfigSpec.ConfigValue<String> CURRENCY_ID;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Currency Motor server settings").push(CurrencyMotorMod.MOD_ID);
        CURRENCY_PER_RPM = builder
                .comment("Q-shop currency charged per RPM each time the payment interval elapses.")
                .defineInRange("currency_per_rpm", 0.01D, 0.0D, 1.0E12D);
        MAX_STRESS = builder
                .comment("Stress capacity provided at the maximum speed of 256 RPM.")
                .defineInRange("max_stress", 16384.0D, 0.0D, 1.0E12D);
        CHARGE_INTERVAL_TICKS = builder
                .comment("How often to charge the owner, in ticks. Valid range: 1 tick to 5 seconds.")
                .defineInRange("charge_interval_ticks", 20, 1, 100);
        CURRENCY_ID = builder
                .comment("Q-shop currency id used for the charge, for example coins or points.")
                .define("currency_id", "coins");
        builder.pop();
        SPEC = builder.build();
    }

    private CurrencyMotorConfig() {
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    public static double currencyPerRpm() {
        return CURRENCY_PER_RPM.get();
    }

    public static double maxStress() {
        return MAX_STRESS.get();
    }

    public static int chargeIntervalTicks() {
        return CHARGE_INTERVAL_TICKS.get();
    }

    public static String currencyId() {
        return CURRENCY_ID.get();
    }

    public static double stressCapacityPerRpm() {
        return maxStress() / CurrencyMotorMod.MAX_SPEED;
    }
}
