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

/** The state of a source table used to refresh a materialized view. */
public interface SourceTableState extends SourceState {

  /** Return {@link Type#TABLE}. */
  @Override
  default Type type() {
    return Type.TABLE;
  }

  /** Return the snapshot ID that was read during the refresh. */
  long snapshotId();

  /**
   * Return the source table branch.
   *
   * <p>When null, the source table's main branch was used.
   */
  default String ref() {
    return null;
  }
}
