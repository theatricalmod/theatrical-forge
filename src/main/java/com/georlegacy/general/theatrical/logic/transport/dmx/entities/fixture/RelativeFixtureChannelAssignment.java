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

package com.georlegacy.general.theatrical.logic.transport.dmx.entities.fixture;

/**
 * Assignments to relative channels within a fixture for example a dimmer fixture may have an assignment
 * of {@code relativeChannel=1;<br>assignmentDescription=Intensity;}
 *
 * @author James Conway
 */
public class RelativeFixtureChannelAssignment {

    private final int relativeChannel;

    private final String assignmentDescription;

    public RelativeFixtureChannelAssignment(int relativeChannel, String assignmentDescription) {
        this.relativeChannel = relativeChannel;
        this.assignmentDescription = assignmentDescription;
    }

    public int getRelativeChannel() {
        return relativeChannel;
    }

    public String getAssignmentDescription() {
        return assignmentDescription;
    }

}
