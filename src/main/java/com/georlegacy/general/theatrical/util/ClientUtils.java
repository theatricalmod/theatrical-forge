package com.georlegacy.general.theatrical.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientUtils {


    public static <E> List<E> optimize(List<E> list)
    {
        if (list.isEmpty())
        {
            return Collections.emptyList();
        }
        else if (list.size() == 1)
        {
            return Collections.singletonList(list.get(0));
        }

        return Arrays.asList(list.toArray((E[]) new Object[0]));
    }


}
