package dev.theatricalmod.theatrical.api;

import net.minecraft.core.BlockPos;

import java.util.Arrays;

public interface IConnectable {

    BlockPos getPos();

    void setPos(BlockPos newPos);

    ConnectableType[] getAllowedTypes();

    default boolean acceptsType(ConnectableType connectableType){
        return Arrays.stream(getAllowedTypes()).anyMatch(c -> c == connectableType);
    }
}
