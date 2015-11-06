package cn.changhong.ancare.util

import com.flashbird.http.util.{SqlProvider, Tools}
import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.request.RequestUtils

/**
 * Created by yangguo on 15/10/28.
 */
object AncareTools {
  def multiPartFormRequestDecoder(request:Request):Map[String,String]={
    val multiParams=RequestUtils.multiParams(request)
    multiParams.map { kv =>
      val values = kv._2
      val value: String =
        if (!values.isFormField) {
          Tools.shortFileStorage(values.data, values.filename) match {
            case Some(url) => url
            case None => ""
          }
        } else {
          new String(values.data)
        }
      (kv._1 -> value)
    }
  }
  def insert(kvs:Map[String,String],tableName:String)={
    val keys=kvs.map(_._1).reduce(_+","+_)
    val values=kvs.map("'"+_._2+"'").reduce(_+","+_)
    val sql=s"insert into $tableName($keys) values($values)"
    SqlProvider.noTransactionExec[Int](sql)
  }
  def update(kvs:Map[String,String],tableName:String,where:String)={
    val settingStr=kvs.map(kv=>s"${kv._1}='${kv._2}'").reduce(_+","+_)
    val updateSql=s"update $tableName set $settingStr where $where"
    SqlProvider.noTransactionExec[Int](updateSql)
  }
}

