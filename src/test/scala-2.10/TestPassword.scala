import com.flashbird.http.util.Tools
import com.twitter.util.Future

/**
 * Created by yangguo on 15/11/6.
 */
object TestPassword {
  def main(args: Array[String]) {
    println(Tools.md5("123456"))
    val t = Future.value("")

  }
}
