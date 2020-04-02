package com.robotman2412.litemod;

import com.robotman2412.litemod.block.*;
import com.robotman2412.litemod.block.inferrer.InferrerChannelMap;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlock;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlockEntity;
import com.robotman2412.litemod.foods.FoodItem;
import com.robotman2412.litemod.item.*;
import com.robotman2412.litemod.item.superweapon.SuperWeapons;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricLitemod implements ModInitializer, ClientModInitializer {
	
	public static final String MOD_ID = "robot_litemod";
	
	public static final Identifier SET_CHANNEL_PACKET = new Identifier(MOD_ID, "set_channel");
	public static final Identifier SET_TRANSMISSION_PACKET = new Identifier(MOD_ID, "set_transmission");
	
	//region items
	public static final EssenceOfBullshiteItem ESSENCE_OF_BULLSHITE_ITEM = new EssenceOfBullshiteItem();
	public static final FrequencyTunerItem FREQUENCY_TUNER_ITEM = new FrequencyTunerItem();
	public static final RemoteRedstoneInferrerItem REMOTE_REDSTONE_INFERRER_ITEM = new RemoteRedstoneInferrerItem();
	public static final SimpleItem KITCHEN_KNIFE_ITEM = new SimpleItem(new Item.Settings().maxCount(1).maxDamage(2048), "kitchen_knife");
	
	public static final ItemWrapper[] ALL_ITEMS = {
			ESSENCE_OF_BULLSHITE_ITEM,
			FREQUENCY_TUNER_ITEM,
			REMOTE_REDSTONE_INFERRER_ITEM,
			KITCHEN_KNIFE_ITEM
	};
	//endregion items
	
	//region blocks
	public static final RedstoneInferrerBlock REDSTONE_INFERRER_BLOCK = new RedstoneInferrerBlock();
	public static final RedstoneCapacitorBlock REDSTONE_CAPACITOR_BLOCK = new RedstoneCapacitorBlock();
	public static final CuttingBoardBlock CUTTING_BOARD_BLOCK = new CuttingBoardBlock();
	public static final ForceLoadificatorBlock FORCE_LOADIFICATOR_BLOCK = new ForceLoadificatorBlock();
	
	public static BlockEntityType<RedstoneCapacitorBlockEntity> REDSTONE_CAPACITOR_BLOCK_ENTITY;
	public static BlockEntityType<RedstoneInferrerBlockEntity> REDSTONE_INFERRER_BLOCK_ENTITY;
	
	public static final BlockWrapper[] ALL_BLOCKS = {
			REDSTONE_INFERRER_BLOCK,
			REDSTONE_CAPACITOR_BLOCK,
			CUTTING_BOARD_BLOCK,
			FORCE_LOADIFICATOR_BLOCK
	};
	//endregion blocks
	
	public static final ItemGroup KITCHEN_SUPPLIES = FabricItemGroupBuilder.create(new Identifier("robot_litemod", "kitchen_supplies"))
			.icon(() -> new ItemStack(KITCHEN_KNIFE_ITEM))
			.appendItems((stax) -> {
				stax.add(new ItemStack(KITCHEN_KNIFE_ITEM));
				stax.add(new ItemStack(CUTTING_BOARD_BLOCK));
			})
			.build();
	
	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(InferrerChannelMap::tick);
		for (ItemWrapper item : ALL_ITEMS) {
			Registry.register(Registry.ITEM, item.getIdentifier(), item);
		}
		for (ItemWrapper item : FoodItem.ALL_ITEMS) {
			Registry.register(Registry.ITEM, item.getIdentifier(), item);
		}
		SuperWeapons.registerAll();
		for (BlockWrapper block : ALL_BLOCKS) {
			Registry.register(Registry.BLOCK, block.getIdentifier(), block);
			if (block.doBlockItem) {
				Registry.register(Registry.ITEM, block.getIdentifier(), block.getBlockItem());
			}
		}
		REDSTONE_CAPACITOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":capacitor",
				BlockEntityType.Builder.create(RedstoneCapacitorBlockEntity::new, REDSTONE_CAPACITOR_BLOCK).build(null)
		);
		REDSTONE_INFERRER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":inferrer",
				BlockEntityType.Builder.create(RedstoneInferrerBlockEntity::new, REDSTONE_INFERRER_BLOCK).build(null)
		);
		ServerSidePacketRegistry.INSTANCE.register(SET_CHANNEL_PACKET, FrequencyTunerItem::PACKIDGE);
		ServerSidePacketRegistry.INSTANCE.register(SET_TRANSMISSION_PACKET, RemoteRedstoneInferrerItem::PACKIDGE);
	}
	
	@Override
	public void onInitializeClient() {
		for (BlockWrapper block : ALL_BLOCKS) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, block.getRenderLayer());
		}
	}
	
}
