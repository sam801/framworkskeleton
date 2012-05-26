package myApp.Template
import javax.servlet.http._
import scala.xml._

/**

 * @author Fatima Chegini
 * @author Samuel Measho
 */


import javax.servlet.http._
import scala.xml._

class Template {
  /**
   * Function that loads a page to be displayed
   * @param filename The file to be displayed
   */
  def loadPage(fileName: String): NodeSeq = {
    val basePage = scala.io.Source.fromFile("src/main/scala/ourSkeleton/" + fileName).getLines.mkString
    return XML.loadString(basePage)
  }
  
  def loadCss(filePath: String) = {
    val basePage = scala.io.Source.fromFile("src/main/scala/ourSkeleton/" + filePath).getLines.mkString
      
    basePage
  }
   def loadJs(filePath: String) = {
    val basePage = scala.io.Source.fromFile("src/main/scala/ourSkeleton/" + filePath)(scala.io.Codec.ISO8859).toArray
      
    basePage
  }

   

  /**
   * Function that loads a template to be displayed
   * @param fileName The file containing the template to be used
   */
  def getDefaultTemplate(fileName: String) = {
    val surround = scala.io.Source.fromFile("src/main/scala/ourSkeleton/" + fileName).getLines.mkString
    XML.loadString(surround)
  }

  /**
   * Adds content to the template
   * @param content
   * @param tag
   */
  def getNewTemplate(content: String, tag: String) = {
    val surround = """<surround with=""" + tag + """><p>Here """ + content + """</p></surround>"""
    XML.loadString(surround)
  }

  /**
   * Loads the default template to be used, and display it
   * @param req Request from the servlet
   * @param resp Response to the servlet
   */
  def setUpTemplate(fileName: String) = {
    val template = getDefaultTemplate(fileName)
    val ns = processTemplate(template)
    ns
  }

  /**
   * Function that makes changes to the template
   * @param template The template to be changed
   */
  def processTemplate(template: NodeSeq): NodeSeq = {
    import scala.xml.transform._
    import scala.xml.Null // hide scala.Null
    class ProcessWrapper(ns: NodeSeq, location: String) extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "div" && (elem.attribute("id")).toString() == "Some(" + location + "/)" =>
          ns
        case n => n
      }
    }

    object ProcessSurround extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "surround" =>
          val wrapperName = (elem \\ "@with").text
          val where = (elem \\ "@at").text

          val wrapperNode = loadPage(wrapperName)

          val wrappedNode = elem.child
          wrapperNode.map(ns => new RuleTransformer(new ProcessWrapper(wrappedNode, where))(ns))
        case n => n
      }
    }
    template.map(ns => new RuleTransformer(ProcessSurround)(ns))
  }
}