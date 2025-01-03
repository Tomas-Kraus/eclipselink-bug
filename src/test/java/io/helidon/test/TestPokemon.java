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
import java.util.List;

import io.helidon.config.Config;
import io.helidon.test.data.InitialData;
import io.helidon.test.jakarta.PersistenceConfig;
import io.helidon.test.model.Pokemon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static io.helidon.test.data.InitialData.POKEMONS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestPokemon {

    private static final MySQLContainer<?> CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));;
    private static final PersistenceConfig CONFIG = PersistenceConfig.create(Config.create());
    private static EntityManagerFactory EMF = null;

    public TestPokemon() {
    }

    @Test
    public void testSelect() {
        try (EntityManager em = EMF.createEntityManager()) {
            List<Pokemon> pokemons = em.createNamedQuery("Pokemon.alive", Pokemon.class)
                    .setParameter("alive", true)
                    .getResultList();
            assertThat(pokemons.size(), is(POKEMONS.length - 1));
        }
    }

    @BeforeClass
    public static void before() {
        // Container setup startup
        if (CONFIG.connectionString().isEmpty() || CONFIG.username().isEmpty() || CONFIG.password().isEmpty()) {
            throw new IllegalStateException("Value of connection-string, username or password is missing in config");
        }
        if (CONFIG.jdbcDriverClassName().isEmpty()) {
            throw new IllegalStateException("Value of jdbc-driver-class-name is missing in config");
        }
        CONFIG.username().ifPresent(CONTAINER::withUsername);
        CONFIG.password().ifPresent(TestPokemon::withPassword);
        CONFIG.connectionString().ifPresent(TestPokemon::withDatabaseName);
        CONTAINER.start();
        // Persistence provider setup (spec 3.2)
        String url = replacePortInUrl(CONFIG.connectionString().get(), CONTAINER.getMappedPort(3306));
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration("test");
        persistenceConfiguration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
        persistenceConfiguration.managedClass(io.helidon.test.model.League.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Creature.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Pokemon.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Region.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Team.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Trainer.class);
        persistenceConfiguration.managedClass(io.helidon.test.model.Type.class);
        persistenceConfiguration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        CONFIG.properties().forEach(persistenceConfiguration::property);
        persistenceConfiguration.property("jakarta.persistence.jdbc.driver", CONFIG.jdbcDriverClassName().get());
        persistenceConfiguration.property("jakarta.persistence.jdbc.user", CONFIG.username().get());
        persistenceConfiguration.property("jakarta.persistence.jdbc.password", new String(CONFIG.password().get()));
        persistenceConfiguration.property("jakarta.persistence.jdbc.url", url);
        EMF = Persistence.createEntityManagerFactory(persistenceConfiguration);
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

    private static void withPassword(char[] password) {
        CONTAINER.withPassword(new String(password));
    }

    private static void withDatabaseName(String connectionString) {
        String dbName = dbNameFromUri(uriFromDbUrl(connectionString));
        CONTAINER.withDatabaseName(dbName);
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
                    String.format("Database JDBC url shall start with \"jdbc:\" prefix, but URC is %s", src));
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
