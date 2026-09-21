package com.qln.currencymotor.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qln.currencymotor.block.CurrencyMotorBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class CurrencyMotorRenderer extends KineticBlockEntityRenderer<CurrencyMotorBlockEntity> {

    private final SkullModelBase skullModel;

    public CurrencyMotorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        skullModel = new SkullModel(context.getModelSet().bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    protected void renderSafe(CurrencyMotorBlockEntity motor, float partialTicks, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(motor, partialTicks, poseStack, buffer, light, overlay);
        GameProfile owner = motor.getOwner();
        if (owner == null) {
            return;
        }
        poseStack.pushPose();
        motor.getOwnerSlot().transform(motor.getLevel(), motor.getBlockPos(), motor.getBlockState(), poseStack);
        poseStack.scale(1.01F, 1.01F, 1.01F);
        // The slot transform puts the origin on the slot surface with -Z pointing away from
        // the block. The mirrored skull model spans 0..0.5 on Y and -0.25..0.25 on Z, so it
        // is shifted down to centre it and pushed out until its back face rests on the slot.
        poseStack.translate(0.0F, -0.25F, -0.25F);
        renderSkull(owner, poseStack, buffer, light, skullModel);
        poseStack.popPose();
    }

    private static void renderSkull(GameProfile owner, PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, SkullModelBase model) {
        RenderType renderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, owner);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer consumer = buffer.getBuffer(renderType);
        model.setupAnim(0.0F, 0.0F, 0.0F);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CurrencyMotorBlockEntity motor, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }
}
