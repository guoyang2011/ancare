import java.net.URLEncoder
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.flashbird.http.util.{ JsonParser, Tools}
import com.twitter.finagle.Httpx
import com.twitter.finagle.httpx.{Status, Method, Request}
import com.twitter.util.Duration
import sun.misc.BASE64Encoder

/**
 * Created by yangguo on 15/12/2.
 */
case class SmsResponse(err_msg:String,err_code:Int,state:String)
class SMSAuthInfo(val apiKey:String="f73ed6801e2211e5a6f4342387b8b85a",val secretKey:String="f90ae3f01e2211e5ba08342387b8b85a"){
  val oauthMethod="HMAC-SHA1"
  val oauthVersion="1.0"
  val host="api.chiq-cloud.com"
  val timestamp=System.currentTimeMillis()
  val nonce=Tools.md5(System.nanoTime().toString)
  val  secretString=s"oauth_consumer_key=$apiKey&oauth_nonce=$nonce&oauth_signature_method=$oauthMethod&oauth_timestamp=$timestamp&oauth_version=$oauthVersion"
  def generatorOAuthString()={
    val mac=Mac.getInstance("HmacSHA1")
    val secretKeySpec=new SecretKeySpec(secretKey.getBytes,"HmacSHA1")
    mac.init(secretKeySpec)
    val byteHmac=mac.doFinal(secretString.getBytes)
    val encoder=new BASE64Encoder
    val str=encoder.encode(byteHmac)
    URLEncoder.encode(str,"utf-8")
  }
}
object SMSUtil {
  val min=100000
  val max=1000000
  def generatorVerifyCode={
    val random=new Random()
    random.nextInt(max)%(max-min+1)+min
  }
  val smsService = Httpx.newService("182.140.231.224:80")
  def apply(msg:String,phone:String):Boolean= {
    var result=false
    val authInfo = new SMSAuthInfo
    val request = Request(Method.Post, "/v1/sms")
    request.host = "api.chiq-cloud.com"
    request.headerMap.set("Authorization",
      s"""OAuth realm="http://api.chiq-cloud.com/v1/sms/",oauth_consumer_key="${authInfo.apiKey}",oauth_signature="${authInfo.generatorOAuthString()}",oauth_signature_method="HMAC-SHA1",oauth_nonce="${authInfo.nonce}",oauth_timestamp="${authInfo.timestamp}",oauth_version="1.0"""")
    request.headerMap.set("Content-type", "application/json")
    request.headerMap.set("User_Agent", "imgfornote")
    val content = Map("checkType" -> "", "content" -> msg, "userNumber" -> phone)
    request.setContentString(JsonParser.objectToJsonStringParser(content))
    println(request.toString() + JsonParser.objectToJsonStringParser(content))
    try {
      val response = smsService(request)(Duration(2, TimeUnit.SECONDS))
      println(response.getContentString())
      if (response.status == Status.Ok) {

        val content = JsonParser.jsonStringToObjectParser[SmsResponse](response.getContentString())
        if (content.err_code == 0) {
          result = true
        }
      }
    }catch{
      case ex:Throwable=>
    }
    result
  }
}