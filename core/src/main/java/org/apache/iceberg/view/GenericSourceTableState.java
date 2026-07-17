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

/** An immutable {@link SourceTableState} implementation. */
public class GenericSourceTableState implements SourceTableState {
  private final TableIdentifier identifier;
  private final String catalog;
  private final String uuid;
  private final long snapshotId;
  private final String ref;

  /**
   * Creates a source table state.
   *
   * @param identifier the source table identifier
   * @param catalog the source catalog, or null for the materialized view's catalog
   * @param uuid the source table UUID
   * @param snapshotId the source snapshot ID that was read
   * @param ref the source branch, or null for the main branch
   */
  public GenericSourceTableState(
      TableIdentifier identifier, String catalog, String uuid, long snapshotId, String ref) {
    this.identifier = Preconditions.checkNotNull(identifier, "Identifier cannot be null");
    this.catalog = catalog;
    this.uuid = Preconditions.checkNotNull(uuid, "UUID cannot be null");
    this.snapshotId = snapshotId;
    this.ref = ref;
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
  public long snapshotId() {
    return snapshotId;
  }

  @Override
  public String ref() {
    return ref;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof SourceTableState)) {
      return false;
    }

    SourceTableState that = (SourceTableState) other;
    return snapshotId == that.snapshotId()
        && identifier.equals(that.identifier())
        && Objects.equals(catalog, that.catalog())
        && uuid.equals(that.uuid())
        && Objects.equals(ref, that.ref());
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, catalog, uuid, snapshotId, ref);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("identifier", identifier)
        .add("catalog", catalog)
        .add("uuid", uuid)
        .add("snapshotId", snapshotId)
        .add("ref", ref)
        .toString();
  }
}
