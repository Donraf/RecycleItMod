package com.donraf.recycleitmod.screen;

import com.donraf.recycleitmod.RecycleItMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Arrays;

public class SynthesizerScreen extends AbstractContainerScreen<SynthesizerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RecycleItMod.MOD_ID, "textures/gui/synthesizer_gui.png");

    private static final ArrayList<ResourceLocation> vanillaTextures = new ArrayList<>(Arrays.asList(
            ResourceLocation.withDefaultNamespace("textures/item/coal.png"),
            ResourceLocation.withDefaultNamespace("textures/item/amethyst_shard.png"),
            ResourceLocation.withDefaultNamespace("textures/item/quartz.png"),
            ResourceLocation.withDefaultNamespace("textures/item/glowstone_dust.png"),
            ResourceLocation.withDefaultNamespace("textures/item/diamond.png"),
            ResourceLocation.withDefaultNamespace("textures/item/emerald.png"),
            ResourceLocation.withDefaultNamespace("textures/item/lapis_lazuli.png"),
            ResourceLocation.withDefaultNamespace("textures/item/redstone.png"),
            ResourceLocation.withDefaultNamespace("textures/item/copper_ingot.png"),
            ResourceLocation.withDefaultNamespace("textures/item/iron_ingot.png"),
            ResourceLocation.withDefaultNamespace("textures/item/gold_ingot.png"),
            ResourceLocation.withDefaultNamespace("textures/item/netherite_ingot.png")
    ));

    public SynthesizerScreen(SynthesizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        for (int i = 0; i < vanillaTextures.size(); i++) {
            ResourceLocation texture = vanillaTextures.get(i);
            pGuiGraphics.blit(texture, x + 80 + 24 * (i % 4), y + 8 + 26 * (i / 4), 0, 0, 16, 16,16,16);
        }

//        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics pGuiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            pGuiGraphics.blit(TEXTURE, x + 85, y + 30, 176, 0, 8, menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
