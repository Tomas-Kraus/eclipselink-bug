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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "GARAGE")
public class Garage {

    private static final String ID_GENERATOR = "GARAGE_ID_GENERATOR";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_GENERATOR)
    @SequenceGenerator(name = ID_GENERATOR, sequenceName = ID_GENERATOR, allocationSize = 1)
    private int id;

    private int area;

    private GarageDoor door;

    @Enumerated(EnumType.ORDINAL)
    private Type type;

    public Garage(int id, int area, GarageDoor door, Type type) {
        this.id = id;
        this.area = area;
        this.door = door;
        this.type = type;
    }

    public Garage(int area, GarageDoor door, Type type) {
        this(-1, area, door, type);
    }

    public Garage() {
        this(-1, -1, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public GarageDoor getDoor() {
        return door;
    }

    public void setDoor(GarageDoor door) {
        this.door = door;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        Attached,
        Detached,
        TuckUnder
    }

}
