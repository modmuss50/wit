package me.modmuss50.wit;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.BlockProxy;
import cpw.mods.fml.common.registry.GameRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhatIsThis implements ClientModInitializer {

	private static Multimap<ModContainer, BlockProxy> blockRegistry;

	@Override
	public void onInitializeClient() {
		System.out.println("What is this?");
	}

	private static void updateBlockRegistry() {
		if (blockRegistry != null) {
			return;
		}
		try {
			Field field = GameRegistry.class.getDeclaredField("blockRegistry");
			field.setAccessible(true);

			blockRegistry = (Multimap<ModContainer, BlockProxy>) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void render(ScaledResolution resolution, GuiIngame gui, FontRenderer font, Minecraft minecraft, RenderItem renderItem) {
		EntityClientPlayerMP clientPlayer = minecraft.g;
		World world = clientPlayer.worldObj;
		MovingObjectPosition rayTraceResult = minecraft.x;
		List<String> lines = new ArrayList<>();
		ItemStack renderStack = null;

		if (rayTraceResult == null) {
			return;
		}

		if (rayTraceResult.typeOfHit == EnumMovingObjectType.TILE) {
			int blockId = world.getBlockId(rayTraceResult.blockX, rayTraceResult.blockY, rayTraceResult.blockZ);
			int blockMeta = world.getBlockMetadata(rayTraceResult.blockX, rayTraceResult.blockY, rayTraceResult.blockZ);
			Block block = Block.blocksList[blockId];
			ItemStack itemStack = new ItemStack(block, blockMeta);
			lines.add(itemStack.getDisplayName());

			lines.addAll(getExtraLines(world, rayTraceResult.blockX, rayTraceResult.blockY, rayTraceResult.blockZ, block));

			lines.add(getModID(block));

			renderStack = itemStack;
		} else if (rayTraceResult.typeOfHit == EnumMovingObjectType.ENTITY && false) {
			Entity entity = rayTraceResult.entityHit;
			if (entity == null) {
				return;
			}
			lines.add(entity.getEntityName());
		}

		if (lines.isEmpty()) {
			return;
		}

		int x = resolution.getScaledWidth() / 2;
		int y = 10;

		int maxLineWidth = 0;
		for (int i = 0; i < lines.size(); i++) {
			int lineWidth = font.getStringWidth(lines.get(i));
			if (lineWidth > maxLineWidth) {
				maxLineWidth = lineWidth;
			}
		}

		RenderHelper.disableStandardItemLighting();
		int boxWidth = maxLineWidth + 25;
		Gui.drawRect(x - (boxWidth / 2) - 2, y - 5, x + (boxWidth / 2) + 2, y + 6 + (lines.size() * 10), -16777216);


		for (int i = 0; i < lines.size(); i++) {
			int lineWidth = lines.get(i).length() * 5;
			gui.drawString(font, lines.get(i), 10 + resolution.getScaledWidth() / 2 - (lineWidth / 2), 12 + (i * 10), 0xFFFFFF);
		}

		GL11.glEnable(32826);
		RenderHelper.enableGUIStandardItemLighting();
		if (renderStack != null) {
			renderItem.renderItemAndEffectIntoGUI(font, minecraft.o, renderStack, x - (boxWidth / 2), 8);
		}
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(32826);
	}

	private static String getModID(Block block) {
		updateBlockRegistry();

		for (Map.Entry<ModContainer, BlockProxy> entry : blockRegistry.entries()) {
			if (entry.getValue() == block) {
				return entry.getKey().getName();
			}
		}

		return "Minecraft";
	}

	private static List<String> getExtraLines(World world, int x, int y, int z, Block block) {
		ArrayList<String> lines = new ArrayList<>();

		//TODO requires packets
//		if (block instanceof BlockJukeBox) {
//			TileEntityRecordPlayer tile = (TileEntityRecordPlayer)world.getBlockTileEntity(x, y, z);
//			if (tile.record != null) {
//				lines.add("Record: " + tile.record.getDisplayName());
//			}
//		}

		return lines;
	}
}
