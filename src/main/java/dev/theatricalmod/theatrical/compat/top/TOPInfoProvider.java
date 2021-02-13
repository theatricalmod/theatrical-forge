package dev.theatricalmod.theatrical.compat.top;

import dev.theatricalmod.theatrical.TheatricalMod;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

    @Override
    public String getID() {
        return TheatricalMod.MOD_ID + ":default";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
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
