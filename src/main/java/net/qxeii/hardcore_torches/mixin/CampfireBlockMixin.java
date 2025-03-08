package net.qxeii.hardcore_torches.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.mixinlogic.CampfireBlockMixinLogic;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements CampfireBlockMixinLogic {

	@Shadow
	private final boolean emitsParticles;

	@Shadow
	private final int fireDamage;

	public CampfireBlockMixin(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings) {
		// This will never be called.

		super(settings);
		this.emitsParticles = emitsParticles;
		this.fireDamage = fireDamage;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(boolean emitsParticles, int fireDamage,
			AbstractBlock.Settings settings,
			CallbackInfo callbackInfo) {

		this.setDefaultState(this.getDefaultState().with(LIT, false));
	}

	@Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
	private void getPlacementState(ItemPlacementContext context,
			CallbackInfoReturnable<BlockState> callbackInfo) {
		var placementState = injectedGetPlacementState(context,
				callbackInfo.getReturnValue());
		callbackInfo.setReturnValue(placementState);
	}

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
			CallbackInfoReturnable<ActionResult> callbackInfo) {
		var useActionResult = injectedOnUse(state, world, pos, player, hand, hit);

		if (useActionResult.isAccepted()) {
			callbackInfo.setReturnValue(useActionResult);
			callbackInfo.cancel();
		}
	}

}
