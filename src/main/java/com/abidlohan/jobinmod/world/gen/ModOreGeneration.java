package com.abidlohan.jobinmod.world.gen;

import com.abidlohan.jobinmod.JobinMod;
import com.abidlohan.jobinmod.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModOreGeneration{

    public static final List<ConfiguredFeature<?, ?>> OVERWORLD_ORES = new ArrayList<>();

    public static void generateOres(final BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> jobinOre = Feature.ORE.configured(new OreConfiguration(
                List.of(
                        OreConfiguration.target(
                                OreConfiguration.Predicates.STONE_ORE_REPLACEABLES, ModBlocks.JOBIN_ORE.get().defaultBlockState()
                        ),
                        OreConfiguration.target(
                                OreConfiguration.Predicates.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_JOBIN_ORE.get().defaultBlockState()
                        )
                ), 3)).rangeUniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)).squared().rarity(10);

        OVERWORLD_ORES.add(register("jobin_ore", jobinOre));
    }

    public static <Config extends FeatureConfiguration> ConfiguredFeature<Config, ?> register(String name, ConfiguredFeature<Config, ?> feature){
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(JobinMod.MOD_ID, name), feature);
    }

    @Mod.EventBusSubscriber(modid = JobinMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusSubscriber{
        @SubscribeEvent
        public static void biomeLoading(BiomeLoadingEvent event){
            List<Supplier<ConfiguredFeature<?, ?>>> features = event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

            if(event.getCategory() != Biome.BiomeCategory.THEEND && event.getCategory() != Biome.BiomeCategory.NETHER){
                ModOreGeneration.OVERWORLD_ORES.forEach(ore -> features.add(() -> ore));
            }
        }
    }

}
