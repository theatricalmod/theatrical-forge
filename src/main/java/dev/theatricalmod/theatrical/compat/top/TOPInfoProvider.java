package dev.theatricalmod.theatrical.compat.top;

import dev.theatricalmod.theatrical.TheatricalMod;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {


    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(TheatricalMod.MOD_ID, "default");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        if(blockState.getBlock() instanceof ITOPInfoProvider){
            ITOPInfoProvider itopInfoProvider = (ITOPInfoProvider) blockState.getBlock();
            itopInfoProvider.addProbeInfo(probeMode, iProbeInfo, playerEntity, world, blockState, iProbeHitData);
        }
    }

    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(this);
        return null;
    }
}
