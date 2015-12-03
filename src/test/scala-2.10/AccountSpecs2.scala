
import java.io.File

import cn.reflesh.R
import org.apache.commons.fileupload.FileItem
import org.specs2.mutable.{BeforeAfter, Specification}
import org.specs2.specification.BeforeAfterEach

/**
 * Created by yangguo on 15/12/2.
 */
class AccountSpecs2 extends Specification with BeforeAfterEach{


  "login is Ok" >> {
    "println "
    1===1
  }
  "get User Info" >> {

    1===1
  }
  "dynamic instance class">>{
    val _class= Class.forName("java.io.File")
    val obj=_class.newInstance()
    if(obj.isInstanceOf[File]){
      obj.asInstanceOf[File].getName
      true
    }else{
      false
    }

  }

  override protected def before: Any = println("before")

  override protected def after: Any = println("after")
}
