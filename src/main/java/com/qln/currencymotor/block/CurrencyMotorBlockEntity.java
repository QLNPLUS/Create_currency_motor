package com.qln.currencymotor.block;

import com.mojang.authlib.GameProfile;
import com.qln.currencymotor.CurrencyMotorMod;
import com.qln.currencymotor.config.CurrencyMotorConfig;
import com.qln.currencymotor.integration.QShopCurrencyBridge;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class CurrencyMotorBlockEntity extends GeneratingKineticBlockEntity {

    public static final int DEFAULT_SPEED = 16;

    public KineticScrollValueBehaviour generatedSpeed;
    private CurrencyMotorOwnerBehaviour ownerBehaviour;
    private final OwnerSlotTransform ownerSlot = new OwnerSlotTransform();
    private int chargeTicks;
    private boolean paymentActive;

    public CurrencyMotorBlockEntity(BlockPos pos, BlockState state) {
        super(CurrencyMotorMod.CURRENCY_MOTOR_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        ownerBehaviour = new CurrencyMotorOwnerBehaviour(this);
        behaviours.add(ownerBehaviour);

        generatedSpeed = new CurrencyMotorSpeedBehaviour(
                CreateLang.translateDirect("kinetics.create_currency_motor.rotation_speed"), this, new MotorValueBox());
        generatedSpeed.between(-CurrencyMotorMod.MAX_SPEED, CurrencyMotorMod.MAX_SPEED);
        generatedSpeed.value = DEFAULT_SPEED;
        generatedSpeed.withCallback(value -> updateGeneratedRotation());
        behaviours.add(generatedSpeed);
    }

    @Override
    public void initialize() {
        super.initialize();
        paymentActive = getOwner() != null;
        if (!hasSource() || getGeneratedSpeed() != getTheoreticalSpeed()) {
            updateGeneratedRotation();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || generatedSpeed == null) {
            return;
        }
        chargeTicks++;
        if (chargeTicks < CurrencyMotorConfig.chargeIntervalTicks()) {
            return;
        }
        chargeTicks = 0;
        chargeOwner();
    }

    @Override
    public float getGeneratedSpeed() {
        if (generatedSpeed == null || !paymentActive || getOwner() == null) {
            return 0.0F;
        }
        return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(CurrencyMotorBlock.FACING));
    }

    public void onOwnerChanged() {
        paymentActive = getOwner() != null;
        chargeTicks = 0;
        if (level != null && !level.isClientSide) {
            updateGeneratedRotation();
        }
    }

    public void toggleOwner(Player player) {
        if (getOwner() == null) {
            setOwner(player.getGameProfile());
        } else if (isOwner(player)) {
            setOwner(null);
        }
    }

    public void setOwner(GameProfile owner) {
        ownerBehaviour.setOwner(owner);
    }

    public GameProfile getOwner() {
        return ownerBehaviour == null ? null : ownerBehaviour.getOwner();
    }

    public boolean isOwner(Player player) {
        return ownerBehaviour != null && ownerBehaviour.isOwner(player);
    }

    public boolean isOwnerSlotHit(Vec3 hit) {
        if (level == null) {
            return false;
        }
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(worldPosition));
        return ownerSlot.testHit(level, worldPosition, getBlockState(), localHit);
    }

    public ValueBoxTransform getOwnerSlot() {
        return ownerSlot;
    }

    private void chargeOwner() {
        GameProfile owner = getOwner();
        float configuredSpeed = generatedSpeed.getValue();
        if (owner == null || configuredSpeed == 0.0F) {
            if (paymentActive != (owner != null)) {
                paymentActive = owner != null;
                updateGeneratedRotation();
            }
            return;
        }

        long amount = (long) Math.ceil(Math.abs(configuredSpeed) * CurrencyMotorConfig.currencyPerRpm());
        MinecraftServer server = level.getServer();
        boolean paid = server != null && QShopCurrencyBridge.withdraw(
                server, owner.getId(), CurrencyMotorConfig.currencyId(), amount, worldPosition);
        if (paymentActive != paid) {
            paymentActive = paid;
            updateGeneratedRotation();
        }
    }

    private static final class CurrencyMotorSpeedBehaviour extends KineticScrollValueBehaviour {

        private CurrencyMotorSpeedBehaviour(Component label, CurrencyMotorBlockEntity blockEntity,
                                            ValueBoxTransform slotPositioning) {
            super(label, blockEntity, slotPositioning);
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ValueSettingsBoard board = super.createBoard(player, hitResult);
            long cost = (long) Math.ceil(Math.abs(getValue()) * (double) CurrencyMotorConfig.currencyPerRpm());
            Component title = board.title().copy()
                    .append(Component.literal("  "))
                    .append(Component.translatable("gui.create_currency_motor.current_cost",
                            Long.toString(cost), CurrencyMotorConfig.currencyId()));
            return new ValueSettingsBoard(title, board.maxValue(), board.milestoneInterval(),
                    board.rows(), board.formatter());
        }
    }

    private static final class MotorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8.0F, 8.0F, 12.5F);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = state.getValue(CurrencyMotorBlock.FACING);
            return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(-1.0F / 16.0F));
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(CurrencyMotorBlock.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN) {
                return false;
            }
            return direction.getAxis() != facing.getAxis();
        }
    }
}
