package com.robotman2412.litemod.block.inferrer;

import com.mojang.authlib.GameProfile;
import com.robotman2412.litemod.block.AbstractRedstoneTileBlock;
import com.robotman2412.litemod.block.ChannelIdentifier;
import com.robotman2412.litemod.util.RelativeDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.MessageType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class RedstoneInferrerBlock extends AbstractRedstoneTileBlock implements BlockEntityProvider {
	
	public static final BooleanProperty IS_SENDER = BooleanProperty.of("sending");
	
	public RedstoneInferrerBlock() {
		super(Settings.copy(Blocks.REPEATER).nonOpaque(), "redstone_inferrer", new IOConfiguration(
				new IOMode(),
				new IOMode(IOModeEnum.IN),
				new IOMode(),
				new IOMode()
		), true);
		setDefaultState(getStateManager().getDefaultState()
				.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
				.with(Properties.POWERED, false)
				.with(IS_SENDER, true)
		);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return facing.equals(state.get(Properties.HORIZONTAL_FACING)) && !state.get(IS_SENDER) && state.get(Properties.POWERED) ? 15 : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return getWeakRedstonePower(state, view, pos, facing);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		RedstoneInferrerBlockEntity ent = (RedstoneInferrerBlockEntity) world.getBlockEntity(pos);
		boolean powered = getBooleanPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		if (ent.isSending()) {
			if (ent.channel.id == 69) {
				int i = 0;
				i++;
			}
			ent.setPowered(powered);
			world.setBlockState(pos, state.with(Properties.POWERED, powered));
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		boolean isSender = !state.get(IS_SENDER);
		RedstoneInferrerBlockEntity ent = (RedstoneInferrerBlockEntity) world.getBlockEntity(pos);
		ChannelIdentifier channel = ent.channel;
		UUID uuid = channel.ownerUUID();
		if (!world.isClient()) {
			ServerWorld serverWorld = (ServerWorld) world; 
			MinecraftServer server = serverWorld.getServer();
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			String sTxRx = isSender ? "block.robot_litemod.redstone_inferrer.tx" : "block.robot_litemod.redstone_inferrer.rx";
			if (uuid != null) {
				UserCache cache = server.getUserCache();
				GameProfile profile = cache.getByUuid(uuid);
				String name = profile != null ? profile.getName() : "unknown player";
				Text text = new TranslatableText(sTxRx).append(new LiteralText(name))
						.append(new TranslatableText("robot_litemod.inferrer_channel_private"))
						.append(new LiteralText(channel.id + ""));
				
				serverPlayerEntity.sendChatMessage(text, MessageType.GAME_INFO);
			}
			else
			{
				Text text = new TranslatableText(sTxRx)
						.append(new TranslatableText("robot_litemod.inferrer_channel_public"))
						.append(new LiteralText(channel.id + ""));
				
				serverPlayerEntity.sendChatMessage(text, MessageType.GAME_INFO);
			}
		}
		if (uuid != null && !uuid.equals(player.getUuid())) {
			//cant mess with other people's inferrers
			return ActionResult.CONSUME;
		}
		ent.setSending(isSender);
		double x = pos.getX() + 0.5f;
		double y = pos.getY() + 0.5f;
		double z = pos.getZ() + 0.5f;
		world.playSound(x, y, z, new SoundEvent(new Identifier("minecraft", "block.lever.click")), SoundCategory.BLOCKS, 1f, isSender ? 0.7f : 0.55f, false);
		neighborUpdate(state, world, pos, null, null, false);
		boolean powered = getBooleanPowerForSide(RelativeDirection.BACKWARD, world, state, pos);
		if (ent.isSending()) {
			ent.setPowered(powered);
			state = state.with(Properties.POWERED, powered);
		}
		else
		{
			state = state.with(Properties.POWERED, ent.isPowered());
		}
		world.setBlockState(pos, state.with(IS_SENDER, isSender));
		return ActionResult.CONSUME;
	}
	
	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.getCutout();
	}
	
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction facing = state.get(Properties.HORIZONTAL_FACING);
		if (state.get(Properties.POWERED)) {
			stupidParticla(15, state, world, pos, random, facing.getOffsetX() * 0.1, 0.1, facing.getOffsetZ() * 0.1);
			stupidParticla(15, state, world, pos, random, facing.getOffsetX() * -0.2, 0.1, facing.getOffsetZ() * -0.2);
		}
	}
	
	protected void stupidParticla(int i, BlockState state, World world, BlockPos pos, Random random, double xOffs, double yOffs, double zOffs) {
		double x = pos.getX() + 0.5D + (random.nextFloat() - 0.5D) * 0.2D + xOffs;
		double y = (pos.getY() + 0.0625D) + yOffs;
		double z = pos.getZ() + 0.5D + (random.nextFloat() - 0.5D) * 0.2D + zOffs;
		float m = i / 15.0F;
		float r = m * 0.6F + 0.4F;
		float g = Math.max(0.0F, m * m * 0.7F - 0.5F);
		float b = Math.max(0.0F, m * m * 0.6F - 0.7F);
		world.addParticle(new DustParticleEffect(r, g, b, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(
				Properties.HORIZONTAL_FACING,
				IS_SENDER,
				Properties.POWERED
		);
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new RedstoneInferrerBlockEntity();
	}
	
}
