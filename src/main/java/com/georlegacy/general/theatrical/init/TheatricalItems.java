package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.items.fixtureattr.gel.Gel;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Item store for Theatrical items
 *
 * @author James Conway
 */
public class TheatricalItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    static {
        ITEMS.add(new Gel());
    }

}
