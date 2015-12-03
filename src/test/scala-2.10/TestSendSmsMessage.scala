import java.util

import com.flashbird.http.util.FlashBirdConfig

/**
 * Created by yangguo on 15/11/18.
 */
class Value{
  private var _str:String="value"
  def str:Option[String]=Some(_str)
  def str_=(value:String){_str=value}
}
object TestSendSmsMessage {
  def main(args: Array[String]) {
    FlashBirdConfig(new util.HashMap[String,AnyRef]())
    println(SMSUtil("ch job is fail.","15828125740"))
    Thread.sleep(3000)
  }

}
