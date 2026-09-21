package com.qln.currencymotor.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class OwnerSlotTransform extends ValueBoxTransform {

    private static final Vec3 HORIZONTAL = VecHelper.voxelSpace(8.0F, 8.0F, -0.501F);
    private static final Vec3 VERTICAL = new Vec3(HORIZONTAL.y, HORIZONTAL.z, HORIZONTAL.x);

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(CurrencyMotorBlock.FACING);
        if (facing.getAxis().isHorizontal()) {
            return VecHelper.rotateCentered(HORIZONTAL, AngleHelper.horizontalAngle(facing), Direction.Axis.Y);
        }
        return VecHelper.rotateCentered(VERTICAL, facing == Direction.DOWN ? 180.0F : 0.0F, Direction.Axis.X);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack poseStack) {
        Direction facing = state.getValue(CurrencyMotorBlock.FACING);
        float yRot = facing.getAxis().isVertical() ? 90.0F : AngleHelper.horizontalAngle(facing);
        float xRot = facing == Direction.UP ? 270.0F : facing == Direction.DOWN ? 90.0F : 0.0F;
        TransformStack.of(poseStack).rotateYDegrees(yRot).rotateXDegrees(xRot);
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = getLocalOffset(level, pos, state);
        return localHit.distanceTo(offset) < scale / 3.5F;
    }

    @Override
    public float getScale() {
        return 0.4975F;
    }
}
