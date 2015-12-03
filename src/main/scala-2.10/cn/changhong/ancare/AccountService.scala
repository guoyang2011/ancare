package cn.changhong.ancare

import cn.changhong.ancare.util.{AncareTools, SMSUtil}
import com.flashbird.http.framework.request.DynamicPathParamsRequest
import com.flashbird.http.framework.{HttpControllerFilter, DefaultResponseCode, DefaultResponseContent, Controller}
import com.flashbird.http.framework.authorization.{ Authorization}
import com.flashbird.http.util.{RedisProvider, Tools, FlashBirdConfig, SqlProvider}
import Controller._
import com.twitter.finagle.httpx.Request
import com.flashbird.http.util.UserDefinedGetResult._


/**
 * Created by yangguo on 15/10/28.
 */
case class Account(phone:String,password:String)
case class RegisterStep2Request(phone:String,verifyCode:String)
case class RegisterStep3Request(phone:String,verifyCode:String,password:String)
case class ChangePasswordBean(id:String,oldPwd:String,newPwd:String)
case class FeedBack(user_id:String,remark:String,tag:String)
class AccountService extends Controller(Some("/account")){
  override def defaultIsNeedAuth: Boolean = true
  private val prefix_register_key="register"
  private def generatorKey(str:String)=prefix_register_key+"_"+str
  val authProvider:Authorization=FlashBirdConfig.getAuthProvider()
  post("/login",false){account:Account=>
    login((account.phone),(account.password))
  }
  private def login(phone:String,password:String)={
    val sql=s"select id,phone,icon,sex,nick,age,area,job from tb_user where phone='${SqlProvider.checkUnSafeWord(phone)}' and password='${Tools.md5(password)}'"
    try {
      val list=SqlProvider.noTransactionExec[Map[String, AnyRef]](sql)
      if(list!=null&&list.size>0){
        val user=list(0)
        user.get("id") match{
          case Some(id)=> {
            val token=authProvider.creatorToken(id.toString)
            val userFamilySql=s"select * from tb_familymember where user_id='${id}'"
            val familys=SqlProvider.noTransactionExec[Map[String,AnyRef]](userFamilySql)
            val bean=user+("token"->token)+("familyInfos"->familys)
            DefaultResponseContent(DefaultResponseCode.succeed._1,bean)
          }
          case None=>DefaultResponseContent(DefaultResponseCode.db_executor_error._1,DefaultResponseCode.db_executor_error._2)
        }
      }else{
        DefaultResponseContent(DefaultResponseCode.user_not_exit._1,DefaultResponseCode.user_not_exit._2)
      }
    }catch{
      case ex:Throwable=>{
        ex.printStackTrace()
        DefaultResponseContent(DefaultResponseCode.db_executor_error._1,DefaultResponseCode.db_executor_error._2)
      }
    }
  }
  /**
   * phone number
   */
  post("/register/step1",false){phone:String=>
    sendSMSVerifyCode(SqlProvider.checkUnSafeWord(phone))
  }
  private def sendSMSVerifyCode(phone:String)={
    val verifyCode=SMSUtil.generatorRandomNumber(6)
    val timeOut=2
    val msg=s"${verifyCode}为你本次验证码,10分钟内有效!"
    if(SMSUtil(msg,phone)){
      RedisProvider.redisCommand{client=>
        val key=generatorKey(phone)
        client.set(key,verifyCode.toString)
        client.expire(key,timeOut+2)
      }
      DefaultResponseContent(DefaultResponseCode.succeed._1,"成功")
    }else{
      DefaultResponseContent(DefaultResponseCode.method_was_closed._1,DefaultResponseCode.method_was_closed._2)
    }
  }
  /**
   * phone and verifyCode
   */
  post("/register/step2",false) { request: RegisterStep2Request =>
    RedisProvider.redisCommand { client =>
      val _code = client.get(generatorKey(request.phone))
      if (_code != null && _code.equals(request.verifyCode)) {
        DefaultResponseContent(DefaultResponseCode.succeed._1, "成功")
      } else {
        DefaultResponseContent(DefaultResponseCode.invalid_token._1, "验证码错误或者无效")
      }
    }
  }

  /**
   * phone number,verify code,password
   */
  post("/register/step3",false){request:RegisterStep3Request=>
    RedisProvider.redisCommand { client =>
      val _code = client.get(generatorKey(request.phone))
      if (_code != null && _code.equals(request.verifyCode)) {
        val insertSql=s"insert into tb_user(phone,password,creation) values('${request.phone}','${Tools.md5(request.password)}','${System.currentTimeMillis()}')"
        SqlProvider.noTransactionExec[Int](insertSql) match{
          case item::list=>{
            if(item>0){
              login(request.phone,request.password)
            }
          }
        }
      } else {
        DefaultResponseContent(DefaultResponseCode.invalid_token._1, "验证码错误或者无效")
      }
    }
  }
  post("/changeInfo"){request:Request=>
    val id=request.headerMap.getOrElse(FlashBirdConfig.key_access_token_info,"-1")
    val map=AncareTools.multiPartFormRequestDecoder(request).filter(kv=>kv._1!="password"&&kv._1!="creation"&&kv._1!="id")
    AncareTools.update(map,"tb_user",s"id='$id'")
  }
  /**
   * oldPassword,newPassword,id,
   */
  post("/changepwd"){request:DynamicPathParamsRequest[ChangePasswordBean]=>
    val currentUserId=request.underlying.headerMap.getOrElse(FlashBirdConfig.key_access_token_info,"-1")
    try{
      request.content match {
        case Some(bean)=>{
          if(currentUserId.equals(bean.id)){
            val updateSql=s"update tb_user set password='${Tools.md5(bean.newPwd)}' where id='${SqlProvider.checkUnSafeWord(bean.id)}' and password='${Tools.md5(bean.oldPwd)}'"
            SqlProvider.noTransactionExec[Int](updateSql) match{
              case item::list if item>0=>DefaultResponseContent(DefaultResponseCode.succeed._1,"修改密码成功")
            }
          }
        }
      }
    }catch{
      case ex:Throwable=>{
        ex.printStackTrace()
        DefaultResponseContent(DefaultResponseCode.user_not_exit._1,"密码错误")
      }
    }
  }

  post("/findpwd",false){request:RegisterStep3Request=>
    RedisProvider.redisCommand { client =>
      val _code = client.get(generatorKey(request.phone))
      if (_code != null && _code.equals(request.verifyCode)) {
        val updateSql=s"update tb_user set password='${Tools.md5(request.password)}' where phone='${SqlProvider.checkUnSafeWord(request.phone)}'"
        SqlProvider.noTransactionExec[Int](updateSql) match{
          case item::list=>{
            if(item>0){
              login(request.phone,request.password)
            }
          }
        }
      } else {
        DefaultResponseContent(DefaultResponseCode.invalid_token._1, "验证码错误或者无效")
      }
    }
  }
  post("/feedback"){fb:Map[String,String]=>
    val kvs:Map[String,String]=fb+("creation"->(System.currentTimeMillis()+""))
    AncareTools.insert(kvs,"tb_feedback")
  }


}
