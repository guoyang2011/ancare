import com.flashbird.http.util.JsonParser

/**
 * Created by yangguo on 15/10/12.
 */

object JsonParserTest {
  case class InlineBean(lng:Double,lat:Double)
  case class Bean(account:String,passwd:String,location:Option[InlineBean])
  case class LoginInfo(account:String,passwd:String)
  def main(args: Array[String]) {
//    val location=InlineBean(0.999,0.8812)
//    val bean=Bean("username","passwd",location)
    val loginInfo=LoginInfo("username","passwd")
    val jsonString=JsonParser.objectToJsonStringParser(loginInfo)
    println(jsonString)
    val map=JsonParser.jsonStringToObjectParser[Bean](jsonString)
    println(new sun.misc.BASE64Encoder().encode("helloword".getBytes()))
  }
}
