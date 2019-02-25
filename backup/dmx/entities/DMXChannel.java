/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.logic.transport.dmx.entities;

import com.georlegacy.general.theatrical.core.exceptions.dmx.DMXValueOutOfBoundsException;
import com.georlegacy.general.theatrical.logic.transport.dmx.entities.event.DMXChannelValueSetEvent;
import com.georlegacy.general.theatrical.logic.transport.dmx.entities.event.subscribers.DMXChannelValueSetEventSubscriber;
import java.util.HashSet;
import java.util.Set;

/**
 * One of the 512 channels stored in a {@link DMXUniverse}</br> Stores a percentage value
 *
 * @author James Conway
 */
public class DMXChannel {

    private double value;

    private final Set<DMXChannelValueSetEventSubscriber> eventSubscribers;

    private final DMXUniverse universe;

    public DMXChannel(DMXUniverse universe) {
        this.value = 0;
        this.eventSubscribers = new HashSet<>();
        this.universe = universe;
    }

    public void setValue(double value) {
        if (value > 100 || value < 0) {
            throw new DMXValueOutOfBoundsException(
                "The value you provided was not a valid percentile");
        } else {
            setAbsoluteValue(value);
        }
    }

    public boolean incrementValue() {
        if (value > 99) {
            return false;
        }
        setAbsoluteValue(value + 1d);
        return true;
    }

    public boolean decrementValue() {
        if (value < 1) {
            return false;
        }
        setAbsoluteValue(value - 1d);
        return true;
    }

    public double getValue() {
        return value;
    }

    public DMXUniverse getUniverse() {
        return universe;
    }

    public Set<DMXChannelValueSetEventSubscriber> getEventSubscribers() {
        return eventSubscribers;
    }

    public void addEventSubscriber(DMXChannelValueSetEventSubscriber eventSubscriber) {
        this.eventSubscribers.add(eventSubscriber);
    }

    public void removeEventSubscriber(DMXChannelValueSetEventSubscriber eventSubscriber) {
        this.eventSubscribers.remove(eventSubscriber);
    }

    private void callDMXChannelValueSetEvent(DMXChannelValueSetEvent event) {
        for (DMXChannelValueSetEventSubscriber eventSubscriber : this.getEventSubscribers()) {
            eventSubscriber.onValueSet(event);
        }
    }

    private void setAbsoluteValue(double value) {
        double previousValue = this.value;
        this.value = value;
        callDMXChannelValueSetEvent(new DMXChannelValueSetEvent(
            this,
            previousValue,
            value
        ));
    }

}
