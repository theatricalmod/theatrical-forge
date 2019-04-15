package dev.theatricalmod.theatrical.api;

import dev.theatricalmod.theatrical.tiles.cables.CableType;

public interface IAcceptsCable {

    CableType[] getAcceptedCables();

}
