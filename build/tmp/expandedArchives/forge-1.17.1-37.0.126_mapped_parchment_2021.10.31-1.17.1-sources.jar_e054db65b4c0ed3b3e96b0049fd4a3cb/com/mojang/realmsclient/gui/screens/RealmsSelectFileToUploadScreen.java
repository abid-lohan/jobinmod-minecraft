package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   static final Component WORLD_TEXT = new TranslatableComponent("selectWorld.world");
   static final Component REQUIRES_CONVERSION_TEXT = new TranslatableComponent("selectWorld.conversion");
   static final Component HARDCORE_TEXT = (new TranslatableComponent("mco.upload.hardcore")).withStyle(ChatFormatting.DARK_RED);
   static final Component CHEATS_TEXT = new TranslatableComponent("selectWorld.cheats");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   Button uploadButton;
   List<LevelSummary> levelList = Lists.newArrayList();
   int selectedWorld = -1;
   RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private final Runnable callback;

   public RealmsSelectFileToUploadScreen(long pWorldId, int pSlotId, RealmsResetWorldScreen pLastScreen, Runnable pCallback) {
      super(new TranslatableComponent("mco.upload.select.world.title"));
      this.lastScreen = pLastScreen;
      this.worldId = pWorldId;
      this.slotId = pSlotId;
      this.callback = pCallback;
   }

   private void loadLevelList() throws Exception {
      this.levelList = this.minecraft.getLevelSource().getLevelList().stream().sorted((p_89512_, p_89513_) -> {
         if (p_89512_.getLastPlayed() < p_89513_.getLastPlayed()) {
            return 1;
         } else {
            return p_89512_.getLastPlayed() > p_89513_.getLastPlayed() ? -1 : p_89512_.getLevelId().compareTo(p_89513_.getLevelId());
         }
      }).collect(Collectors.toList());

      for(LevelSummary levelsummary : this.levelList) {
         this.worldSelectionList.addEntry(levelsummary);
      }

   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception exception) {
         LOGGER.error("Couldn't load level list", (Throwable)exception);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(new TextComponent("Unable to load worlds"), Component.nullToEmpty(exception.getMessage()), this.lastScreen));
         return;
      }

      this.addWidget(this.worldSelectionList);
      this.uploadButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableComponent("mco.upload.button.name"), (p_89532_) -> {
         this.upload();
      }));
      this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
      this.addRenderableWidget(new Button(this.width / 2 + 6, this.height - 32, 153, 20, CommonComponents.GUI_BACK, (p_89525_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.addLabel(new RealmsLabel(new TranslatableComponent("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.addLabel(new RealmsLabel(new TranslatableComponent("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
      }

   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         LevelSummary levelsummary = this.levelList.get(this.selectedWorld);
         this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, levelsummary, this.callback));
      }

   }

   public void render(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
      this.renderBackground(pMatrixStack);
      this.worldSelectionList.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
      drawCenteredString(pMatrixStack, this.font, this.title, this.width / 2, 13, 16777215);
      super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   static Component gameModeName(LevelSummary p_89535_) {
      return p_89535_.getGameMode().getLongDisplayName();
   }

   static String formatLastPlayed(LevelSummary p_89539_) {
      return DATE_FORMAT.format(new Date(p_89539_.getLastPlayed()));
   }

   @OnlyIn(Dist.CLIENT)
   class Entry extends ObjectSelectionList.Entry<RealmsSelectFileToUploadScreen.Entry> {
      private final LevelSummary levelSummary;
      private final String name;
      private final String id;
      private final Component info;

      public Entry(LevelSummary p_89560_) {
         this.levelSummary = p_89560_;
         this.name = p_89560_.getLevelName();
         this.id = p_89560_.getLevelId() + " (" + RealmsSelectFileToUploadScreen.formatLastPlayed(p_89560_) + ")";
         if (p_89560_.isRequiresConversion()) {
            this.info = RealmsSelectFileToUploadScreen.REQUIRES_CONVERSION_TEXT;
         } else {
            Component component;
            if (p_89560_.isHardcore()) {
               component = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
            } else {
               component = RealmsSelectFileToUploadScreen.gameModeName(p_89560_);
            }

            if (p_89560_.hasCheats()) {
               component = component.copy().append(", ").append(RealmsSelectFileToUploadScreen.CHEATS_TEXT);
            }

            this.info = component;
         }

      }

      public void render(PoseStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks) {
         this.renderItem(pMatrixStack, pIndex, pLeft, pTop);
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void renderItem(PoseStack p_167475_, int p_167476_, int p_167477_, int p_167478_) {
         String s;
         if (this.name.isEmpty()) {
            s = RealmsSelectFileToUploadScreen.WORLD_TEXT + " " + (p_167476_ + 1);
         } else {
            s = this.name;
         }

         RealmsSelectFileToUploadScreen.this.font.draw(p_167475_, s, (float)(p_167477_ + 2), (float)(p_167478_ + 1), 16777215);
         RealmsSelectFileToUploadScreen.this.font.draw(p_167475_, this.id, (float)(p_167477_ + 2), (float)(p_167478_ + 12), 8421504);
         RealmsSelectFileToUploadScreen.this.font.draw(p_167475_, this.info, (float)(p_167477_ + 2), (float)(p_167478_ + 12 + 10), 8421504);
      }

      public Component getNarration() {
         Component component = CommonComponents.joinLines(new TextComponent(this.levelSummary.getLevelName()), new TextComponent(RealmsSelectFileToUploadScreen.formatLastPlayed(this.levelSummary)), RealmsSelectFileToUploadScreen.gameModeName(this.levelSummary));
         return new TranslatableComponent("narrator.select", component);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.Entry> {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
      }

      public void addEntry(LevelSummary p_89588_) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new Entry(p_89588_));
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      public boolean isFocused() {
         return RealmsSelectFileToUploadScreen.this.getFocused() == this;
      }

      public void renderBackground(PoseStack pMatrixStack) {
         RealmsSelectFileToUploadScreen.this.renderBackground(pMatrixStack);
      }

      public void setSelected(@Nullable RealmsSelectFileToUploadScreen.Entry pEntry) {
         super.setSelected(pEntry);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(pEntry);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
      }
   }
}