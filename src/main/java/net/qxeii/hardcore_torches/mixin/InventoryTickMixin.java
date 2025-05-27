package net.qxeii.hardcore_torches.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.mixinlogic.InventoryTickMixinLogic;

@Mixin(ServerPlayerEntity.class)
public abstract class InventoryTickMixin implements InventoryTickMixinLogic {
	@Shadow
	public abstract ServerWorld getServerWorld();

	@Shadow
	@Nullable
	private Entity cameraEntity;

	@Inject(at = @At("TAIL"), method = "tick")
	private void tick(CallbackInfo info) {
		var worldTick = getServerWorld().getTime();
		var world = getServerWorld();
		var player = ((ServerPlayerEntity) (Object) this);
		var inventory = player.getInventory();

		if (world.isClient) {
			return;
		}

		// Conversion

		for (int i = 0; i < inventory.size(); i++) {
			tickItemForConversion(world, player, inventory, i);
		}

		// Optimization Bail

		if (worldTick % Mod.config.itemFuelTickFactor != 0) {
			return;
		}

		// Fuel Use

		for (int i = 0; i < inventory.size(); i++) {
			tickItemForFuelUse(world, player, inventory, i);
		}
	}

}