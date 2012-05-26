/**
* @author Fatima Chegini
* @author Samuel Measho
 */
package ourSkeleton.routing

import javax.servlet.http._
import javax.servlet.ServletConfig
import scala.collection.mutable.HashMap

class Routing {
  type HandlerFunction = (HttpServletRequest, HttpServletResponse) => Unit
  val  routeTable = new HashMap[String, HandlerFunction]

  /**
   * Function that adds a new route to the routeTable, with an associated handler function
   * @param path The new path to be added to the route
   * @param function The function associated to this new path
   */
  def addRoute(path: String, function: HandlerFunction) {
    routeTable(path) = function
  }

  /**
   * Function that checks if it exists some function in the routeTable
   * @param req Request from the servlet
   * @param resp Response to the servlet
   * @return Boolean True if there exists a function, false otherwise
   */
  def tableDispatch(req: HttpServletRequest, resp: HttpServletResponse): Boolean = {
    routeTable.get(req.getRequestURI) match {
      case Some(f) => f(req, resp); true
      case None => false
    }
  }
  
  /**
   * Function that return the routeTable
   * @return routeTable The routeTable
   */
  def getRouteTable: HashMap[String, HandlerFunction] = return routeTable
  
  /**
   * Function that remove a specific route from the routeTable
   * @param s A string that represent the route that you want to delete
   */
  def removeRoute(s: String) = routeTable.remove(s)
  
  /**
   * Function that remove all the mapping routes from the routeTable
   */
  def clearRouteTable = routeTable.clear()
}