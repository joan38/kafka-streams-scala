/**
  * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
  * Copyright 2017-2018 Alexis Seigneurin.
  */
package com.lightbend.kafka.scala.streams

import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.common.utils.Bytes
import ImplicitConversions._
import FunctionConversions._

/**
  * Wraps the Java class KGroupedStream and delegates method calls to the underlying Java object.
  */
class KGroupedStreamS[K, V](inner: KGroupedStream[K, V]) {

  def count(): KTableS[K, Long] = inner.count().asInstanceOf[KTable[K, Long]]

  def count(materialized: Materialized[K, Long, KeyValueStore[Bytes, Array[Byte]]]): KTableS[K, Long] =
    inner.count(materialized)

  def reduce(reducer: (V, V) => V): KTableS[K, V] =
    inner.reduce((v1, v2) => reducer(v1, v2))

  def reduce(reducer: (V, V) => V, materialized: Materialized[K, V, KeyValueStore[Bytes, Array[Byte]]]): KTableS[K, V] =
    // need this explicit asReducer for Scala 2.11 or else the SAM conversion doesn't take place
    // works perfectly with Scala 2.12 though
    inner.reduce(((v1: V, v2: V) => reducer(v1, v2)).asReducer, materialized)

  def aggregate[VR](initializer: () => VR, aggregator: (K, V, VR) => VR): KTableS[K, VR] =
    inner.aggregate(initializer.asInitializer, aggregator.asAggregator)

  def aggregate[VR](initializer: () => VR,
                    aggregator: (K, V, VR) => VR,
                    materialized: Materialized[K, VR, KeyValueStore[Bytes, Array[Byte]]]): KTableS[K, VR] =
    inner.aggregate(initializer.asInitializer, aggregator.asAggregator, materialized)

  def windowedBy(windows: SessionWindows): SessionWindowedKStreamS[K, V] =
    inner.windowedBy(windows)

  def windowedBy[W <: Window](windows: Windows[W]): TimeWindowedKStreamS[K, V] =
    inner.windowedBy(windows)
}
