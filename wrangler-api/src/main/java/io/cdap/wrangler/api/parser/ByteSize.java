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
  * Token representing a byte size like 10KB, 1MB, etc.
  */
 public class ByteSize implements Token {
   private final String rawValue;
   private final long bytes;
 
   public ByteSize(String rawValue) {
     this.rawValue = rawValue;
     this.bytes = parse(rawValue.trim().toUpperCase());
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
       case "B": return (long) number;
       case "KB": return (long) (number * 1024);
       case "MB": return (long) (number * 1024 * 1024);
       case "GB": return (long) (number * 1024 * 1024 * 1024);
       case "TB": return (long) (number * 1024L * 1024L * 1024L * 1024L);
       default: throw new IllegalArgumentException("Unknown byte unit: " + unit);
     }
   }
 
   public long getBytes() {
     return bytes;
   }
 
   @Override
   public Object value() {
     return rawValue;
   }
 
   @Override
   public TokenType type() {
     return TokenType.BYTE_SIZE;
   }
 
   @Override
   public JsonElement toJson() {
     JsonObject obj = new JsonObject();
     obj.addProperty("type", "BYTE_SIZE");
     obj.addProperty("raw", rawValue);
     obj.addProperty("bytes", bytes);
     return obj;
   }




 }

