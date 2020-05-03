package com.robotman2412.litemod.item;

import com.robotman2412.litemod.FabricLitemod;
import com.robotman2412.litemod.entity.RocketLauncherRocket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RocketLauncherItem extends ItemWrapper {
	
	public RocketLauncherItem() {
		super(new Settings().maxDamage(4096), "rocket_launcher");
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		Vec3d startPos = user.getCameraPosVec(1);
		Vec3d direction = user.getRotationVec(1);
		RocketLauncherRocket entity = new RocketLauncherRocket(FabricLitemod.ROCKET_LAUNCHER_ROCKT_TYPE, world);
		startPos = startPos.add(direction.multiply(5f));
		entity.setPos(startPos.x, startPos.y, startPos.z);
		entity.setVelocity(direction.multiply(0.1));
		world.spawnEntity(entity);
		return TypedActionResult.consume(user.getStackInHand(hand));
	}
	
}
