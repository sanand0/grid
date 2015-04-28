package com.gu.mediaservice.lib.elasticsearch

import org.elasticsearch.client.Client

trait ElasticSearchClientStrategy {
  val client: Client
}

