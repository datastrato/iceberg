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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.apache.iceberg.catalog.TableIdentifier;
import org.junit.jupiter.api.Test;

public class TestRefreshStateParser {

  @Test
  public void roundTripSourceStates() {
    SourceTableState tableState =
        new GenericSourceTableState(
            TableIdentifier.of("analytics", "events"),
            "prod",
            "d4a10b5c-1e8a-4b72-9d67-3f4a8c9e1b2d",
            6148331192489823102L,
            "audit");
    SourceViewState viewState =
        new GenericSourceViewState(
            TableIdentifier.of("analytics", "event_view"),
            null,
            "6cebed28-22c5-411c-b455-5123d40c1997",
            3);
    RefreshState refreshState =
        new GenericRefreshState(7, 1573518435000L, List.of(tableState, viewState));

    String expectedJson =
        "{\"view-version-id\":7,\"refresh-start-timestamp-ms\":1573518435000,"
            + "\"source-states\":[{\"type\":\"table\",\"namespace\":[\"analytics\"],"
            + "\"name\":\"events\",\"catalog\":\"prod\","
            + "\"uuid\":\"d4a10b5c-1e8a-4b72-9d67-3f4a8c9e1b2d\","
            + "\"snapshot-id\":6148331192489823102,\"ref\":\"audit\"},"
            + "{\"type\":\"view\",\"namespace\":[\"analytics\"],\"name\":\"event_view\","
            + "\"uuid\":\"6cebed28-22c5-411c-b455-5123d40c1997\",\"version-id\":3}]}";

    assertThat(RefreshStateParser.toJson(refreshState)).isEqualTo(expectedJson);
    assertThat(RefreshStateParser.fromJson(expectedJson)).isEqualTo(refreshState);
  }

  @Test
  public void emptySourceStates() {
    RefreshState refreshState = new GenericRefreshState(1, 1573518435000L, List.of());

    String json = RefreshStateParser.toJson(refreshState);
    assertThat(json)
        .isEqualTo(
            "{\"view-version-id\":1,\"refresh-start-timestamp-ms\":1573518435000,"
                + "\"source-states\":[]}");
    assertThat(RefreshStateParser.fromJson(json)).isEqualTo(refreshState);
  }

  @Test
  public void optionalFieldsMayBeNullOrMissing() {
    String json =
        "{\"view-version-id\":1,\"refresh-start-timestamp-ms\":23,\"source-states\":["
            + "{\"type\":\"TABLE\",\"namespace\":[\"ns\"],\"name\":\"table\","
            + "\"catalog\":null,\"uuid\":\"table-uuid\",\"snapshot-id\":34,\"ref\":null},"
            + "{\"type\":\"VIEW\",\"namespace\":[\"ns\"],\"name\":\"view\","
            + "\"uuid\":\"view-uuid\",\"version-id\":5}]}";

    RefreshState parsed = RefreshStateParser.fromJson(json);
    assertThat(parsed.sourceStates()).hasSize(2);
    assertThat(parsed.sourceStates().get(0)).isInstanceOf(SourceTableState.class);
    SourceTableState tableState = (SourceTableState) parsed.sourceStates().get(0);
    assertThat(tableState.catalog()).isNull();
    assertThat(tableState.ref()).isNull();
    assertThat(parsed.sourceStates().get(1)).isInstanceOf(SourceViewState.class);
    assertThat(parsed.sourceStates().get(1).catalog()).isNull();
  }

  @Test
  public void nullAndInvalidRefreshState() {
    assertThatThrownBy(() -> RefreshStateParser.toJson(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot serialize null refresh state");
    assertThatThrownBy(() -> RefreshStateParser.fromJson((String) null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse refresh state from null string");
    assertThatThrownBy(() -> RefreshStateParser.fromJson((JsonNode) null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse refresh state from null object");
    assertThatThrownBy(() -> RefreshStateParser.fromJson("[]"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse refresh state from a non-object: []");
  }

  @Test
  public void invalidSourceStates() {
    String prefix = "{\"view-version-id\":1,\"refresh-start-timestamp-ms\":23,\"source-states\":";
    assertThatThrownBy(() -> RefreshStateParser.fromJson(prefix + "{}}"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot parse source states from non-array: {}");

    String unknown =
        prefix
            + "[{\"type\":\"stream\",\"namespace\":[\"ns\"],\"name\":\"source\","
            + "\"uuid\":\"source-uuid\"}]}";
    assertThatThrownBy(() -> RefreshStateParser.fromJson(unknown))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unsupported source state type: stream");

    String malformed =
        prefix
            + "[{\"type\":\"table\",\"namespace\":\"ns\",\"name\":\"source\","
            + "\"uuid\":\"source-uuid\",\"snapshot-id\":1}]}";
    assertThatThrownBy(() -> RefreshStateParser.fromJson(malformed))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot parse JSON array from non-array value: namespace");
  }
}
