package com.qln.currencymotor.client;

import com.qln.currencymotor.CurrencyMotorMod;
import com.qln.currencymotor.block.CurrencyMotorBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = CurrencyMotorMod.MOD_ID, value = Dist.CLIENT)
public final class CurrencyMotorClientEvents {

    private static final ResourceLocation COST_BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(CurrencyMotorMod.MOD_ID, "textures/gui/currency_motor_cost.png");

    private CurrencyMotorClientEvents() {
    }

    @SubscribeEvent
    public static void renderCostBackground(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof ValueSettingsScreen)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || minecraft.level == null
                || !(minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof CurrencyMotorBlockEntity)) {
            return;
        }

        int width = 256;
        int left = (event.getScreen().width - width) / 2;
        int top = event.getScreen().height / 2 - 43;
        event.getGuiGraphics().blit(COST_BACKGROUND, left, top, 0, 0, 0, width, 16, width, 16);
    }
}
