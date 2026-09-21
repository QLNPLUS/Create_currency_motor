package com.qln.currencymotor.block;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class CurrencyMotorOwnerBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<CurrencyMotorOwnerBehaviour> TYPE = new BehaviourType<>();

    @Nullable
    private GameProfile owner;

    public CurrencyMotorOwnerBehaviour(CurrencyMotorBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (owner != null) {
            CompoundTag ownerTag = new CompoundTag();
            NbtUtils.writeGameProfile(ownerTag, owner);
            nbt.put("Owner", ownerTag);
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        owner = nbt.contains("Owner", 10) ? NbtUtils.readGameProfile(nbt.getCompound("Owner")) : null;
    }

    @Nullable
    public GameProfile getOwner() {
        return owner;
    }

    public boolean isOwner(Player player) {
        return owner == null || owner.getId().equals(player.getGameProfile().getId());
    }

    public void setOwner(@Nullable GameProfile owner) {
        if (Objects.equals(this.owner, owner)) {
            return;
        }
        this.owner = owner;
        blockEntity.sendData();
        blockEntity.setChanged();
        if (blockEntity instanceof CurrencyMotorBlockEntity motor) {
            motor.onOwnerChanged();
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
