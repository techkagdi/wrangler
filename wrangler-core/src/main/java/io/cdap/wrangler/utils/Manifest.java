/*
 *  Copyright © 2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */


package io.cdap.wrangler.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Contains the Schemas Manifest for standard schemas.
 */
public class Manifest {
   /**
   * Represents a standard schema format with description.
   */
  public static class Standard {
    private final String format;
    private final String description; // second parameter

    public Standard(String format, String description) {
      this.format = format;
      this.description = description;
    }

    public String getFormat() {
      return format;
    }

    public String getDescription() {
      return description;
    }
  }

  public Map<String, Standard> getStandards() {
    Map<String, Standard> map = new HashMap<>();
    map.put("dummy", new Standard("json", "dummy standard"));
    return map;
  }
}

