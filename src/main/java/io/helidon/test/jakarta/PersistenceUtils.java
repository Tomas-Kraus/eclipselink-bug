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
package io.helidon.test.jakarta;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;

public class PersistenceUtils {

    public static EntityManagerFactory createEmf(PersistenceConfig config) {
        return new EmfBuilder(config).build();
    }

    private PersistenceUtils() {
        throw new UnsupportedOperationException("No instances of PersistenceUtils are allowed");
    }

    private static class EmfBuilder {

        private final PersistenceConfiguration pc;
        private final PersistenceConfig config;


        private EmfBuilder(PersistenceConfig config) {
            this.pc = new PersistenceConfiguration(config.persistenceUnitName());
            this.config = config;
        }

        private EntityManagerFactory build() {
            if (!config.providerClassName().isEmpty()) {
                pc.provider(config.providerClassName());
            }
            pc.property("jakarta.persistence.jdbc.url", config.connectionString());
            pc.property("jakarta.persistence.jdbc.user", config.username());
            pc.property("jakarta.persistence.jdbc.password", new String(config.password()));
            pc.property("jakarta.persistence.jdbc.driver", config.jdbcDriverClassName());
            config.managedClasses().forEach(this::managedClass);
            pc.transactionType(switch (config.transactionType()) {
                case "RESOURCE_LOCAL" -> PersistenceUnitTransactionType.RESOURCE_LOCAL;
                case "JTA" -> PersistenceUnitTransactionType.JTA;
                default -> throw new IllegalStateException(
                        String.format("Unsupported PersistenceUnitTransactionType %s", config.transactionType()));
            });
            pc.validationMode(switch (config.validationMode()) {
                case "AUTO" -> ValidationMode.AUTO;
                case "CALLBACK" -> ValidationMode.CALLBACK;
                case "NONE" -> ValidationMode.NONE;
                default -> throw new IllegalStateException(
                        String.format("Unsupported ValidationMode %s", config.validationMode()));
            });
            pc.sharedCacheMode(switch (config.sharedCacheMode()){
                case "ALL" -> SharedCacheMode.ALL;
                case "NONE" -> SharedCacheMode.NONE;
                case "ENABLE_SELECTIVE" -> SharedCacheMode.ENABLE_SELECTIVE;
                case "DISABLE_SELECTIVE" -> SharedCacheMode.DISABLE_SELECTIVE;
                case "UNSPECIFIED" -> SharedCacheMode.UNSPECIFIED;
                default -> throw new IllegalStateException(
                        String.format("Unsupported SharedCacheMode %s", config.sharedCacheMode()));
            });
            config.properties().forEach(pc::property);
            return Persistence.createEntityManagerFactory(pc);
        }

        private void managedClass(String className) {
            try {
                pc.managedClass(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new PersistenceConfigException(
                        String.format("Could not add \"%s\" class to persistence config", className), e);
            }
        }
    }

}
