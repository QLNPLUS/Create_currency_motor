package com.qln.currencymotor.client;

import com.qln.currencymotor.CurrencyMotorMod;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CurrencyMotorMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CurrencyMotorClient {

    private CurrencyMotorClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get(), CurrencyMotorRenderer::new);
        SimpleBlockEntityVisualizer.builder(CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get())
                .factory(OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
                .apply();
    }
}
