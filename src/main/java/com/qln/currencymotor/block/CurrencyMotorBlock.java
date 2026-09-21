package com.qln.currencymotor.block;

import com.qln.currencymotor.CurrencyMotorMod;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CurrencyMotorBlock extends DirectionalKineticBlock implements IBE<CurrencyMotorBlockEntity> {

    public CurrencyMotorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || preferred == null) {
            return super.getStateForPlacement(context);
        }
        return defaultBlockState().setValue(FACING, preferred);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player
                && level.getBlockEntity(pos) instanceof CurrencyMotorBlockEntity motor) {
            motor.setOwner(player.getGameProfile());
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (player.isShiftKeyDown() || player.isSpectator()) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof CurrencyMotorBlockEntity motor)
                || !motor.isOwnerSlotHit(hit.getLocation())
                || !motor.isOwner(player)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            motor.toggleOwner(player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25F, 0.1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public Class<CurrencyMotorBlockEntity> getBlockEntityClass() {
        return CurrencyMotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CurrencyMotorBlockEntity> getBlockEntityType() {
        return CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get();
    }
}
