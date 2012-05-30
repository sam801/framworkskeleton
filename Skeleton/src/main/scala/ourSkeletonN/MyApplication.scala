/**
 * @author Fatemeh Chegini
 * @author Samuel Measho
 */
package ourSkeletonN
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
import ourSkeletonN.routing._
import ourSkeletonN.Template._
import ourSkeletonN.Persistance._
import ourSkeletonN.SessionManagment._
import scala.util.parsing.json.JSON
class MyApplication extends HttpServlet {

  type HandlerFunction = (HttpServletRequest, HttpServletResponse) => Unit
  /**
   *  Creating an object of Routing and Tempate class for later uses.
   */
  val routing = new Routing()
  val template = new Template()
  val persistance = new Persistence()
  val session = new SessionManagement()

  override def init(config: ServletConfig) {
    super.init(config)

    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some(() =>

      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:" + System.getProperty("user.dir") + "/test;AUTO_SERVER=TRUE", "sa", "sa"),
        new H2Adapter))
    routing.addRoute("/", documentation)
    routing.addRoute("/documentation", documentation)
    routing.addRoute("/modules", module)
    routing.addRoute("/download", download)
    routing.addRoute("/contact", contact)
    routing.addRoute("/features", features)
    routing.addRoute("/anatomy", anatomy)
    routing.addRoute("/ide", ide)
    routing.addRoute("/sampleApp", sampleApp)
    routing.addRoute("/downloadFw", downloadFw)
    routing.addRoute("/mainCss.css", css)
    routing.addRoute("/session", session.justSession)
    routing.addRoute("/template", templateFw)
    routing.addRoute("/routing", routingFw)
    routing.addRoute("/persistance", persistanceFw)
    routing.addRoute("/sessionfw", sessionFw)
    routing.addRoute("/form", formFw)
    routing.addRoute("/createApp", createApp)
    routing.addRoute("/session", session.justSession)
    routing.addRoute("/checking", checking)
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) {
    var currentContext = session.getCurrentContext
    currentContext = session.newContext(req, resp, currentContext)
    currentContext().session.setMaxInactiveInterval(10)
    if (currentContext().session.isNew()) {

    }

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

  def contact(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("contact.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def module(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("module.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def download(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("download.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def sampleApp(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("sampleApp.xml")
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

  def features(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("features.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def anatomy(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("anatomy.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def ide(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("ide.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def downloadFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("ide.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def createApp(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("createApp.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }
  /*
   * features 
   */

  def routingFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("routing.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def templateFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("template.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def persistanceFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("persistance.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  def formFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("form.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }
  def sessionFw(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("session.xml")
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }
  
 def checking(req: HttpServletRequest, resp: HttpServletResponse){
  
     val xml = template.loadBinaryFile("socialNetwork.jpg")
     print(xml)
    
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

