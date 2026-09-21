package com.qln.currencymotor.client;

import com.qln.currencymotor.CurrencyMotorMod;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
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
        SimpleBlockEntityVisualizer.builder(CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get())
                .factory(OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
                // The visual only draws the shaft. The block entity renderer still draws the
                // owner avatar, so it must not be skipped while visuals are active.
                .neverSkipVanillaRender()
                .apply();
    }
}
