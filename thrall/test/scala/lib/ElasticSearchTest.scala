package scala.lib

import com.gu.mediaservice.lib.elasticsearch.NodeElasticSearchClient
import lib.ElasticSearchTrait
import org.scalatest.{FunSpec, Matchers, BeforeAndAfter}
import play.api.libs.json.Json

import play.api.libs.concurrent.Execution.Implicits._

object ElasticSearchX extends NodeElasticSearchClient("imageTest") with ElasticSearchTrait


class ElasticSearchTest extends FunSpec with Matchers with BeforeAndAfter {

  var client: NodeElasticSearchClient = null

  before {
    client = NodeElasticSearchClient("imageTest")
  }

  after {
    client.close
  }


  it("should do X") {
    ElasticSearchX.indexImage("hello", Json.obj("eek" -> "foo"))
  }

}
