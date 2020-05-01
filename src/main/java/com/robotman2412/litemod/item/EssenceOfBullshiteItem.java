package com.robotman2412.litemod.item;

import com.robotman2412.litemod.util.IRedstoneInfo;
import com.robotman2412.litemod.util.PowerResult;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EssenceOfBullshiteItem extends ItemWrapper {
	
	public EssenceOfBullshiteItem() {
		super(new Settings().group(ItemGroup.REDSTONE).maxCount(16), "essence_of_bullshite");
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient && user != null) {
			ServerPlayerEntity player = (ServerPlayerEntity) user;
			player.sendChatMessage(new LiteralText("The bullshite of not using this on anything."), MessageType.CHAT);
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient) {
			ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
			World worldIn = context.getWorld();
			BlockState state = worldIn.getBlockState(context.getBlockPos());
			if (player != null) {
				int powerLevel = 0;
				boolean isEmittedPowerLevel = false;
				int blockPowerLevel = 0;
				boolean isStrongBlockPowered = false;
				boolean showBlockPowerLevel = false;
				Block block = state.getBlock();
				BlockEntity ent =  worldIn.getBlockEntity(context.getBlockPos());
				if (state.isSimpleFullBlock(worldIn, context.getBlockPos())) {
					PowerResult res = PowerResult.getInducedPower(worldIn, context.getBlockPos());
					isStrongBlockPowered = res.isStronglyPowered;
					blockPowerLevel = res.powerLevel;
					showBlockPowerLevel = true;
				}
				if (block instanceof IRedstoneInfo) {
					IRedstoneInfo info = (IRedstoneInfo) block;
					powerLevel = info.getPowerLevel(state);
					isEmittedPowerLevel = info.isEmittedPower(state);
				}
				else if (ent instanceof IRedstoneInfo) {
					IRedstoneInfo info = (IRedstoneInfo) ent;
					powerLevel = info.getPowerLevel(state);
					isEmittedPowerLevel = info.isEmittedPower(state);
				}
				else if (block instanceof RedstoneBlock) {
					powerLevel = 16;
					isEmittedPowerLevel = true;
					showBlockPowerLevel = false;
				}
				else if (block instanceof RedstoneWireBlock) {
					powerLevel = state.get(RedstoneWireBlock.POWER);
					isEmittedPowerLevel = true;
				}
				else if (block instanceof AbstractButtonBlock) {
					powerLevel = state.get(AbstractButtonBlock.POWERED) ? 16 : 0;
					isEmittedPowerLevel = true;
				}
				else if (block instanceof LeverBlock) {
					powerLevel = state.get(LeverBlock.POWERED) ? 16 : 0;
					isEmittedPowerLevel = true;
				}
				else if (block instanceof ObserverBlock) {
					powerLevel = state.get(ObserverBlock.POWERED) ? 16 : 0;
					isEmittedPowerLevel = true;
				}
				else if (block instanceof TripwireHookBlock) {
					powerLevel = state.get(TripwireHookBlock.POWERED) ? 16 : 0;
					isEmittedPowerLevel = true;
				}
				else if (block instanceof WeightedPressurePlateBlock) {
					powerLevel = state.get(WeightedPressurePlateBlock.POWER);
					isEmittedPowerLevel = true;
				}
				else if (block instanceof PressurePlateBlock) {
					powerLevel = state.get(PressurePlateBlock.POWERED) ? 16 : 0;
					isEmittedPowerLevel = true;
				}
				else if (block instanceof ComparatorBlock) {
					if (ent instanceof ComparatorBlockEntity) {
						powerLevel = ((ComparatorBlockEntity) ent).getOutputSignal();
						isEmittedPowerLevel = true;
					}
				}
				else if (block instanceof RedstoneLampBlock) {
					if (state.get(RedstoneLampBlock.LIT)) {
						powerLevel = 16;
					}
				}
				else if (block instanceof RedstoneTorchBlock || block instanceof WallRedstoneTorchBlock) {
					if (state.get(RedstoneTorchBlock.LIT)) {
						powerLevel = 16;
					}
					isEmittedPowerLevel = true;
				}
				else if (block instanceof RepeaterBlock) {
					if (state.get(RepeaterBlock.POWERED)) {
						powerLevel = 16;
					}
					isEmittedPowerLevel = true;
				}
				else if (block instanceof CommandBlock && ent instanceof CommandBlockBlockEntity) {
					if (((CommandBlockBlockEntity) ent).isPowered()) {
						powerLevel = 16;
					}
				}
				else if (block instanceof DispenserBlock) {
					if (state.get(DispenserBlock.TRIGGERED)) {
						powerLevel = 16;
					}
				}
				else if (block instanceof DropperBlock) {
					if (state.get(DropperBlock.TRIGGERED)) {
						powerLevel = 16;
					}
				}
				else
				{
					powerLevel = -1;
				}
				Text messageText = null;
				if (powerLevel >= 0) {
					String sPowerLevel = powerLevel == 0 ? "off" : powerLevel == 16 ? "on" : "" + powerLevel;
					messageText = new LiteralText((isEmittedPowerLevel ? "Redstone power: " : "Powered: ") + sPowerLevel);
				}
				if (showBlockPowerLevel) {
					String sBlockPowerLevel = blockPowerLevel == 0 ? "none" : blockPowerLevel + (isStrongBlockPowered ? " strong (powers redstone wire)" : " weak (does not power redstone wire)");
					Text tx = new LiteralText("Block induced power: " + sBlockPowerLevel);
					if (messageText == null) {
						messageText = tx;
					}
					else
					{
						messageText.append(", ").append(tx);
					}
				}
				if (messageText != null) {
					player.sendChatMessage(messageText, MessageType.GAME_INFO);
				}
			}
		}
		return ActionResult.SUCCESS;
	}
	
	@Override
	public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (user instanceof ServerPlayerEntity) {
			user.sendMessage(new LiteralText("The bullshite of using this on an entity."));
		}
		return true;
	}
	
}
