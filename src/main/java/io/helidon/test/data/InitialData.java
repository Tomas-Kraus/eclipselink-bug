/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
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
package io.helidon.test.data;

import java.time.Year;
import java.util.List;

import io.helidon.test.model.Garage;
import io.helidon.test.model.GarageDoor;
import io.helidon.test.model.House;
import io.helidon.test.model.Kitchen;

import jakarta.persistence.EntityManager;

public class InitialData {

    private static final System.Logger LOGGER = System.getLogger(InitialData.class.getName());

    private InitialData() {
        throw new UnsupportedOperationException("No instances of Data are allowed");
    }

    /**
     * List of {@code Kitchen}s. Array index matches ID.
     */
    public static final Kitchen[] KITCHENS = new Kitchen[] {
            new Kitchen(2, 3),
            new Kitchen(3, 3),
            new Kitchen(2, 4)
    };

    /**
     * List of {@code GarageDoor}s. Array index matches ID.
     */
    public static final GarageDoor[] GARAGE_DOORS = new GarageDoor[] {
            new GarageDoor(2, 2),
            new GarageDoor(2, 3)
    };

    /**
     * List of {@code Garage}s. Array index matches ID.
     */
    public static final Garage[] GARAGES = new Garage[] {
            new Garage(9, GARAGE_DOORS[0], Garage.Type.Attached),
            new Garage(12, GARAGE_DOORS[0], Garage.Type.Attached),
            new Garage(16, GARAGE_DOORS[1], Garage.Type.Detached)
    };

    /**
     * List of {@code House}s. Array index matches ID.
     */
    public static final House[] HOUSES = new House[] {
            new House(100, GARAGES[0], KITCHENS[0], 700, 1, "P 001", 1500000, Year.of(2024)),
            new House(130, GARAGES[1], KITCHENS[1], 900, 1, "P 002", 1900000, Year.of(2023)),
            new House(180, GARAGES[2], KITCHENS[2], 1200, 2, "P 003", 2500000, Year.of(2025))
    };

    /**
     * Initialize database data.
     *
     * @param em JPA {@link jakarta.persistence.EntityManager}
     */
    public static void init(EntityManager em) {
        LOGGER.log(System.Logger.Level.DEBUG, "Data initialization");
        for (int i = 0; i < InitialData.KITCHENS.length; i++) {
            em.persist(InitialData.KITCHENS[i]);
        }
        for (int i = 0; i < InitialData.GARAGE_DOORS.length; i++) {
            em.persist(InitialData.GARAGE_DOORS[i]);
        }
        for (int i = 0; i < InitialData.GARAGES.length; i++) {
            em.persist(InitialData.GARAGES[i]);
        }
        for (int i = 0; i < InitialData.HOUSES.length; i++) {
            em.persist(InitialData.HOUSES[i]);
        }
    }

    /**
     * Verify database data
     */
    public static void verify(EntityManager em) {
        LOGGER.log(System.Logger.Level.DEBUG, "Data verification");
        List<House> houses = em.createQuery("SELECT h FROM House h", House.class)
                .getResultList();
        LOGGER.log(System.Logger.Level.DEBUG, String.format(" - houses: %d", houses.size()));
    }

}
