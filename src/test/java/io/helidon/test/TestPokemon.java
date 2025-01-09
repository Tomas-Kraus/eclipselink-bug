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
package io.helidon.test;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.test.data.InitialData;
import io.helidon.test.jakarta.PersistenceConfig;
import io.helidon.test.jakarta.PersistenceUtils;
import io.helidon.test.model.City;
import io.helidon.test.model.CityId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static io.helidon.test.data.InitialData.CITIES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestPokemon {
    private static final DockerImageName IMAGE = DockerImageName.parse(
            "container-registry.oracle.com/database/free");
    private static final GenericContainer<?> CONTAINER = new GenericContainer<>(IMAGE);
    private static final int DB_PORT = 1521;
    private static Config CONFIG = Config.just(ConfigSources.classpath("application.yaml"));
    private static PersistenceConfig PERSISTENCE_CONFIG = PersistenceConfig.create(CONFIG);
    private static EntityManagerFactory EMF = null;

    public TestPokemon() {
    }

    @Test
    public void testSelectAll() {
        try (EntityManager em = EMF.createEntityManager()) {
            List<City> cities = em.createQuery("SELECT c FROM City c", City.class)
                    //.setParameter("alive", true)
                    .getResultList();
            assertThat(cities.size(), is(CITIES.length - 1));
        }
    }

    @Test
    public void testSelectCity() {
        try (EntityManager em = EMF.createEntityManager()) {
            List<String> cities = em.createQuery("SELECT name FROM City WHERE name = :name", String.class)
                    .setParameter("name", "Prague")
                    .getResultList();
            assertThat(cities.size(), is(1));
            assertThat(cities.getFirst(), is("Prague"));
        }
    }

    @Test
    public void testSelectKeys() {
        try (EntityManager em = EMF.createEntityManager()) {
            List<CityId> cities = em.createQuery(
                    "SELECT new io.helidon.test.model.CityId(c.name, c.stateName) FROM City c WHERE c.name = :name", CityId.class)
                    .setParameter("name", "Prague")
                    .getResultList();
            assertThat(cities.size(), is(1));
            assertThat(cities.getFirst().getName(), is("Prague"));
            assertThat(cities.getFirst().getStateName(), is("Bohemia"));
        }
    }

    @BeforeClass
    public static void before() {
        // Container setup and startup
        CONTAINER.withEnv("ORACLE_PWD", new String(PERSISTENCE_CONFIG.password()));
        CONTAINER.withExposedPorts(DB_PORT)
                .waitingFor(Wait.forHealthcheck()
                                    .withStartupTimeout(Duration.ofMinutes(5)));
        CONTAINER.start();
        // Config update
        Map<String, String> updatedNodes = new HashMap<>(1);
        String url = replacePortInUrl(PERSISTENCE_CONFIG.connectionString(),
                                      CONTAINER.getMappedPort(DB_PORT));
        updatedNodes.put("connection-string", url);
        CONFIG = Config.create(ConfigSources.create(updatedNodes),
                             ConfigSources.create(CONFIG));
        PERSISTENCE_CONFIG = PersistenceConfig.create(CONFIG);
        // Persistence provider setup (spec 3.2)
        EMF = PersistenceUtils.createEmf(PERSISTENCE_CONFIG);
        // Initialize data
        try (EntityManager em = EMF.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            et.begin();
            try {
                InitialData.init(em);
                InitialData.verify(em);
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    @AfterClass
    public static void after() {
        if (EMF != null) {
            EMF.close();
        }
        CONTAINER.stop();
    }

    private static URI uriFromDbUrl(String url) {
        // Search for beginning of authority element which is considered as mandatory
        int authIndex = url.indexOf("://");
        if (authIndex == -1) {
            throw new IllegalArgumentException("Missing URI authorioty initial sequence \"://\"");
        }
        if (authIndex == 0) {
            throw new IllegalArgumentException("Missing URI segment part");
        }
        // Search for last sub-scheme separator ':' before "://", it may not exist
        int separator = url.lastIndexOf(':', authIndex - 1);
        if (separator >= 0) {
            return URI.create(url.substring(separator + 1));
        }
        return URI.create(url);
    }

    private static String dbNameFromUri(URI dbUri) {
        String dbPath = dbUri.getPath();
        if (dbPath.isEmpty()) {
            throw new IllegalArgumentException("Database name is empty");
        }
        String dbName = dbPath.charAt(0) == '/' ? dbPath.substring(1) : dbPath;
        if (dbName.isEmpty()) {
            throw new IllegalArgumentException("Database name is empty");
        }
        return dbName;
    }

    private static String replacePortInUrl(String url, int port) {
        int begin = indexOfHostSeparator(url);
        if (begin >= 0) {
            int end = url.indexOf('/', begin + 3);
            int portBeg = url.indexOf(':', begin + 3);
            // Found port position in URL
            if (end > 0 && portBeg < end) {
                String frontPart = url.substring(0, portBeg + 1);
                String endPart = url.substring(end);
                return frontPart + port + endPart;
            } else {
                throw new IllegalStateException(
                        String.format("URL %s does not contain host and port part \"://host:port/\"", url));
            }
        } else {
            throw new IllegalStateException(
                    String.format("Could not find host separator \"://\" in URL %s", url));
        }
    }

    private static int indexOfHostSeparator(String src) {
        // First check DB type
        int jdbcSep = src.indexOf(':');
        String scheme = src.substring(0, jdbcSep);
        if (!"jdbc".equals(scheme)) {
            throw new IllegalArgumentException(
                    String.format("Database JDBC url shall start with \"jdbc:\" prefix, but URL is %s", src));
        }
        if (src.length() > jdbcSep + 2) {
            int typeSep = src.indexOf(':', jdbcSep + 1);
            String dbType = src.substring(jdbcSep + 1, typeSep);
            // Keeping switch here to simplify future extension
            return switch (dbType) {
                case "oracle" -> src.indexOf(":@");
                default -> src.indexOf("://");
            };
        } else {
            throw new IllegalArgumentException( "Database JDBC url has nothing after \"jdbc:\" prefix");
        }
    }

}
