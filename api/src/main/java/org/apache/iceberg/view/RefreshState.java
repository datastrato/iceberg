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

/** The state of a materialized view refresh recorded in a storage table snapshot summary. */
public interface RefreshState {

  /** Return the materialized view version that was refreshed. */
  int viewVersionId();

  /** Return the time when the refresh started, in milliseconds from the epoch. */
  long refreshStartTimestampMillis();

  /**
   * Return the states of source tables and views that were used during the refresh.
   *
   * <p>The returned list may be empty when source state is not tracked.
   */
  List<SourceState> sourceStates();
}
