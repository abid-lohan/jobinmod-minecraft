package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AdultSensor extends Sensor<AgeableMob> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerLevel pLevel, AgeableMob pAttacker) {
      pAttacker.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((p_148262_) -> {
         this.setNearestVisibleAdult(pAttacker, p_148262_);
      });
   }

   private void setNearestVisibleAdult(AgeableMob pMob, List<LivingEntity> pNearbyEntities) {
      Optional<AgeableMob> optional = pNearbyEntities.stream().filter((p_148254_) -> {
         return p_148254_.getType() == pMob.getType();
      }).map((p_148259_) -> {
         return (AgeableMob)p_148259_;
      }).filter((p_148251_) -> {
         return !p_148251_.isBaby();
      }).findFirst();
      pMob.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
   }
}