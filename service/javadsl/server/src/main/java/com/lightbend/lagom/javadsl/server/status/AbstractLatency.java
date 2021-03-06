/*
 * Copyright (C) 2016-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.lagom.javadsl.server.status;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;

// NOTE: The `Latency.java` is generated by Immutables, but since Scaladoc is
// not running the annotation processor we had to embed the generated code.
// Do not edit `Latency.java` manually, but re-generate it and copy the
// generated source. Remove the comment @Value.Immutable at this class to
// re-generate.

/** Latency distribution for Exponentially Decaying Reservoir. Time unit is microseconds. */
// @Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Latency.class)
public interface AbstractLatency {

  /** @return the median value in the distribution */
  double getMedian();

  /** @return the value at the 98th percentile */
  double getPercentile98th();

  /** @return the value at the 99th percentile */
  double getPercentile99th();

  /** @return the value at the 99.9th percentile */
  double getPercentile999th();

  /** @return the arithmetic mean */
  double getMean();

  /** @return the lowest value */
  long getMin();

  /** @return the highest value */
  long getMax();
}
