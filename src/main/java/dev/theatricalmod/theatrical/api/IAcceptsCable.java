package dev.theatricalmod.theatrical.api;

import net.minecraft.core.Direction;

public interface IAcceptsCable {

    CableType[] getAcceptedCables(Direction side);

}
