package com.qln.currencymotor;

import com.qln.currencymotor.block.CurrencyMotorBlock;
import com.qln.currencymotor.block.CurrencyMotorBlockEntity;
import com.qln.currencymotor.config.CurrencyMotorConfig;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Mod(CurrencyMotorMod.MOD_ID)
public final class CurrencyMotorMod {

    public static final String MOD_ID = "currency_motor";
    public static final int MAX_SPEED = 256;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final Supplier<CurrencyMotorBlock> CURRENCY_MOTOR = BLOCKS.register(
            "currency_motor",
            () -> new CurrencyMotorBlock(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final Supplier<Item> CURRENCY_MOTOR_ITEM = ITEMS.register(
            "currency_motor",
            () -> new BlockItem(CURRENCY_MOTOR.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<CurrencyMotorBlockEntity>> CURRENCY_MOTOR_ENTITY =
            BLOCK_ENTITIES.register("currency_motor", () -> BlockEntityType.Builder.of(
                    CurrencyMotorBlockEntity::new,
                    CURRENCY_MOTOR.get()).build(null));

    public CurrencyMotorMod(IEventBus modBus, ModContainer modContainer) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        CurrencyMotorConfig.register(modContainer);
        modBus.addListener(this::commonSetup);
        modBus.addListener(CurrencyMotorMod::addCreative);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.CAPACITIES.register(CURRENCY_MOTOR.get(),
                    CurrencyMotorConfig::stressCapacityPerRpm);
            BlockStressValues.RPM.register(CURRENCY_MOTOR.get(),
                    new BlockStressValues.GeneratedRpm(MAX_SPEED, true));
        });
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(CURRENCY_MOTOR_ITEM.get());
        }
    }
}
