package com.gu.mediaservice.lib.config

class Services(domainRoot: String, ssl: Boolean) {
  // FIXME: do this via new config properties!
  val DomainExtractor = """^([-a-z]+)\.(.*)""".r
  val (appName, parentDomain) = domainRoot match {
    case DomainExtractor(a, b) => (a, b)
  }

  val kahunaHost: String   = domainRoot
  val apiHost: String      = s"api.$domainRoot"
  val loaderHost: String   = s"loader.$domainRoot"
  val cropperHost: String  = s"cropper.$domainRoot"
  val metadataHost: String = s"$appName-metadata.$parentDomain"
  val imgopsHost: String   = s"$appName-imgops.$parentDomain"
  val usageHost: String    = s"$appName-usage.$parentDomain"
  val collectionsHost: String = s"$appName-collections.$parentDomain"
  val authHost: String     = s"$appName-auth.$parentDomain"


  val kahunaBaseUri      = baseUri(kahunaHost)
  val apiBaseUri         = baseUri(apiHost)
  val loaderBaseUri      = baseUri(loaderHost)
  val cropperBaseUri     = baseUri(cropperHost)
  val metadataBaseUri    = baseUri(metadataHost)
  val imgopsBaseUri      = baseUri(imgopsHost)
  val usageBaseUri       = baseUri(usageHost)
  val collectionsBaseUri = baseUri(collectionsHost)
  val authBaseUri        = baseUri(authHost)


  val loginUriTemplate = s"$authBaseUri/login{?redirectUri}"

  def baseUri(host: String) = {
    val protocol = if (ssl) "https" else "http"
    s"$protocol://$host"
  }
}
