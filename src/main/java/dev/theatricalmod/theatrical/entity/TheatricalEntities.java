package dev.theatricalmod.theatrical.entity;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheatricalEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TheatricalMod.MOD_ID);

    public static final RegistryObject<EntityType<FallingLightEntity>> FALLING_LIGHT = ENTITIES.register("falling_light", () -> EntityType.Builder.<FallingLightEntity>create(FallingLightEntity::new, EntityClassification.MISC)
            .size(0.98F, 0.98F)
            .trackingRange(10)
            .func_233608_b_(20)
            .build("falling_light"));
}
