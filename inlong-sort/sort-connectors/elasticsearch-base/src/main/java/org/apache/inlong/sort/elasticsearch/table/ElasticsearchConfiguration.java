/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.sort.elasticsearch.table;

import org.apache.flink.configuration.ReadableConfig;
import org.apache.inlong.sort.elasticsearch.ElasticsearchSinkBase;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.BULK_FLUSH_BACKOFF_DELAY_OPTION;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.BULK_FLUSH_BACKOFF_MAX_RETRIES_OPTION;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.BULK_FLUSH_BACKOFF_TYPE_OPTION;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.BULK_FLUSH_INTERVAL_OPTION;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.PASSWORD_OPTION;
import static org.apache.inlong.sort.elasticsearch.table.ElasticsearchOptions.USERNAME_OPTION;

/**
 * Accessor methods to elasticsearch options.
 */
public class ElasticsearchConfiguration {

    protected final ReadableConfig config;
    private final ClassLoader classLoader;

    public ElasticsearchConfiguration(ReadableConfig config, ClassLoader classLoader) {
        this.config = config;
        this.classLoader = classLoader;
    }

    public ReadableConfig getConfig() {
        return config;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getDocumentType() {
        return config.get(ElasticsearchOptions.DOCUMENT_TYPE_OPTION);
    }

    public int getBulkFlushMaxActions() {
        int maxActions = config.get(ElasticsearchOptions.BULK_FLUSH_MAX_ACTIONS_OPTION);
        // convert 0 to -1, because Elasticsearch client use -1 to disable this configuration.
        return maxActions == 0 ? -1 : maxActions;
    }

    public long getBulkFlushMaxByteSize() {
        long maxSize = config.get(ElasticsearchOptions.BULK_FLASH_MAX_SIZE_OPTION).getBytes();
        // convert 0 to -1, because Elasticsearch client use -1 to disable this configuration.
        return maxSize == 0 ? -1 : maxSize;
    }

    public long getBulkFlushInterval() {
        long interval = config.get(BULK_FLUSH_INTERVAL_OPTION).toMillis();
        // convert 0 to -1, because Elasticsearch client use -1 to disable this configuration.
        return interval == 0 ? -1 : interval;
    }

    public Optional<String> getUsername() {
        return config.getOptional(USERNAME_OPTION);
    }

    public Optional<String> getPassword() {
        return config.getOptional(PASSWORD_OPTION);
    }

    public boolean isBulkFlushBackoffEnabled() {
        return config.get(BULK_FLUSH_BACKOFF_TYPE_OPTION) != ElasticsearchOptions.BackOffType.DISABLED;
    }

    public Optional<ElasticsearchSinkBase.FlushBackoffType> getBulkFlushBackoffType() {
        switch (config.get(BULK_FLUSH_BACKOFF_TYPE_OPTION)) {
            case CONSTANT:
                return Optional.of(ElasticsearchSinkBase.FlushBackoffType.CONSTANT);
            case EXPONENTIAL:
                return Optional.of(ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL);
            default:
                return Optional.empty();
        }
    }

    public Optional<Integer> getBulkFlushBackoffRetries() {
        return config.getOptional(BULK_FLUSH_BACKOFF_MAX_RETRIES_OPTION);
    }

    public Optional<Long> getBulkFlushBackoffDelay() {
        return config.getOptional(BULK_FLUSH_BACKOFF_DELAY_OPTION).map(Duration::toMillis);
    }

    public boolean isDisableFlushOnCheckpoint() {
        return !config.get(ElasticsearchOptions.FLUSH_ON_CHECKPOINT_OPTION);
    }

    public String getIndex() {
        return config.get(ElasticsearchOptions.INDEX_OPTION);
    }

    public String getKeyDelimiter() {
        return config.get(ElasticsearchOptions.KEY_DELIMITER_OPTION);
    }

    public Optional<String> getRoutingField() {
        return config.getOptional(ElasticsearchOptions.ROUTING_FIELD_NAME);
    }

    public Optional<String> getPathPrefix() {
        return config.getOptional(ElasticsearchOptions.CONNECTION_PATH_PREFIX);
    }

    public Optional<String> getMultipleIndexPattern() {
        return config.getOptional(ElasticsearchOptions.SINK_MULTIPLE_INDEX_PATTERN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ElasticsearchConfiguration that = (ElasticsearchConfiguration) o;
        return Objects.equals(config, that.config) && Objects.equals(classLoader, that.classLoader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, classLoader);
    }
}
