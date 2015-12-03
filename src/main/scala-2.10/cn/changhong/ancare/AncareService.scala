package cn.changhong.ancare

import cn.changhong.ancare.util.AncareTools
import com.flashbird.http.framework.{DefaultResponseCode, DefaultResponseContent, Controller}
import com.flashbird.http.framework.request.DynamicPathParamsRequest
import com.flashbird.http.util.{ FlashBirdConfig, SqlProvider}
import com.twitter.finagle.httpx.Request
import com.flashbird.http.util.UserDefinedGetResult._
import com.twitter.util.Future

/**
 * Created by yangguo on 15/10/28.
 */
class AncareService extends Controller(Some("/my")) {


  /**
   * 1.
   * get test data by user id order by creation
   * current user must in one family
   * url template:/d/:userId?columns=${columns}&start=${start}&max=${max}
   */
  get("/hsounds/:userId") { request: DynamicPathParamsRequest[String] =>
    val userId = request.getDynamicPathParam("userId")
    val columns = request.getUrlParam("columns", "hsourdsFile,hrate,diseaseType,heartIndex")
    val start = try {
      request.getUrlParam("start", "0")(0).toInt
    } catch {
      case ex: Throwable => 0
    }
    val max = try {
      request.getUrlParam("max", "20")(0).toInt
    } catch {
      case ex: Throwable => 20
    }
    val currentUserId=request.underlying.headerMap.getOrElse(FlashBirdConfig.key_access_token_info,"-1")
    if(userId.equals(currentUserId)||familyMemberCheck(currentUserId,userId)){
      val sql = s"select $columns from tb_hsounds where user_id='$userId' order by creation desc limit ${start*max},$max "
      SqlProvider.noTransactionExec[Map[String,AnyRef]](sql)
    }else{
      DefaultResponseContent(DefaultResponseCode.no_right._1,DefaultResponseCode.no_right._2)
    }
  }

  override def defaultIsNeedAuth: Boolean = true

  private def familyMemberCheck(currentUserId:String,otherUserId:String):Boolean={
    val sql=s"select count(0) from (select user_id from tb_familymember where family_id in (SELECT DISTINCT family_id from tb_familymember where user_id='${currentUserId}')) as a where a.user_id='${otherUserId}' "
    var res:Boolean=false
    val counts=try{
      SqlProvider.noTransactionExec[Int](sql) match{
        case item::list=>if(item>0) res=true
      }
    }catch{
      case ex:Throwable=>
    }
    res
  }

  /**
   * 2.
   * add user new test data
   *user_id,hrate,diseaseType,heartIndex,hsourdsFile(File)
   */
  post("/hsounds/add"){request:Request=>
    var kvs:Map[String,String]=AncareTools.multiPartFormRequestDecoder(request)
    kvs+=("creation"-> (System.currentTimeMillis()+""))//++kvs
    AncareTools.insert(kvs,"tb_hsounds")
  }

}
