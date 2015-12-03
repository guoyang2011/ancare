package cn.changhong.ancare

import cn.changhong.ancare.util.AncareTools
import com.flashbird.http.framework.Controller
import com.flashbird.http.framework.request.DynamicPathParamsRequest
import com.flashbird.http.util.SqlProvider
import com.twitter.finagle.httpx.Request
import com.flashbird.http.util.UserDefinedGetResult._

/**
 * Created by yangguo on 15/10/28.
 */
class AncareFamilyService extends Controller(Some("/family")){

  /**
   *  create Family
   */
  post("/addfamily"){request:Request=>
    var map:Map[String,String]=AncareTools.multiPartFormRequestDecoder(request)
    map+=("creation" -> (System.currentTimeMillis()+""))
    AncareTools.insert(map,"tb_family")
  }
  //add Family Member
  post("/addmember"){request:Request=>
    var map:Map[String,String]=AncareTools.multiPartFormRequestDecoder(request)
    map+=("creation"->(System.currentTimeMillis()+""))
    AncareTools.insert(map,"tb_familymember")
  }
  //get Familys By User Id
  get("/getfamilysbyuserid/:userId") { request: DynamicPathParamsRequest[String] =>
    val userId = request.getDynamicPathParam("userId", "-1")
    val selectSql = s"select * from tb_family as a inner join (select family_id from tb_familymember where user_id='$userId') as b on a.id=b.family_id"
    SqlProvider.noTransactionExec[Map[String, AnyRef]](selectSql)
  }
  //get family members by family id
  get("/getmemberbyfid/:fid") { request: DynamicPathParamsRequest[String] =>
    val fid = request.getDynamicPathParam("fid", "-1")
    val selectSql = s"select * from tb_familymember where family_id='$fid'"
    SqlProvider.noTransactionExec[Map[String, AnyRef]](selectSql)
  }

  //get Family By Family Id
  get("/getfamilybyfid/:fid") { request: DynamicPathParamsRequest[String] =>
    val fid = request.getDynamicPathParam("fid", "-1")
    val selectSql = s"select * from tb_family where id='$fid'"
    SqlProvider.noTransactionExec[Map[String, AnyRef]](selectSql)
  }
  //modify Family Attribute
  put("/modifyfamily"){request:Request=>
    def filterKey(key:String):Boolean={
      key!="user_id"&&key!="creation"
    }
    val map=AncareTools.multiPartFormRequestDecoder(request).filter(kv=>filterKey(kv._1))
    AncareTools.update(map,"tb_family",s"id='${map.getOrElse("id","-1")}'")
  }

  override def defaultIsNeedAuth: Boolean = true
}
