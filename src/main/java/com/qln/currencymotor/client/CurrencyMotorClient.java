package com.qln.currencymotor.client;

import com.qln.currencymotor.CurrencyMotorMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CurrencyMotorMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CurrencyMotorClient {

    private CurrencyMotorClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get(), CurrencyMotorRenderer::new);
    }
}
