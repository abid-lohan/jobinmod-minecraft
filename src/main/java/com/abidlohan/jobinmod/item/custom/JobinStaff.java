package com.abidlohan.jobinmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class JobinStaff extends SwordItem {

    public JobinStaff(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()){
            Level level = pContext.getLevel();
            BlockPos clickedPos = pContext.getClickedPos();
            Player player = pContext.getPlayer();

            EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level, null, null,
                    player, clickedPos, MobSpawnType.TRIGGERED, true, true);
            EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level, null, null,
                    player, clickedPos, MobSpawnType.TRIGGERED, true, true);

            pContext.getItemInHand().hurtAndBreak(1, player, p -> {
                p.broadcastBreakEvent(pContext.getHand());
            });
            player.getCooldowns().addCooldown(pContext.getItemInHand().getItem(), 20);
        }

        return super.useOn(pContext);
    }

}
