package com.robotman2412.litemod;

import com.robotman2412.litemod.block.*;
import com.robotman2412.litemod.block.grave.TheStoneOfGravesBlock;
import com.robotman2412.litemod.block.grave.TheStoneOfGravesBlockEntity;
import com.robotman2412.litemod.block.inferrer.InferrerChannelMap;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlock;
import com.robotman2412.litemod.block.inferrer.RedstoneInferrerBlockEntity;
import com.robotman2412.litemod.block.itemduct.GoldenItemductBlock;
import com.robotman2412.litemod.block.itemduct.GoldenItemductBlockEntity;
import com.robotman2412.litemod.block.pushableredstone.*;
import com.robotman2412.litemod.effect.HealthConfusionEffect;
import com.robotman2412.litemod.effect.StatusEffexWrapper;
import com.robotman2412.litemod.entity.TestLivingEntity;
import com.robotman2412.litemod.foods.BLENDOMATOR9000BlockEntity;
import com.robotman2412.litemod.foods.BlenderRecipe;
import com.robotman2412.litemod.foods.FoodItem;
import com.robotman2412.litemod.item.*;
import com.robotman2412.litemod.item.superweapon.SuperWeapons;
import com.robotman2412.litemod.util.CustomItemGroupAppender;
import com.robotman2412.litemod.weaopn.AK47Type2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricLitemod implements ModInitializer {
	
	public static final String MOD_ID = "robot_litemod";
	
	public static final Identifier SET_CHANNEL_PACKET = new Identifier(MOD_ID, "set_channel");
	public static final Identifier SET_TRANSMISSION_PACKET = new Identifier(MOD_ID, "set_transmission");
	
	public static final Identifier BLENDOMATOR9000_CONTAINER = new Identifier(MOD_ID, "blendomator_9000");
	public static final Identifier BLENDER_RECIPE = new Identifier(MOD_ID, "blender");
	public static RecipeType<BlenderRecipe> BLENDER_RECIPE_TYPE;
	
	//region effex
	public static final StatusEffexWrapper HEALTH_CONFUSION = new HealthConfusionEffect();
	
	public static final StatusEffexWrapper[] ALL_EFFEX = new StatusEffexWrapper[] {
		HEALTH_CONFUSION
	};
	//endregion effex
	
	//region items
	public static final ItemGroup KITCHEN_SUPPLIES = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "kitchen_supplies"))
			.icon(FabricLitemod::getKitchenKnifeItem)
			.appendItems(new CustomItemGroupAppender(MOD_ID, "kitchen_supplies"))
			.build();
	
	public static final ItemGroup WEAPONS = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "weaponry"))
			.icon(FabricLitemod::getAK47Item)
			.appendItems(new CustomItemGroupAppender(MOD_ID, "weaponry"))
			.build();
	
	public static final EssenceOfBullshiteItem ESSENCE_OF_BULLSHITE_ITEM = new EssenceOfBullshiteItem();
	public static final FrequencyTunerItem FREQUENCY_TUNER_ITEM = new FrequencyTunerItem();
	public static final RemoteRedstoneInferrerItem REMOTE_REDSTONE_INFERRER_ITEM = new RemoteRedstoneInferrerItem();
	public static final ItemWrapper KITCHEN_KNIFE_ITEM = new ItemWrapper(new Item.Settings().maxCount(1).maxDamage(2048).group(KITCHEN_SUPPLIES), "kitchen_knife");
	public static final SlimecompassItem SLIME_COMPASS_ITEM = new SlimecompassItem();
	
	public static final AK47Type2 AK_47_TYPE_2 = new AK47Type2();
	
	public static final ItemWrapper[] ALL_ITEMS = {
			ESSENCE_OF_BULLSHITE_ITEM,
			FREQUENCY_TUNER_ITEM,
			REMOTE_REDSTONE_INFERRER_ITEM,
			KITCHEN_KNIFE_ITEM,
			SLIME_COMPASS_ITEM,
			
			AK_47_TYPE_2
	};
	//endregion items
	
	//region blocks
	public static final RedstoneInferrerBlock REDSTONE_INFERRER_BLOCK = new RedstoneInferrerBlock();
	public static final RedstoneCapacitorBlock REDSTONE_CAPACITOR_BLOCK = new RedstoneCapacitorBlock();
	public static final CuttingBoardBlock CUTTING_BOARD_BLOCK = new CuttingBoardBlock();
	public static final ForceLoadificatorBlock FORCE_LOADIFICATOR_BLOCK = new ForceLoadificatorBlock();
	public static final TheStoneOfGravesBlock THE_STONE_OF_GRAVES_BLOCK = new TheStoneOfGravesBlock();
	
	public static final PushaPeater PUSHA_PEATER_BLOCK = new PushaPeater();
	public static final BlockItem PUSHA_PEATER_ITEM = new BlockItem(PUSHA_PEATER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
	public static final PushaParator PUSHA_PARATOR_BLOCK = new PushaParator();
	public static final BlockItem PUSHA_PARATOR_ITEM = new BlockItem(PUSHA_PARATOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
	public static final PushaRedstone PUSHA_REDSTONE_BLOCK = new PushaRedstone();
	public static final BlockItem PUSHA_REDSTONE_ITEM = new BlockItem(PUSHA_REDSTONE_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
	public static final PushaRedstoneTorch PUSHA_REDSTONE_TORCH_BLOCK = new PushaRedstoneTorch();
	public static final PushaRedstoneWallTorch PUSHA_REDSTONE_WALL_TORCH_BLOCK = new PushaRedstoneWallTorch();
	public static final BlockItem PUSHA_REDSTONE_TORCH_ITEM = new WallStandingBlockItem(PUSHA_REDSTONE_TORCH_BLOCK, PUSHA_REDSTONE_WALL_TORCH_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
	
	public static final GoldenItemductBlock GOLDEN_ITEMDUCT_BLOCK = new GoldenItemductBlock();
	public static BlockEntityType<GoldenItemductBlockEntity> GOLDEN_ITEMDUCT_BLOCK_ENTITY;
	
	public static final BlockWrapper FACTORY_BLOCK = new BlockWrapper(Block.Settings.copy(Blocks.YELLOW_TERRACOTTA), "factory", true);
	public static final BlockWrapper FACTORY_MOSSY_BLOCK = new BlockWrapper(Block.Settings.copy(Blocks.YELLOW_TERRACOTTA), "factory_mossy", true);
	
	public static BlockEntityType<RedstoneCapacitorBlockEntity> REDSTONE_CAPACITOR_BLOCK_ENTITY;
	public static BlockEntityType<RedstoneInferrerBlockEntity> REDSTONE_INFERRER_BLOCK_ENTITY;
	public static BlockEntityType<BLENDOMATOR9000BlockEntity> BLENDOMATOR9000_BLOCK_ENTITY;
	public static BlockEntityType<TheStoneOfGravesBlockEntity> THE_STONE_OF_GRAVES_BLOCK_ENTITY;
	
	public static final BlockWrapper[] ALL_BLOCKS = {
			REDSTONE_INFERRER_BLOCK,
			REDSTONE_CAPACITOR_BLOCK,
			CUTTING_BOARD_BLOCK,
			FORCE_LOADIFICATOR_BLOCK,
			THE_STONE_OF_GRAVES_BLOCK,
			
			GOLDEN_ITEMDUCT_BLOCK,
			
			FACTORY_MOSSY_BLOCK,
			FACTORY_BLOCK
	};
	//endregion blocks
	
	//region entities
	public static EntityType<TestLivingEntity> TEST_LIVING_ENTITY_TYPE;
	//endregion entities
	
	private static ItemStack getAK47Item() {
		return new ItemStack(AK_47_TYPE_2);
	}
	
	private static ItemStack getKitchenKnifeItem() {
		return new ItemStack(KITCHEN_KNIFE_ITEM);
	}
	
	@Override
	public void onInitialize() {
		for (StatusEffexWrapper wrapper : ALL_EFFEX) {
			Registry.register(Registry.STATUS_EFFECT, wrapper.getIdentifier(), wrapper);
		}
		ServerTickCallback.EVENT.register(InferrerChannelMap::tick);
		for (ItemWrapper item : ALL_ITEMS) {
			Registry.register(Registry.ITEM, item.getIdentifier(), item);
		}
		for (ItemWrapper item : FoodItem.ALL_ITEMS) {
			Registry.register(Registry.ITEM, item.getIdentifier(), item);
		}
		for (BlockWrapper block : FoodItem.ALL_BLOCKS) {
			Registry.register(Registry.BLOCK, block.getIdentifier(), block);
			if (block.doBlockItem) {
				Registry.register(Registry.ITEM, block.getIdentifier(), block.getBlockItem());
			}
		}
		SuperWeapons.registerAll();
		for (BlockWrapper block : ALL_BLOCKS) {
			Registry.register(Registry.BLOCK, block.getIdentifier(), block);
			if (block.doBlockItem) {
				Registry.register(Registry.ITEM, block.getIdentifier(), block.getBlockItem());
			}
		}
		
		//pushable redstone
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "movable_repeater"), PUSHA_PEATER_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "movable_repeater"), PUSHA_PEATER_ITEM);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "movable_comparator"), PUSHA_PARATOR_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "movable_comparator"), PUSHA_PARATOR_ITEM);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "movable_redstone_wire"), PUSHA_REDSTONE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "movable_redstone_wire"), PUSHA_REDSTONE_ITEM);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "movable_redstone_torch"), PUSHA_REDSTONE_TORCH_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "movable_redstone_wall_torch"), PUSHA_REDSTONE_WALL_TORCH_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "movable_redstone_torch"), PUSHA_REDSTONE_TORCH_ITEM);
		
		
		TEST_LIVING_ENTITY_TYPE = EntityType.Builder.create(TestLivingEntity::new, EntityCategory.CREATURE)
				//this is so dumb... why is Entity#getDimensions(EntityPose) a thing?!?
				//spent actual hours trying to find out why it looked right but was wrong
				.setDimensions(3f, 2.4f)
				.build(new Identifier(MOD_ID, "test_living_entity").toString());
		Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "test_living_entity"), TEST_LIVING_ENTITY_TYPE);
		
		
		REDSTONE_CAPACITOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":capacitor",
				BlockEntityType.Builder.create(RedstoneCapacitorBlockEntity::new, REDSTONE_CAPACITOR_BLOCK).build(null)
		);
		REDSTONE_INFERRER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":inferrer",
				BlockEntityType.Builder.create(RedstoneInferrerBlockEntity::new, REDSTONE_INFERRER_BLOCK).build(null)
		);
		BLENDOMATOR9000_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":blendomator_9000",
				BlockEntityType.Builder.create(BLENDOMATOR9000BlockEntity::new,
						FoodItem.BLENDOMATOR_9000_WHITE,
						FoodItem.BLENDOMATOR_9000_ORANGE,
						FoodItem.BLENDOMATOR_9000_MAGENTA,
						FoodItem.BLENDOMATOR_9000_LIGHT_BLUE,
						FoodItem.BLENDOMATOR_9000_YELLOW,
						FoodItem.BLENDOMATOR_9000_LIME,
						FoodItem.BLENDOMATOR_9000_PINK,
						FoodItem.BLENDOMATOR_9000_GRAY,
						FoodItem.BLENDOMATOR_9000_LIGHT_GRAY,
						FoodItem.BLENDOMATOR_9000_CYAN,
						FoodItem.BLENDOMATOR_9000_PURPLE,
						FoodItem.BLENDOMATOR_9000_BLUE,
						FoodItem.BLENDOMATOR_9000_BROWN,
						FoodItem.BLENDOMATOR_9000_RED,
						FoodItem.BLENDOMATOR_9000_BLACK
				).build(null)
		);
		THE_STONE_OF_GRAVES_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":the_stone_of_graves",
				BlockEntityType.Builder.create(
						TheStoneOfGravesBlockEntity::new,
						THE_STONE_OF_GRAVES_BLOCK
				).build(null)
		);
		
		GOLDEN_ITEMDUCT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":golden_itemduct",
				BlockEntityType.Builder.create(GoldenItemductBlockEntity::new, GOLDEN_ITEMDUCT_BLOCK).build(null)
		);
		
		
		Registry.register(Registry.RECIPE_TYPE, BLENDER_RECIPE, BLENDER_RECIPE_TYPE = new RecipeType<BlenderRecipe>() {});
		Registry.register(Registry.RECIPE_SERIALIZER, BLENDER_RECIPE, BlenderRecipe.Serializer.INSTANCE);
		ContainerProviderRegistry.INSTANCE.registerFactory(BLENDOMATOR9000_CONTAINER, (syncId, id, player, buf) -> {
			final BlockEntity blockEntity = player.world.getBlockEntity(buf.readBlockPos());
			return((BLENDOMATOR9000BlockEntity) blockEntity).createMenu(syncId, player.inventory, player);
		});
		ServerSidePacketRegistry.INSTANCE.register(SET_CHANNEL_PACKET, FrequencyTunerItem::PACKIDGE);
		ServerSidePacketRegistry.INSTANCE.register(SET_TRANSMISSION_PACKET, RemoteRedstoneInferrerItem::PACKIDGE);
	}
	
}
