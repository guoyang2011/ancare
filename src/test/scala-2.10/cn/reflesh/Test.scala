package cn.reflesh

/**
 * Created by yangguo on 15/12/2.
 */
trait R{
  def executor:Unit
}
class Test extends R{
  override def executor: Unit = println("hello")
}
