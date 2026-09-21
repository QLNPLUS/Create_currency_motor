package com.qln.currencymotor;

import com.qln.currencymotor.block.CurrencyMotorBlock;
import com.qln.currencymotor.block.CurrencyMotorBlockEntity;
import com.qln.currencymotor.config.CurrencyMotorConfig;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraftforge.fml.common.Mod;

@Mod(CurrencyMotorMod.MOD_ID)
public final class CurrencyMotorMod {

    public static final String MOD_ID = "currency_motor";
    public static final int MAX_SPEED = 256;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<CurrencyMotorBlock> CURRENCY_MOTOR = BLOCKS.register(
            "currency_motor",
            () -> new CurrencyMotorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .strength(5.0F, 6.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> CURRENCY_MOTOR_ITEM = ITEMS.register(
            "currency_motor",
            () -> new BlockItem(CURRENCY_MOTOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<CurrencyMotorBlockEntity>> CURRENCY_MOTOR_ENTITY =
            BLOCK_ENTITIES.register("currency_motor", () -> BlockEntityType.Builder.of(
                    CurrencyMotorBlockEntity::new,
                    CURRENCY_MOTOR.get()).build(null));

    public CurrencyMotorMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        CurrencyMotorConfig.register();
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
            event.accept(CURRENCY_MOTOR_ITEM);
        }
    }
}
