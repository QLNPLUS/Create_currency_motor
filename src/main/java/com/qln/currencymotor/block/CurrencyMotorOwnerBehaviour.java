package com.qln.currencymotor.block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.Objects;

public final class CurrencyMotorOwnerBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<CurrencyMotorOwnerBehaviour> TYPE = new BehaviourType<>();

    @Nullable
    private GameProfile owner;

    public CurrencyMotorOwnerBehaviour(CurrencyMotorBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (owner != null) {
            nbt.put("Owner", writeProfile(owner));
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        owner = nbt.contains("Owner", 10) ? readProfile(nbt.getCompound("Owner")) : null;
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

    private static CompoundTag writeProfile(GameProfile profile) {
        CompoundTag tag = new CompoundTag();
        if (profile.getId() != null) {
            tag.putUUID("Id", profile.getId());
        }
        if (profile.getName() != null) {
            tag.putString("Name", profile.getName());
        }

        CompoundTag propertiesTag = new CompoundTag();
        for (String key : profile.getProperties().keySet()) {
            ListTag properties = new ListTag();
            for (Property property : profile.getProperties().get(key)) {
                CompoundTag propertyTag = new CompoundTag();
                propertyTag.putString("Value", property.value());
                if (property.hasSignature()) {
                    propertyTag.putString("Signature", property.signature());
                }
                properties.add(propertyTag);
            }
            propertiesTag.put(key, properties);
        }
        if (!propertiesTag.isEmpty()) {
            tag.put("Properties", propertiesTag);
        }
        return tag;
    }

    @Nullable
    private static GameProfile readProfile(CompoundTag tag) {
        UUID id = tag.hasUUID("Id") ? tag.getUUID("Id") : null;
        String name = tag.contains("Name", 8) ? tag.getString("Name") : null;
        GameProfile profile = new GameProfile(id, name);
        if (!tag.contains("Properties", 10)) {
            return profile;
        }

        CompoundTag propertiesTag = tag.getCompound("Properties");
        for (String key : propertiesTag.getAllKeys()) {
            ListTag properties = propertiesTag.getList(key, 10);
            for (int index = 0; index < properties.size(); index++) {
                CompoundTag propertyTag = properties.getCompound(index);
                String value = propertyTag.getString("Value");
                if (propertyTag.contains("Signature", 8)) {
                    profile.getProperties().put(key, new Property(key, value, propertyTag.getString("Signature")));
                } else {
                    profile.getProperties().put(key, new Property(key, value));
                }
            }
        }
        return profile;
    }
}
