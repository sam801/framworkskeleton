/**
 * @author Fatemeh Chegini
 * @author Samuel Measho
 */
package ourSkeleton
import javax.servlet.http._
import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.http._
import javax.servlet.AsyncContext
import javax.servlet.ServletConfig
import scala.xml._
import scala.collection.JavaConversions._
import scala.util.matching.Regex
import scala.xml._
import org.squeryl.KeyedEntity
import org.squeryl.SessionFactory
import org.squeryl.Session
import org.squeryl.adapters.H2Adapter
import scala.xml.PrettyPrinter
import scala.xml.NodeSeq
import scala.xml.Elem
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.annotations.OptionType
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import org.scribe._
import org.scribe.builder._
import org.scribe.builder.api._
import org.scribe.model._
import org.scribe.oauth._
import ourSkeleton.routing._
import ourSkeleton.Template._
import scala.util.parsing.json.JSON
class MyApplication extends HttpServlet {

  type HandlerFunction = (HttpServletRequest, HttpServletResponse) => Unit
  /**
   *  Creating an object of Routing and Tempate class for later uses.
   */
  val routing =  new Routing()
  val template = new Template()

  override def init(config: ServletConfig) {
    super.init(config)

    ////////////////////////////////////////////
    // 					  					  //
    //		YOU CAN ADD ROUT HERE			  //
    // 										  //
    ////////////////////////////////////////////

    routing.addRoute("/", main)
    routing.addRoute("/documentation", documentation)
    routing.addRoute("/code", code)
    routing.addRoute("/model", model)
    routing.addRoute("/download", download)
    routing.addRoute("/mainCss.css", css)
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) {

    //Routing dispatch
    if (routing.tableDispatch(req, resp))
      return
    resp.sendError(HttpServletResponse.SC_NOT_FOUND)

  }

  def main(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("main.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def documentation(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("documentation.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def code(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("code.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def model(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("model.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def download(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("download.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }
  def css(req: HttpServletRequest, resp: HttpServletResponse) {

    val css = template.loadCss("mainCss.css")
    resp.getWriter.write(css)
    resp.setContentType("text/css")

  }
  def js(req: HttpServletRequest, resp: HttpServletResponse) {

    val js = template.loadJs("search.js")
    resp.getWriter.write(js)
    resp.setContentType("text/css")

  }

  /**
   * Prepare the form
   * @param template The template of the form we are using
   * @return NodeSeq The template changed
   */
  def prepareForm(template: NodeSeq): NodeSeq = {
    import scala.xml.transform._
    import scala.xml.Null // hide scala.Null
    object MakeForm extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case form: Elem if form.label == "form" =>
          val Some(handler) = form.attribute("handler")
          var Some(method) = form.attribute("method")

          // Make the handler and install in routeTable
          val (uri, f: HandlerFunction) = makeHandler(handler.text)

          routing.addRoute(uri, f)

          // Add an action to the form.
          form % Attribute(None, "action", Text(uri), Null)
        case n => n
      }
    }
    template.map(ns => new RuleTransformer(MakeForm)(ns))
  }
  /**
   * Creates a new path with its corresponding function
   * @param s
   * @return String A new path
   * @return HandlerFunction The corresponding function
   */
  def makeHandler(s: String): (String, HandlerFunction) = {
    // Parse the handler string.
    val q = "^(\\w+)\\((.*)\\)$".r

    val (methodName, argNames) = s match {
      case q(methodName, argNames) => (methodName, argNames.split(",").map(_.trim))
      case _ => throw new RuntimeException("bad handler: " + s)
    }

    // Look for a method that takes the right number of strings.
    val method = this.getClass.getMethod(methodName, argNames.map(_ => classOf[String]): _*)
    val h = (req: HttpServletRequest, resp: HttpServletResponse) => {
      val result = method.invoke(this, argNames.map(k => req.getParameter(k)): _*)
      result match {
        case s: String =>
          resp.getWriter.write(s)
        case html: NodeSeq =>
          resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
      }
    }
    (handlerURI(methodName), h)
  }

  /**
   * Constructs a new path with the method name
   * @param methodName The method that will be used as path
   */
  def handlerURI(methodName: String) = {
    "/" + methodName
  }

}

