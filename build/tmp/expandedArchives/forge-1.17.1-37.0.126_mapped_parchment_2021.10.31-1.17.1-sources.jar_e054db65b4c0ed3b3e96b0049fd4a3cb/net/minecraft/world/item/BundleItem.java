package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;

public class BundleItem extends Item {
   private static final String TAG_ITEMS = "Items";
   public static final int MAX_WEIGHT = 64;
   private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

   public BundleItem(Item.Properties p_150726_) {
      super(p_150726_);
   }

   public static float getFullnessDisplay(ItemStack pStack) {
      return (float)getContentWeight(pStack) / 64.0F;
   }

   public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
      if (pAction != ClickAction.SECONDARY) {
         return false;
      } else {
         ItemStack itemstack = pSlot.getItem();
         if (itemstack.isEmpty()) {
            removeOne(pStack).ifPresent((p_150740_) -> {
               add(pStack, pSlot.safeInsert(p_150740_));
            });
         } else if (itemstack.getItem().canFitInsideContainerItems()) {
            int i = (64 - getContentWeight(pStack)) / getWeight(itemstack);
            add(pStack, pSlot.safeTake(itemstack.getCount(), i, pPlayer));
         }

         return true;
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
      if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer)) {
         if (pOther.isEmpty()) {
            removeOne(pStack).ifPresent(pAccess::set);
         } else {
            pOther.shrink(add(pStack, pOther));
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
      if (dropContents(itemstack, pPlayer)) {
         pPlayer.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public boolean isBarVisible(ItemStack pStack) {
      return getContentWeight(pStack) > 0;
   }

   public int getBarWidth(ItemStack pStack) {
      return Math.min(1 + 12 * getContentWeight(pStack) / 64, 13);
   }

   public int getBarColor(ItemStack pStack) {
      return BAR_COLOR;
   }

   private static int add(ItemStack pBundleStack, ItemStack pInsertedStack) {
      if (!pInsertedStack.isEmpty() && pInsertedStack.getItem().canFitInsideContainerItems()) {
         CompoundTag compoundtag = pBundleStack.getOrCreateTag();
         if (!compoundtag.contains("Items")) {
            compoundtag.put("Items", new ListTag());
         }

         int i = getContentWeight(pBundleStack);
         int j = getWeight(pInsertedStack);
         int k = Math.min(pInsertedStack.getCount(), (64 - i) / j);
         if (k == 0) {
            return 0;
         } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            Optional<CompoundTag> optional = getMatchingItem(pInsertedStack, listtag);
            if (optional.isPresent()) {
               CompoundTag compoundtag1 = optional.get();
               ItemStack itemstack = ItemStack.of(compoundtag1);
               itemstack.grow(k);
               itemstack.save(compoundtag1);
               listtag.remove(compoundtag1);
               listtag.add(0, (Tag)compoundtag1);
            } else {
               ItemStack itemstack1 = pInsertedStack.copy();
               itemstack1.setCount(k);
               CompoundTag compoundtag2 = new CompoundTag();
               itemstack1.save(compoundtag2);
               listtag.add(0, (Tag)compoundtag2);
            }

            return k;
         }
      } else {
         return 0;
      }
   }

   private static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList) {
      return pStack.is(Items.BUNDLE) ? Optional.empty() : pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((p_150755_) -> {
         return ItemStack.isSameItemSameTags(ItemStack.of(p_150755_), pStack);
      }).findFirst();
   }

   private static int getWeight(ItemStack pStack) {
      if (pStack.is(Items.BUNDLE)) {
         return 4 + getContentWeight(pStack);
      } else {
         if ((pStack.is(Items.BEEHIVE) || pStack.is(Items.BEE_NEST)) && pStack.hasTag()) {
            CompoundTag compoundtag = pStack.getTagElement("BlockEntityTag");
            if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
               return 64;
            }
         }

         return 64 / pStack.getMaxStackSize();
      }
   }

   private static int getContentWeight(ItemStack pStack) {
      return getContents(pStack).mapToInt((p_150785_) -> {
         return getWeight(p_150785_) * p_150785_.getCount();
      }).sum();
   }

   private static Optional<ItemStack> removeOne(ItemStack pStack) {
      CompoundTag compoundtag = pStack.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         return Optional.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         if (listtag.isEmpty()) {
            return Optional.empty();
         } else {
            int i = 0;
            CompoundTag compoundtag1 = listtag.getCompound(0);
            ItemStack itemstack = ItemStack.of(compoundtag1);
            listtag.remove(0);
            if (listtag.isEmpty()) {
               pStack.removeTagKey("Items");
            }

            return Optional.of(itemstack);
         }
      }
   }

   private static boolean dropContents(ItemStack pStack, Player pPlayer) {
      CompoundTag compoundtag = pStack.getOrCreateTag();
      if (!compoundtag.contains("Items")) {
         return false;
      } else {
         if (pPlayer instanceof ServerPlayer) {
            ListTag listtag = compoundtag.getList("Items", 10);

            for(int i = 0; i < listtag.size(); ++i) {
               CompoundTag compoundtag1 = listtag.getCompound(i);
               ItemStack itemstack = ItemStack.of(compoundtag1);
               pPlayer.drop(itemstack, true);
            }
         }

         pStack.removeTagKey("Items");
         return true;
      }
   }

   private static Stream<ItemStack> getContents(ItemStack pStack) {
      CompoundTag compoundtag = pStack.getTag();
      if (compoundtag == null) {
         return Stream.empty();
      } else {
         ListTag listtag = compoundtag.getList("Items", 10);
         return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      getContents(pStack).forEach(nonnulllist::add);
      return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(pStack)));
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", getContentWeight(pStack), 64)).withStyle(ChatFormatting.GRAY));
   }

   public void onDestroyed(ItemEntity pItemEntity) {
      ItemUtils.onContainerDestroyed(pItemEntity, getContents(pItemEntity.getItem()));
   }
}