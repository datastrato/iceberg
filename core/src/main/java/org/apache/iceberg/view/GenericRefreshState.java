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

import java.util.List;
import java.util.Objects;
import org.apache.iceberg.relocated.com.google.common.base.MoreObjects;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;
import org.apache.iceberg.relocated.com.google.common.collect.ImmutableList;

/** An immutable {@link RefreshState} implementation. */
public class GenericRefreshState implements RefreshState {
  private final int viewVersionId;
  private final long refreshStartTimestampMillis;
  private final List<SourceState> sourceStates;

  /**
   * Creates a refresh state.
   *
   * @param viewVersionId the materialized view version that was refreshed
   * @param refreshStartTimestampMillis the time when the refresh started
   * @param sourceStates source table and view states; may be empty
   */
  public GenericRefreshState(
      int viewVersionId, long refreshStartTimestampMillis, List<SourceState> sourceStates) {
    Preconditions.checkNotNull(sourceStates, "Source states cannot be null");
    this.viewVersionId = viewVersionId;
    this.refreshStartTimestampMillis = refreshStartTimestampMillis;
    this.sourceStates = ImmutableList.copyOf(sourceStates);
  }

  @Override
  public int viewVersionId() {
    return viewVersionId;
  }

  @Override
  public long refreshStartTimestampMillis() {
    return refreshStartTimestampMillis;
  }

  @Override
  public List<SourceState> sourceStates() {
    return sourceStates;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof RefreshState)) {
      return false;
    }

    RefreshState that = (RefreshState) other;
    return viewVersionId == that.viewVersionId()
        && refreshStartTimestampMillis == that.refreshStartTimestampMillis()
        && sourceStates.equals(that.sourceStates());
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewVersionId, refreshStartTimestampMillis, sourceStates);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("viewVersionId", viewVersionId)
        .add("refreshStartTimestampMillis", refreshStartTimestampMillis)
        .add("sourceStates", sourceStates)
        .toString();
  }
}
