package dev.theatricalmod.theatrical.entity;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TheatricalEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TheatricalMod.MOD_ID);

    public static final RegistryObject<EntityType<FallingLightEntity>> FALLING_LIGHT = ENTITIES.register("falling_light", () -> EntityType.Builder.<FallingLightEntity>of(FallingLightEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .build("falling_light"));
}
