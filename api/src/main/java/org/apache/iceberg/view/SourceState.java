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

import org.apache.iceberg.catalog.TableIdentifier;

/** The state of an Iceberg table or view used to refresh a materialized view. */
public interface SourceState {

  /** The supported source state types. */
  enum Type {
    /** A source table or a source materialized view's storage table. */
    TABLE,

    /** A source view, including a materialized view treated as a view. */
    VIEW
  }

  /** Return the source state type. */
  Type type();

  /** Return the source table or view identifier. */
  TableIdentifier identifier();

  /**
   * Return the source catalog.
   *
   * <p>When null, the source is in the materialized view's catalog.
   */
  default String catalog() {
    return null;
  }

  /** Return the UUID of the source table or view. */
  String uuid();
}
