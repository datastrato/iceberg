/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Locale;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.catalog.TableIdentifierParser;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;
import org.apache.iceberg.relocated.com.google.common.collect.ImmutableList;
import org.apache.iceberg.util.JsonUtil;

/** Converts materialized view refresh states to and from JSON. */
public class RefreshStateParser {
  private static final String VIEW_VERSION_ID = "view-version-id";
  private static final String REFRESH_START_TIMESTAMP_MS = "refresh-start-timestamp-ms";
  private static final String SOURCE_STATES = "source-states";
  private static final String TYPE = "type";
  private static final String NAMESPACE = "namespace";
  private static final String NAME = "name";
  private static final String CATALOG = "catalog";
  private static final String UUID = "uuid";
  private static final String SNAPSHOT_ID = "snapshot-id";
  private static final String REF = "ref";
  private static final String VERSION_ID = "version-id";

  private RefreshStateParser() {}

  /**
   * Converts a refresh state to a JSON string.
   *
   * @param refreshState a refresh state
   * @return the refresh state JSON
   */
  public static String toJson(RefreshState refreshState) {
    return toJson(refreshState, false);
  }

  /**
   * Converts a refresh state to a JSON string.
   *
   * @param refreshState a refresh state
   * @param pretty whether to pretty-print the JSON
   * @return the refresh state JSON
   */
  public static String toJson(RefreshState refreshState, boolean pretty) {
    return JsonUtil.generate(generator -> toJson(refreshState, generator), pretty);
  }

  /**
   * Writes a refresh state as JSON.
   *
   * @param refreshState a refresh state
   * @param generator a JSON generator
   * @throws IOException if the generator cannot write the refresh state
   */
  public static void toJson(RefreshState refreshState, JsonGenerator generator) throws IOException {
    Preconditions.checkArgument(refreshState != null, "Cannot serialize null refresh state");

    generator.writeStartObject();
    generator.writeNumberField(VIEW_VERSION_ID, refreshState.viewVersionId());
    generator.writeNumberField(
        REFRESH_START_TIMESTAMP_MS, refreshState.refreshStartTimestampMillis());
    generator.writeArrayFieldStart(SOURCE_STATES);
    for (SourceState sourceState : refreshState.sourceStates()) {
      sourceStateToJson(sourceState, generator);
    }

    generator.writeEndArray();
    generator.writeEndObject();
  }

  /**
   * Parses a refresh state from a JSON string.
   *
   * @param json refresh state JSON
   * @return the parsed refresh state
   */
  public static RefreshState fromJson(String json) {
    Preconditions.checkArgument(json != null, "Cannot parse refresh state from null string");
    return JsonUtil.parse(json, RefreshStateParser::fromJson);
  }

  /**
   * Parses a refresh state from a JSON object.
   *
   * @param node a refresh state JSON object
   * @return the parsed refresh state
   */
  public static RefreshState fromJson(JsonNode node) {
    Preconditions.checkArgument(node != null, "Cannot parse refresh state from null object");
    Preconditions.checkArgument(
        node.isObject(), "Cannot parse refresh state from a non-object: %s", node);

    int viewVersionId = JsonUtil.getInt(VIEW_VERSION_ID, node);
    long refreshStartTimestampMillis = JsonUtil.getLong(REFRESH_START_TIMESTAMP_MS, node);
    JsonNode sourceStateNodes = JsonUtil.get(SOURCE_STATES, node);
    Preconditions.checkArgument(
        sourceStateNodes.isArray(),
        "Cannot parse source states from non-array: %s",
        sourceStateNodes);

    ImmutableList.Builder<SourceState> sourceStates = ImmutableList.builder();
    for (JsonNode sourceStateNode : sourceStateNodes) {
      sourceStates.add(sourceStateFromJson(sourceStateNode));
    }

    return new GenericRefreshState(
        viewVersionId, refreshStartTimestampMillis, sourceStates.build());
  }

  private static void sourceStateToJson(SourceState sourceState, JsonGenerator generator)
      throws IOException {
    Preconditions.checkArgument(sourceState != null, "Cannot serialize null source state");

    generator.writeStartObject();
    generator.writeStringField(TYPE, sourceState.type().name().toLowerCase(Locale.ROOT));
    writeIdentifier(sourceState.identifier(), generator);
    if (sourceState.catalog() != null) {
      generator.writeStringField(CATALOG, sourceState.catalog());
    }

    generator.writeStringField(UUID, sourceState.uuid());
    switch (sourceState.type()) {
      case TABLE:
        Preconditions.checkArgument(
            sourceState instanceof SourceTableState, "Invalid table source state: %s", sourceState);
        SourceTableState tableState = (SourceTableState) sourceState;
        generator.writeNumberField(SNAPSHOT_ID, tableState.snapshotId());
        if (tableState.ref() != null) {
          generator.writeStringField(REF, tableState.ref());
        }

        break;

      case VIEW:
        Preconditions.checkArgument(
            sourceState instanceof SourceViewState, "Invalid view source state: %s", sourceState);
        generator.writeNumberField(VERSION_ID, ((SourceViewState) sourceState).versionId());
        break;
    }

    generator.writeEndObject();
  }

  private static SourceState sourceStateFromJson(JsonNode node) {
    Preconditions.checkArgument(
        node != null && node.isObject(), "Cannot parse source state from a non-object: %s", node);

    String type = JsonUtil.getString(TYPE, node).toLowerCase(Locale.ROOT);
    TableIdentifier identifier = TableIdentifierParser.fromJson(node);
    String catalog = JsonUtil.getStringOrNull(CATALOG, node);
    String uuid = JsonUtil.getString(UUID, node);
    switch (type) {
      case "table":
        return new GenericSourceTableState(
            identifier,
            catalog,
            uuid,
            JsonUtil.getLong(SNAPSHOT_ID, node),
            JsonUtil.getStringOrNull(REF, node));

      case "view":
        return new GenericSourceViewState(
            identifier, catalog, uuid, JsonUtil.getInt(VERSION_ID, node));

      default:
        throw new IllegalArgumentException("Unsupported source state type: " + type);
    }
  }

  private static void writeIdentifier(TableIdentifier identifier, JsonGenerator generator)
      throws IOException {
    generator.writeArrayFieldStart(NAMESPACE);
    for (String level : identifier.namespace().levels()) {
      generator.writeString(level);
    }

    generator.writeEndArray();
    generator.writeStringField(NAME, identifier.name());
  }
}
