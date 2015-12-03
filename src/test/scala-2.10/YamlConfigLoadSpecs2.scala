import java.io.File

import com.flashbird.http.util.FlashBirdConfig
import org.specs2.mutable.{BeforeAfter, Specification}

/**
 * Created by yangguo on 15/12/2.
 */
class YamlConfigLoadSpecs2  extends Specification with BeforeAfter{
  val testYamlConfigFile=new File("/Users/yangguo/hadoop/finatra/finatra/examples/FlashbirdTest/src/main/resources","ancare.yaml")
  FlashBirdConfig(testYamlConfigFile.getAbsolutePath)
  override def after: Any = println("after")

  override def before: Any = println("before")
  "Mysql server database name">>{
    FlashBirdConfig.getMysqlServerDatabaseName()==="ancare"
  }
  "com.ancare.storage.local.dir">>{
    FlashBirdConfig.getValue("com.ancare.storage.local.dir","default")==="/usr/local/share/nginx/storage/"
  }


}
