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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
@Prototype.Configured
interface PersistenceConfigBlueprint {

    /**
     * Persistence unit name.
     *
     * @return the persistence unit name
     */
    @Option.Configured
    @Option.Default("@default")
    String persistenceUnitName();

    /**
     * Persistence provider class name.
     *
     * @return the persistence provider class name
     */
    @Option.Configured
    @Option.Default("")
    String providerClassName();

    /**
     * Transaction type of the entity managers created by the {@link jakarta.persistence.EntityManagerFactory}.
     *
     * @return the transaction type.
     */
    @Option.Configured
    @Option.Default("RESOURCE_LOCAL")
    @Option.AllowedValue(value = "JTA", description = "Transaction management via JTA.")
    @Option.AllowedValue(value = "RESOURCE_LOCAL", description = "Resource-local transaction management.")
    String transactionType();

    /**
     * Managed persistence entities.
     *
     * @return the entities list
     */
    @Option.Configured
    List<String> managedClasses();

    /**
     * Whether classes in the root of the persistence unit that have not been explicitly listed are to be included
     * in the set of managed classes.
     *
     * @return whether to exclude unlisted classes
     */
    @Option.Configured
    @Option.Default("false")
    boolean excludeUnlistedClasses();

    /**
     * The validation mode to be used by the persistence provider for the persistence unit.
     *
     * @return the validation mode
     */
    @Option.Configured
    @Option.Default("AUTO")
    @Option.AllowedValue(value = "AUTO", description = "If a Bean Validation provider is present in the environment, "
            + "the persistence provider must perform the automatic validation of entities. If no Bean Validation provider "
            + "is present in the environment, no lifecycle event validation takes place. This is the default behavior.")
    @Option.AllowedValue(value = "CALLBACK", description = "The persistence provider must perform the lifecycle event "
            + "validation. It is an error if there is no Bean Validation provider present in the environment.")
    @Option.AllowedValue(value = "NONE", description = "The persistence provider must not perform lifecycle event validation.")
    String validationMode();

    /**
     * The specification of how the provider must use a second-level cache for the persistence unit.
     *
     * @return the shared cache mode
     */
    @Option.Configured
    @Option.Default("UNSPECIFIED")
    @Option.AllowedValue(value = "ALL", description = "All entities and entity-related state and data are cached.")
    @Option.AllowedValue(value = "NONE", description = "Caching is disabled for the persistence unit.")
    @Option.AllowedValue(value = "ENABLE_SELECTIVE", description = "Caching is enabled for all entities for which "
                         + "Cacheable(true) is specified. All other entities are not cached.")
    @Option.AllowedValue(value = "DISABLE_SELECTIVE", description = "Caching is enabled for all entities except those "
                         + "for which Cacheable(false) is specified. Entities for which Cacheable(false) is specified "
                         + "are not cached.")
    @Option.AllowedValue(value = "UNSPECIFIED", description = "Caching behavior is undefined: provider-specific defaults "
                         + "may apply.")
    String sharedCacheMode();

    /**
     * Database connection string.
     *
     * @return the connection string
     */
    @Option.Configured
    @Option.Required
    String connectionString();

    /**
     * Username for the database connection.
     *
     * @return the username
     */
    @Option.Configured
    Optional<String> username();

    /**
     * Password for the database connection.
     *
     * @return the password
     */
    @Option.Configured
    @Option.Confidential
    Optional<char[]> password();

    /**
     * JDBC driver class for database connection.
     *
     * @return the JDBC driver class name
     */
    @Option.Configured
    @Option.Required
    String jdbcDriverClassName();

    /**
     * Additional persistence unit or connection properties.
     *
     * @return the properties
     */
    @Option.Configured
    @Option.Singular("property")
    Map<String, String> properties();

}
