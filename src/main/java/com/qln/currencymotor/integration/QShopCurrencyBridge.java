package com.qln.currencymotor.integration;

import com.qln.currencymotor.CurrencyMotorMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public final class QShopCurrencyBridge {

    private static final ResourceLocation SOURCE =
            ResourceLocation.fromNamespaceAndPath(CurrencyMotorMod.MOD_ID, "motor");
    private static Method withdrawMethod;
    private static Object service;
    private static boolean lookupAttempted;

    private QShopCurrencyBridge() {
    }

    public static boolean withdraw(MinecraftServer server, UUID owner, String currencyId,
                                   long amount, BlockPos sourcePos) {
        if (amount <= 0L) {
            return true;
        }
        if (!lookup()) {
            return false;
        }
        try {
            return (boolean) withdrawMethod.invoke(service, server, owner, currencyId, (double) amount,
                    SOURCE, sourcePos, false);
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException exception) {
            CurrencyMotorMod.LOGGER.error("Unable to withdraw Q-shop currency for Currency Motor", exception);
            return false;
        }
    }

    private static boolean lookup() {
        if (lookupAttempted) {
            return withdrawMethod != null;
        }
        lookupAttempted = true;
        try {
            Class<?> serviceClass = Class.forName("com.qshop.api.CurrencyService");
            service = serviceClass.getField("INSTANCE").get(null);
            withdrawMethod = serviceClass.getMethod("withdraw", MinecraftServer.class, UUID.class,
                    String.class, double.class, ResourceLocation.class, BlockPos.class, boolean.class);
            return true;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            CurrencyMotorMod.LOGGER.error("Q-shop CurrencyService API was not found", exception);
            return false;
        }
    }
}
