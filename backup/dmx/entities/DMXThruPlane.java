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

/**
 * DMXThruPlane allows passthru DMX signal and an output to a fixture, used in DMX Fixture Nodes
 *
 * @author James Conway
 */
public class DMXThruPlane {

    private final DMXUniverse universe;

    private final com.georlegacy.general.theatrical.logic.transport.dmx.entities.io.DMXOutput output;

    public DMXThruPlane(DMXUniverse universe,
        com.georlegacy.general.theatrical.logic.transport.dmx.entities.io.DMXOutput output) {
        this.universe = universe;
        this.output = output;
    }

    public DMXUniverse getUniverse() {
        return universe;
    }

    public com.georlegacy.general.theatrical.logic.transport.dmx.entities.io.DMXOutput getOutput() {
        return output;
    }

}
