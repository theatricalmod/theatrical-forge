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

package com.georlegacy.general.theatrical.logic.transport.dmx.entities.event;

import com.georlegacy.general.theatrical.logic.transport.dmx.entities.DMXChannel;
import com.georlegacy.general.theatrical.logic.transport.dmx.entities.event.subscribers.DMXChannelValueSetEventSubscriber;

/**
 * Event called when the value is changed in a {@link com.georlegacy.general.theatrical.logic.transport.dmx.entities.DMXChannel}
 *
 * @author James Conway
 * @see DMXChannelValueSetEventSubscriber
 */
public class DMXChannelValueSetEvent {

    private final DMXChannel channel;

    private final double previousValue;
    private final double newValue;

    public DMXChannelValueSetEvent(DMXChannel channel, double previousValue, double newValue) {
        this.channel = channel;

        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    public DMXChannel getChannel() {
        return channel;
    }

    public double getPreviousValue() {
        return previousValue;
    }

    public double getNewValue() {
        return newValue;
    }

}
