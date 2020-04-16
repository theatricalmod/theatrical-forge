package dev.theatricalmod.theatrical.compat.top;

import dev.theatricalmod.theatrical.TheatricalMod;
import java.util.function.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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
