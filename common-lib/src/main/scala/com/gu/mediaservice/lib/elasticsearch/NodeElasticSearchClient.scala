package com.gu.mediaservice.lib.elasticsearch

import org.elasticsearch.node.NodeBuilder._


case class NodeElasticSearchClient(clusterName: String) extends ElasticSearchClientStrategy {

  private val node = nodeBuilder().clusterName(clusterName).local(true).node

  val client = node.client


  def close = node.close
}
