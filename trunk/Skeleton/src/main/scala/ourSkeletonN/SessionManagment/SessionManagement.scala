/**
 * @author Fatima Chegini
 * @author Samuel Measho
 */
package ourSkeletonN.SessionManagment
import javax.servlet.http._
import scala.xml._

class SessionManagement {
  // Generate unique identifiers.
  object UniqueId {
    var n = 0
    def next: String = this.synchronized {
      n += 1
      "$" + n
    }
  }
  trait Var[A] extends Function0[A] {
    def update(x: A): Unit
    def alter(f: A => A) = update(f(apply))
    def map[B](body: A => B): B = body(apply)
  }

  // A utility class for thread-local variables.
  class ThreadLocal[T](init: => T) extends java.lang.ThreadLocal[T] with Var[T] {
    override def initialValue: T = init
    def apply = get
    def update(x: T) = set(x)
  }

  // An object representing a request context.
  // Just a wrapper around the request and response objects.
  class Context(val request: HttpServletRequest, val response: HttpServletResponse) {
    def session = request.getSession
    def uri = request.getRequestURI
  }

  // The current context. Each request has its own thread, so the context needs to be
  // thread local.
  var currentContext = new ThreadLocal[Context](null)
  
  def getCurrentContext = {
    currentContext
  }
  
  def newContext(req: HttpServletRequest, resp: HttpServletResponse, context: SessionManagement.this.ThreadLocal[SessionManagement.this.Context]): SessionManagement.this.ThreadLocal[SessionManagement.this.Context] = {
    context() = new Context(req, resp)
    return context
  }
  /*
   * Abstract classes can have constructor parameters as well as type parameters.
   *  Traits can have only type parameters. 
   *  There was some discussion that in future even traits can have constructor parameters
   *  Abstract classes are fully interoperable with Java. You can call them from Java code without any wrappers.
   *   Traits are fully interoperable only if they do not contain any implementation code
   */
  abstract class KeyedVar[A](default: A) extends Var[A] {
    val key = this.toString + UniqueId.next

    protected def get: Any

    def apply = get match {
      case null => default
      case x: A => x
      case x => throw new RuntimeException("value of wrong type: " + x)
    }
  }

  // A mutable variable whose lifetime is a session.
  class SessionVar[A](default: A) extends KeyedVar[A](default) {
    //currentContext()=currentContext.apply
    def update(x: A) = currentContext().session.setAttribute(key, x)
    protected def get = currentContext().session.getAttribute(key)
  }

  // A mutable variable whose lifetime is a single request.
  class RequestVar[A](default: A) extends KeyedVar[A](default) {
    def update(x: A) = currentContext().request.setAttribute(key, x)
    protected def get = currentContext().request.getAttribute(key)
  }

  class KeyVar[A](default: A) extends KeyedVar[A](default) {
    def update(x: A) = currentContext().session.setAttribute(key, x)
    //currentContext().session.setMaxInactiveInterval(5)
    protected def get = currentContext().session.getId()
  }

  object Count extends SessionVar[Int](0)
  object RequestCount extends RequestVar[Int](0)
  object IdSession extends KeyVar[Int](0)
  
  def getIdSession = {
    IdSession
  }

  def renderPageSession = {
    updateSession

    <html>
      <head>
        <title>Hello!</title>
      </head>
      <body>
        <p>The current count for this session is { Count() }. This should increment on each refresh.</p>
        <p>The current count for this request is { RequestCount() }. This should always be 1.</p>
        <p>The current key is { IdSession() }</p>
      </body>
    </html>
  }

  def updateSession = {
    Count() = Count() + 1
    RequestCount.alter(_ + 1)
    IdSession
  }
  
  def justSession(req: HttpServletRequest, resp: HttpServletResponse) {
    req.getRequestURI match {
      case "/session" =>
        resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(renderPageSession))
      case _ =>
        resp.sendError(HttpServletResponse.SC_NOT_FOUND)
    }
  }
}