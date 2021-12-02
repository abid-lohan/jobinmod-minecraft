package net.minecraft.world.level.levelgen.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StructureFeatureIndexSavedData extends SavedData {
   private static final String TAG_REMAINING_INDEXES = "Remaining";
   private static final String TAG_All_INDEXES = "All";
   private final LongSet all;
   private final LongSet remaining;

   private StructureFeatureIndexSavedData(LongSet pAll, LongSet pRemaining) {
      this.all = pAll;
      this.remaining = pRemaining;
   }

   public StructureFeatureIndexSavedData() {
      this(new LongOpenHashSet(), new LongOpenHashSet());
   }

   public static StructureFeatureIndexSavedData load(CompoundTag pTag) {
      return new StructureFeatureIndexSavedData(new LongOpenHashSet(pTag.getLongArray("All")), new LongOpenHashSet(pTag.getLongArray("Remaining")));
   }

   /**
    * Used to save the {@code SavedData} to a {@code CompoundTag}
    * @param pCompound the {@code CompoundTag} to save the {@code SavedData} to
    */
   public CompoundTag save(CompoundTag pCompound) {
      pCompound.putLongArray("All", this.all.toLongArray());
      pCompound.putLongArray("Remaining", this.remaining.toLongArray());
      return pCompound;
   }

   public void addIndex(long pChunkPos) {
      this.all.add(pChunkPos);
      this.remaining.add(pChunkPos);
   }

   public boolean hasStartIndex(long pChunkPos) {
      return this.all.contains(pChunkPos);
   }

   public boolean hasUnhandledIndex(long pChunkPos) {
      return this.remaining.contains(pChunkPos);
   }

   public void removeIndex(long pChunkPos) {
      this.remaining.remove(pChunkPos);
   }

   public LongSet getAll() {
      return this.all;
   }
}