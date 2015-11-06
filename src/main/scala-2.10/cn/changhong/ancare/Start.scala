package cn.changhong.ancare

import com.flashbird.http.BuilderFlashBirdServer
import com.flashbird.http.util.FlashBirdConfig

/**
 * Created by yangguo on 15/11/2.
 */
object Start extends BuilderFlashBirdServer{
  override def builderControllers(): Unit = {
    new AccountService
    new AncareFamilyService
    new AncareService
  }

  override def loadConfig(path: String): Unit = FlashBirdConfig(path)

  def main(args: Array[String]) {
    require(args!=null&&args.size>0,"Please Enter Ancare Config File!")
    start(args(0))
  }
}
