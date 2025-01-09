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

import java.util.List;

import io.helidon.test.model.City;

import jakarta.persistence.EntityManager;

public class InitialData {

    private static final System.Logger LOGGER = System.getLogger(InitialData.class.getName());

    private InitialData() {
        throw new UnsupportedOperationException("No instances of Data are allowed");
    }


    /**
     * List of {@code Pokemons}s. Array index matches ID.
     */
    public static final City[] CITIES = new City[] {
            null, // skip index 0
            new City("Prague", "Bohemia", 1384732),
            new City("Munich", "Germany", 1510378),
            new City("Vienna", "Austria", 2014614),
            new City("Bratislava", "Slovakia", 475503),
            new City("Warsaw", "Poland", 1862402),
            new City("Berlin", "Germany", 3878100),
            new City("Paris", "France", 2102650)
    };


    /**
     * Initialize database data.
     *
     * @param em JPA {@link jakarta.persistence.EntityManager}
     */
    public static void init(EntityManager em) {
        LOGGER.log(System.Logger.Level.DEBUG, "Data initialization");
        for (int i = 1; i < InitialData.CITIES.length; i++) {
            em.persist(InitialData.CITIES[i]);
        }
    }

    /**
     * Verify database data.
     * Just logs number of the records in the database.
     */
    public static void verify(EntityManager em) {
        LOGGER.log(System.Logger.Level.DEBUG, "Data verification");
        List<City> pokemons = em.createQuery("SELECT c FROM City c", City.class)
                .getResultList();
        LOGGER.log(System.Logger.Level.DEBUG, String.format(" - pokemons: %d", pokemons.size()));
    }

}
