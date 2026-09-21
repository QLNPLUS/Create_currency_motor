package com.qln.currencymotor.client;

import com.qln.currencymotor.CurrencyMotorMod;
import com.qln.currencymotor.block.CurrencyMotorBlockEntity;
import com.qln.currencymotor.config.CurrencyMotorConfig;
import com.qln.currencymotor.integration.QShopCurrencyBridge;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigDecimal;

@Mod.EventBusSubscriber(modid = CurrencyMotorMod.MOD_ID, value = Dist.CLIENT)
public final class CurrencyMotorClientEvents {

    private static final ResourceLocation COST_BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(CurrencyMotorMod.MOD_ID, "textures/gui/currency_motor_cost.png");
    private static final int COST_BACKGROUND_WIDTH = 224;
    private static final int COST_BACKGROUND_HEIGHT = 32;
    private static final float COST_OVERLAY_Z = 300.0F;

    private CurrencyMotorClientEvents() {
    }

    @SubscribeEvent
    public static void renderCostOverlay(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ValueSettingsScreen screen)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || minecraft.level == null
                || !(minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof CurrencyMotorBlockEntity)) {
            return;
        }

        ValueSettingsBehaviour.ValueSettings hovered =
                screen.getClosestCoordinate(event.getMouseX(), event.getMouseY());
        long cost = CurrencyMotorBlockEntity.calculateCharge(hovered.value());
        Component speedLabel = Component.translatable("kinetics.create_currency_motor.rotation_speed");
        Component chargeInterval = Component.translatable("gui.create_currency_motor.charge_interval",
                formatIntervalSeconds());
        Component costLabel = Component.translatable("gui.create_currency_motor.current_cost",
                Long.toString(cost), QShopCurrencyBridge.displayName(CurrencyMotorConfig.currencyId()),
                chargeInterval);

        int guiTop = Math.round(screen.getCoordinateOfValue(0, 0).y - 5.0F);
        int left = (screen.width - COST_BACKGROUND_WIDTH) / 2;
        int top = guiTop - COST_BACKGROUND_HEIGHT - 2;

        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(0.0F, 0.0F, COST_OVERLAY_Z);
        event.getGuiGraphics().blit(COST_BACKGROUND, left, top, 0, 0, 0,
                COST_BACKGROUND_WIDTH, COST_BACKGROUND_HEIGHT, COST_BACKGROUND_WIDTH, COST_BACKGROUND_HEIGHT);
        event.getGuiGraphics().drawCenteredString(minecraft.font, costLabel, screen.width / 2, top + 5,
                0xFFFFFFFF);
        event.getGuiGraphics().drawCenteredString(minecraft.font, speedLabel, screen.width / 2, top + 18,
                0xFFFFFFFF);
        event.getGuiGraphics().flush();
        event.getGuiGraphics().pose().popPose();
    }

    private static String formatIntervalSeconds() {
        return BigDecimal.valueOf(CurrencyMotorConfig.chargeIntervalTicks())
                .divide(BigDecimal.valueOf(20L))
                .stripTrailingZeros()
                .toPlainString();
    }
}
