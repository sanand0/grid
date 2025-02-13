package lib

import java.util.concurrent.atomic.AtomicBoolean
import com.gu.mediaservice.lib.config.{CommonPlayAppConfig, Properties}
import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentials}


object Config extends CommonPlayAppConfig {

  val appName = "ftp-watcher"

  val properties: Map[String, String] =
    Properties.fromPath("/etc/gu/ftp-watcher.properties") ++ sys.props

  val ftpHost: String = properties("ftp.host")
  val ftpPort: Int = properties.get("ftp.port").fold(21)(_.toInt)
  val ftpUser: String = properties("ftp.user")
  val ftpPassword: String = properties("ftp.password")

  // As we move to using the S3 Watcher, we'll need to exclude paths
  val possibleFtpPaths: Set[String] =
    Set("aapimages", "ap", "email", "epa", "getty", "pa", "priorityftp", "reuters", "stingray")
  val excludedFtpPaths: Set[String] = Set("stingray", "pa", "getty")
  val ftpPaths: List[String] = (possibleFtpPaths -- excludedFtpPaths).toList

  val imageLoaderUri: String = properties("loader.uri")

  val active: AtomicBoolean =
    new AtomicBoolean(properties.get("ftp.active").exists(_.toBoolean))

  def status: String = if (isActive) "active" else "passive"

  def isActive: Boolean = active.get

  val metricsAwsCredentials: AWSCredentials =
    new BasicAWSCredentials(properties("metrics.aws.id"), properties("metrics.aws.secret"))

  val mediaApiKey = properties("auth.key.ftpwatcher")

}
