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
import com.georlegacy.general.theatrical.logic.transport.dmx.entities.io.DMXInput;
import com.georlegacy.general.theatrical.logic.transport.dmx.entities.io.DMXOutput;

/**
 * Network Plane, used on desk DMX interfaces for DMX I/O
 *
 * @author James Conway
 * @see DMXOutput
 * @see DMXInput
 */
public class DMXNetworkPlane {

    private final DMXOutput[] outputs;
    private final DMXInput[] inputs;

    public DMXNetworkPlane(int outputs, int inputs) {
        this.outputs = new DMXOutput[outputs];
        this.inputs = new DMXInput[inputs];
    }

    public DMXOutput[] getOutputs() {
        return outputs;
    }

    public DMXInput[] getInputs() {
        return inputs;
    }

    public DMXOutput getOutput(int index) {
        if (index > (outputs.length - 1) || index < 0) {
            throw new DMXValueOutOfBoundsException(
                "There are only " + outputs.length + " outputs on this plane");
        } else {
            return outputs[index];
        }
    }

    public DMXInput getInput(int index) {
        if (index > (inputs.length - 1) || index < 0) {
            throw new DMXValueOutOfBoundsException(
                "There are only " + inputs.length + " inputs on this plane");
        } else {
            return inputs[index];
        }
    }

}
