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
import scala.util.parsing.json.JSON
class MyApplication extends HttpServlet {

  type HandlerFunction = (HttpServletRequest, HttpServletResponse) => Unit
  /**
   *  Creating an object of Routing and Tempate class for later uses.
   */
  val routing = new Routing()
  val template = new Template()
  val persistance = new Persistence()
  override def init(config: ServletConfig) {
    super.init(config)

    
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some(() =>
    
    Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:" + System.getProperty("user.dir") + "/test;AUTO_SERVER=TRUE", "sa", "sa"),
        new H2Adapter))
    routing.addRoute("/", main)
    routing.addRoute("/documentation", documentation)
    routing.addRoute("/model", model)
    routing.addRoute("/download", download)
    routing.addRoute("/blog", blog)
    routing.addRoute("/newApp", creatNewApp)
    routing.addRoute("/anatomy", anatomy)
    routing.addRoute("/ide", ide)
    routing.addRoute("/sampleApp", sampleApp)
    routing.addRoute("/downloadFw", downloadFw)
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

  def blog(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("blog.xml")
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
  
  def creatNewApp(req: HttpServletRequest, resp: HttpServletResponse) = {
    val xml = template.setUpTemplate("newApp.xml")
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

  
  /*
  * register 
  */

  def Register(name: String, last: String, email: String, pass: String) = {

    var toReturn: Option[Long] = None

    transaction {

      var res = from(Forum.forum_users)(c =>
        where(c.email === email)
          select (c.id))

      if (res != null)
        toReturn = res.headOption

    }

    toReturn match {
      case Some(id: Long) =>

        repeatation
      case _ =>
        if ((name.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty()) ||((name.isEmpty()) && ( last.isEmpty() && email.isEmpty() && pass.isEmpty()))) {
        	fillUp
        } else {

          LogOut(name, last, email, pass)
        }

    }
  }

  //handler function for SignOut
  def LogOut(name: String, last: String, email: String, pass: String) = {

    transaction {
      Forum.forum_users.insert(new User(name, last, email, pass))
    }

    <html>
      <head>
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="mainCss.css"/>
      </head>
      <body>
        <header>
          <h1 id="welcome">Welcome To Our Framework</h1>
        </header>
        <nav>
          <ul>
            <li class="active" id="Documentation"><a href="http://localhost:8080/documentation">Documentation</a></li>
            <li class="active" id="Model"><a href="http://localhost:8080/model">Model</a></li>
            <li class="active" id="Blog"><a href="http://localhost:8080/blog">Blog</a></li>
            <li class="active" id="Download"><a href="http://localhost:8080/download">Download</a></li>
          </ul>
        </nav>
        <section>
          <article>
            <h1>Congratulations { name }</h1>
            <h2>your registration is complete!</h2>
          </article>
          <aside>
            <img id="myImage" src="http://www.lss.lu.unisi.ch/pictures/usi_partialview.jpg" alt="myImage"/>
          </aside>
        </section>
        <footer>
          <p>2011 fatima and samuel, Last Update!!</p>
        </footer>
      </body>
    </html>
  }

  def repeatation = {

    <html>
      <head>
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="mainCss.css"/>
      </head>
      <body>
        <header>
          <h1 id="welcome">Welcome To Our Framework</h1>
        </header>
        <nav>
          <ul>
            <li class="active" id="Documentation"><a href="http://localhost:8080/documentation">Documentation</a></li>
            <li class="active" id="Model"><a href="http://localhost:8080/model">Model</a></li>
            <li class="active" id="Blog"><a href="http://localhost:8080/blog">Blog</a></li>
            <li class="active" id="Download"><a href="http://localhost:8080/download">Download</a></li>
          </ul>
        </nav>
        <section>
          <article>
            <h2>  This email is already reagistered!!</h2>
          </article>
          <aside>
            <img id="myImage" src="http://www.lss.lu.unisi.ch/pictures/usi_partialview.jpg" alt="myImage"/>
          </aside>
        </section>
        <footer>
          <p>2011 fatima and samuel, Last Update!!</p>
        </footer>
      </body>
    </html>
  }

  def fillUp = {

    <html>
      <head>
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="mainCss.css"/>
      </head>
      <body>
        <header>
          <h1 id="welcome">Welcome To Our Framework</h1>
        </header>
        <nav>
          <ul>
            <li class="active" id="Documentation"><a href="http://localhost:8080/documentation">Documentation</a></li>
            <li class="active" id="Model"><a href="http://localhost:8080/model">Model</a></li>
            <li class="active" id="Blog"><a href="http://localhost:8080/blog">Blog</a></li>
            <li class="active" id="Download"><a href="http://localhost:8080/download">Download</a></li>
          </ul>
        </nav>
        <section>
          <article>
            <h2> Fill the remaining fields and sign up again.(name.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty()</h2>
          </article>
          <aside>
            <img id="myImage" src="http://www.lss.lu.unisi.ch/pictures/usi_partialview.jpg" alt="myImage"/>
          </aside>
        </section>
        <footer>
          <p>2011 fatima and samuel, Last Update!!</p>
        </footer>
      </body>
    </html>
  }

  
  def getAllUsers(req: HttpServletRequest, resp: HttpServletResponse) {

    val xml = ListUsers
    val html = prepareForm(xml)
    resp.getWriter.write(new PrettyPrinter(72, 2).formatNodes(html))
  }

  //show all accounts
  def ListUsers = {

    <html>
      <head>
        <title>Accounts:</title>
      </head>
      <body>
        <h3>All accounts</h3>
        <table border="1">
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>password</th>
          </tr>
          {
            persistance.getUsers

          }
        </table>
      </body>
    </html>
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

