/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 * Copyright (c) 2025 IBM Corporation. All rights reserved.
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
package io.helidon.test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "GARAGE_DOOR")
public class GarageDoor {

    private static final String ID_GENERATOR = "GARAGE_DOOR_ID_GENERATOR";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_GENERATOR)
    @SequenceGenerator(name = ID_GENERATOR, sequenceName = ID_GENERATOR, allocationSize = 1)
    private int id;

    private int height;

    private int width;

    public GarageDoor(int id, int height, int width) {
        this.id = id;
        this.height = height;
        this.width = width;
    }

    public GarageDoor(int height, int width) {
        this(-1, height, width);
    }

    public GarageDoor() {
        this(-1, -1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
