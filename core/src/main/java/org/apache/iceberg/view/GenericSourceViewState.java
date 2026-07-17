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

import java.util.Objects;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.relocated.com.google.common.base.MoreObjects;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;

/** An immutable {@link SourceViewState} implementation. */
public class GenericSourceViewState implements SourceViewState {
  private final TableIdentifier identifier;
  private final String catalog;
  private final String uuid;
  private final int versionId;

  /**
   * Creates a source view state.
   *
   * @param identifier the source view identifier
   * @param catalog the source catalog, or null for the materialized view's catalog
   * @param uuid the source view UUID
   * @param versionId the source view version ID that was read
   */
  public GenericSourceViewState(
      TableIdentifier identifier, String catalog, String uuid, int versionId) {
    this.identifier = Preconditions.checkNotNull(identifier, "Identifier cannot be null");
    this.catalog = catalog;
    this.uuid = Preconditions.checkNotNull(uuid, "UUID cannot be null");
    this.versionId = versionId;
  }

  @Override
  public TableIdentifier identifier() {
    return identifier;
  }

  @Override
  public String catalog() {
    return catalog;
  }

  @Override
  public String uuid() {
    return uuid;
  }

  @Override
  public int versionId() {
    return versionId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof SourceViewState)) {
      return false;
    }

    SourceViewState that = (SourceViewState) other;
    return versionId == that.versionId()
        && identifier.equals(that.identifier())
        && Objects.equals(catalog, that.catalog())
        && uuid.equals(that.uuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, catalog, uuid, versionId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("identifier", identifier)
        .add("catalog", catalog)
        .add("uuid", uuid)
        .add("versionId", versionId)
        .toString();
  }
}
