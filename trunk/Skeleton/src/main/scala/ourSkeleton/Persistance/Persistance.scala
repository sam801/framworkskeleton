package ourSkeleton.Persistance
/**
 * @author Samuel Measho
 * @author Fatima Chegini
 *
 */
import org.squeryl.KeyedEntity
import org.squeryl.SessionFactory
import org.squeryl.Session
import org.squeryl.adapters.H2Adapter
import scala.util.matching.Regex
import org.squeryl.adapters.H2Adapter
import java.sql.Timestamp
import java.sql.Date
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.annotations.OptionType

object Forum extends org.squeryl.Schema {

  val forum_users = table[User]
  val messages = table[Message]
}

//settings forum_users
class User(
  val name: String,
  val last: String,
  val email: String,
  val pass: String) extends KeyedEntity[Long] {
  val id = 0L

  def login(name: String, pass: String) {
    from(Forum.forum_users)(a =>
      where(a.name === name and a.pass === pass)
        select (a))
  }

}

//setting for Message

class Message(
  val message: String,
  val user_id: Long,
  val user_name: String) extends KeyedEntity[Long] {
  val id = 0L

  def login(message: String) {
    from(Forum.messages)(a =>
      where(a.message === message)
        select (a))

  }

}

class Persistence {

  def getUsers = {
    transaction {
      for (a <- Forum.forum_users)
        yield <tr><td>{ a.name }</td><td>{ a.email }</td><td>{ a.pass }</td></tr>
    }
  }

  def FindUser(email: String, pass: String) = {
    transaction {
      for (a <- Forum.forum_users)
        yield <tr><td>{ a.name }</td></tr>
    }
  }

  def getMessages = {
    transaction {
      for (a <- Forum.messages)
        yield <tr><td>{ a.message }</td><td>{ a.user_id }</td><td>{ a.user_name }</td></tr>
    }

  }
  def search(message: String) {
    from(Forum.messages)(a =>
      where(a.message === message)
        select (a))
  }

}

  
