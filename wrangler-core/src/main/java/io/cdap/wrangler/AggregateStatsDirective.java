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
package io.cdap.wrangler;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Identifier;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Aggregates byte size and time duration across all rows and outputs totals in desired units.
 */
@Plugin(type = Directive.TYPE)
@Name(AggregateStatsDirective.NAME)
@Categories(categories = {"aggregate"})
@Description("Aggregates byte size and duration columns across rows, and outputs totals in MB and seconds.")
public class AggregateStatsDirective implements Directive, Executor<List<Row>, List<Row>> {
  public static final String NAME = "aggregate-stats";

  private String byteInputCol;
  private String timeInputCol;
  private String byteOutputCol;
  private String timeOutputCol;

  private static final String TOTAL_BYTES_KEY = "totalBytes";
  private static final String TOTAL_DURATION_KEY = "totalDuration";

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("byteInputCol", TokenType.COLUMN_NAME);
    builder.define("timeInputCol", TokenType.COLUMN_NAME);
    builder.define("byteOutputCol", TokenType.IDENTIFIER);
    builder.define("timeOutputCol", TokenType.IDENTIFIER);
    return builder.build();
  }

  @Override
  public void initialize(Arguments arguments) throws DirectiveParseException {
    this.byteInputCol = ((ColumnName) arguments.value("byteInputCol")).value();
    this.timeInputCol = ((ColumnName) arguments.value("timeInputCol")).value();
    this.byteOutputCol = ((Identifier) arguments.value("byteOutputCol")).value();
    this.timeOutputCol = ((Identifier) arguments.value("timeOutputCol")).value();
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    TransientStore store = context.getTransientStore();
    Long totalBytes = Optional.ofNullable((Long) store.get(TOTAL_BYTES_KEY)).orElse(0L);
    Long totalDuration = Optional.ofNullable((Long) store.get(TOTAL_DURATION_KEY)).orElse(0L);

    for (Row row : rows) {
      Object byteVal = row.getValue(byteInputCol);
      if (byteVal != null) {
        ByteSize byteSize = new ByteSize(byteVal.toString());
        totalBytes += byteSize.getBytes();
      }

      Object timeVal = row.getValue(timeInputCol);
      if (timeVal != null) {
        TimeDuration timeDuration = new TimeDuration(timeVal.toString());
        totalDuration += timeDuration.getMilliseconds();
      }
    }

    store.set(TransientVariableScope.LOCAL, TOTAL_BYTES_KEY, totalBytes);
    store.set(TransientVariableScope.LOCAL, TOTAL_DURATION_KEY, totalDuration);

    if (!rows.isEmpty()) {
      Row result = new Row();
      result.add(byteOutputCol, totalBytes / (1024.0 * 1024.0));
      result.add(timeOutputCol, totalDuration / 1000.0);
      return Collections.singletonList(result);
    }

    return Collections.emptyList();
  }

  @Override
  public void destroy() {
    // No-op
  }
}

