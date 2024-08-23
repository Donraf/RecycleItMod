package com.donraf.recycleitmod.screen;

import com.donraf.recycleitmod.RecycleItMod;
import com.donraf.recycleitmod.network.RecycleItModPacketHandler;
import com.donraf.recycleitmod.network.packet.RecycleItModMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;

public class SynthesizerScreen extends AbstractContainerScreen<SynthesizerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RecycleItMod.MOD_ID, "textures/gui/synthesizer_gui.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(RecycleItMod.MOD_ID, "textures/gui/synthesizer_gui_arrow.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int MAX_BUTTONS_ON_SCREEN = 7;
    private static final int BUTTON_X = 5;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 88;
    private static final int BUTTON_LIST_TOP_POS_Y = 18;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component TITLE_LABEL = Component.translatable("block.recycleitmod.synthesizer_block");
    int recycleItem;
    int scrollOff;

    public static final ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(
            new ItemStack(Items.COAL),
            new ItemStack(Items.GLOWSTONE_DUST),
            new ItemStack(Items.LAPIS_LAZULI),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.QUARTZ),
            new ItemStack(Items.AMETHYST_SHARD),
            new ItemStack(Items.COPPER_INGOT),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.NETHERITE_INGOT)
    ));
    public static final String[] costs = {
            "50",
            "50",
            "50",
            "50",
            "50",
            "50",
            "50",
            "100",
            "150",
            "1000",
            "1500",
            "2000"
    };

    public SynthesizerScreen(SynthesizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        for (int i = 0; i < MAX_BUTTONS_ON_SCREEN; i++) {
            renderButton(x + BUTTON_X, y + BUTTON_LIST_TOP_POS_Y + BUTTON_HEIGHT * i, i);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, 5, 6, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void renderButton(int x, int y, int index) {
        this.addRenderableWidget(new SynthesizerButton(x, y, index, btn -> {
            if (btn instanceof SynthesizerScreen.SynthesizerButton) {
                this.recycleItem = ((SynthesizerScreen.SynthesizerButton) btn).getIndex() + this.scrollOff;
                RecycleItModMessage msg = new RecycleItModMessage(this.recycleItem);
                RecycleItModPacketHandler.INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
            }
        }));
    }

    @OnlyIn(Dist.CLIENT)
    class SynthesizerButton extends Button {
        final int index;

        protected SynthesizerButton(int pX, int pY, int pIndex, OnPress pOnPress) {
            super(pX, pY, BUTTON_WIDTH, BUTTON_HEIGHT, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
            this.index = pIndex;
        }

        public int getIndex() {
            return this.index;
        }
    }

    private void renderScroller(GuiGraphics pGuiGraphics, int pPosX, int pPosY) {
        int i = items.size() + 1 - MAX_BUTTONS_ON_SCREEN;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }
            pGuiGraphics.blitSprite(SCROLLER_SPRITE, pPosX + SCROLL_BAR_START_X, pPosY + SCROLL_BAR_TOP_POS_Y + i1,
                    0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        } else {
            pGuiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, pPosX + SCROLL_BAR_START_X, pPosY + SCROLL_BAR_TOP_POS_Y,
                    0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
    }

    private void renderProgressArrow(GuiGraphics pGuiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            pGuiGraphics.blit(ARROW_TEXTURE, x + 180, y + 29, 0, 0, 15, menu.getScaledProgress(),15, 21);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int k = y + 16 + 1;

        this.renderScroller(pGuiGraphics, x, y);

        int i1 = 0;
        for (ItemStack item : items) {
            if (!this.canScroll(items.size()) || i1 >= this.scrollOff && i1 < MAX_BUTTONS_ON_SCREEN + this.scrollOff) {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                int j1 = k + 3;
                pGuiGraphics.drawString(this.font, costs[i1], x + 32, j1 + 4, 4210752, false);
                pGuiGraphics.renderFakeItem(item, x + 10, j1);
                pGuiGraphics.pose().popPose();
                k += 20;
            }
            i1++;
        }
        pGuiGraphics.drawString(this.font, String.valueOf(menu.getRecyclePoints()), x + 161, y + 55, 4210752, false);
        renderProgressArrow(pGuiGraphics, x, y);
        RenderSystem.enableDepthTest();
    }

    private boolean canScroll(int numItems) {
        return numItems > MAX_BUTTONS_ON_SCREEN;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        int i = items.size();
        if (this.canScroll(i)) {
            int j = i - MAX_BUTTONS_ON_SCREEN;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - pScrollY), 0, j);
        }
        return true;
    }
}
