/*
 * Copyright © 2017-2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 
 /**
  * Token representing a time duration like 100ms, 2.5s, 1min, etc.
  */
 public class TimeDuration implements Token {
   private final String rawValue;
   private final long milliseconds;
 
   public TimeDuration(String rawValue) {
     this.rawValue = rawValue;
     this.milliseconds = parse(rawValue.trim().toLowerCase());
   }
 
   private long parse(String input) {
     double number;
     String unit;
     int i = 0;
 
     while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
       i++;
     }
 
     number = Double.parseDouble(input.substring(0, i));
     unit = input.substring(i);
 
     switch (unit) {
       case "ms": return (long) number;
       case "s":
       case "sec": return (long) (number * 1000);
       case "m":
       case "min": return (long) (number * 60 * 1000);
       case "h": return (long) (number * 60 * 60 * 1000);
       default: throw new IllegalArgumentException("Unknown time unit: " + unit);
     }
   }
 
   public long getMilliseconds() {
     return milliseconds;
   }
 
   @Override
   public Object value() {
     return rawValue;
   }
 
   @Override
   public TokenType type() {
     return TokenType.TIME_DURATION;
   }
 
   @Override
   public JsonElement toJson() {
     JsonObject obj = new JsonObject();
     obj.addProperty("type", "TIME_DURATION");
     obj.addProperty("raw", rawValue);
     obj.addProperty("milliseconds", milliseconds);
     return obj;
   }
 }

