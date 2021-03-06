/*
 * Copyright (C) 2016-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.lagom.internal.broker.kafka

import java.net.URI

import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterShardingSettings
import akka.pattern.BackoffOpts
import akka.pattern.BackoffSupervisor
import akka.persistence.query.Offset
import akka.stream.Materializer
import akka.stream.scaladsl._
import com.lightbend.lagom.internal.persistence.cluster.ClusterDistribution
import com.lightbend.lagom.internal.persistence.cluster.ClusterDistributionSettings
import com.lightbend.lagom.spi.persistence.OffsetStore
import org.apache.kafka.common.serialization.Serializer

import scala.collection.immutable
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
 * A Producer for publishing messages in Kafka using the Alpakka Kafka API.
 */
private[lagom] object Producer {

  def startTaggedOffsetProducer[Message](
      system: ActorSystem,
      tags: immutable.Seq[String],
      kafkaConfig: KafkaConfig,
      locateService: String => Future[Seq[URI]],
      topicId: String,
      eventStreamFactory: (String, Offset) => Source[(Message, Offset), _],
      partitionKeyStrategy: Option[Message => String],
      serializer: Serializer[Message],
      offsetStore: OffsetStore
  )(implicit mat: Materializer, ec: ExecutionContext): Unit = {

    val projectionName = s"kafkaProducer-$topicId"

    val producerConfig = ProducerConfig(system.settings.config)
    val topicProducerProps = TopicProducerActor.props(
      kafkaConfig,
      producerConfig,
      locateService,
      topicId,
      eventStreamFactory,
      partitionKeyStrategy,
      serializer,
      offsetStore
    )

    val entityIds = tags.toSet

    val clusterShardingSettings = ClusterShardingSettings(system).withRole(producerConfig.role)

    ClusterDistribution(system).start(
      projectionName,
      topicProducerProps,
      entityIds,
      ClusterDistributionSettings(system).copy(clusterShardingSettings = clusterShardingSettings)
    )
  }

}
