import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server, ServerConnector}
import org.eclipse.jetty.util.thread.QueuedThreadPool

object DemoService {
  def main(args: Array[String]): Unit = {
    // Version 1 did just start Jetty
    // val server = new Server(8080)
    // It creates many acceptors, selectors and threads on big machines, even when running in small container
    // So, let's explicitly configure it.
    val minThreads = System.getProperty("jetty.min-threads", "8").toInt
    val maxThreads = System.getProperty("jetty.max-threads", "200").toInt
    var threadPool = new QueuedThreadPool(minThreads, maxThreads)

    val server = new Server(threadPool)
    val acceptorCount = System.getProperty("jetty.acceptor-threads", "-1").toInt
    val selectorCount = System.getProperty("jetty.selector-threads", "-1").toInt
    server.setConnectors(Array(new ServerConnector(server, acceptorCount, selectorCount)))

    server.setHandler(new OurHandling)
    server.start()
  }

  class OurHandling extends AbstractHandler {
    // Limit the amount of items in memory for this example.
    // So, max ~192*256k=48MBytes
    val maxMemoryItems = 192
    val memoryPerItem = 256 * 1024
    val memoryWasting = new ArrayBlockingQueue[Array[Byte]](maxMemoryItems)
    val createdItemsCounter = new AtomicInteger()

    override def handle(target: String,
                        baseRequest: Request,
                        request: HttpServletRequest,
                        response: HttpServletResponse): Unit = {
      response.setHeader("Content-Type", "application/json")

      val path = request.getRequestURI
      val pathSegments = path.split("/").toList.filter(_.nonEmpty)
      if (request.getMethod == "GET") {
        pathSegments match {
          // Web root aka /
          case Nil => {
            // On each request, consume some memory.
            // And keep things in memory to simulate memory use
            val chunkOfMemory = Array.ofDim[Byte](memoryPerItem)
            var addedToQueue = memoryWasting.offer(chunkOfMemory)

            // Throw things away until we've space
            while (!addedToQueue) {
              memoryWasting.poll()
              addedToQueue = memoryWasting.offer(chunkOfMemory)
            }

            val createdSoFar = createdItemsCounter.incrementAndGet()
            response.getWriter.write(
              s"""
                 |{
                 |"title":"Hello World! Let's use some memory",
                 |"created-overall":"$createdSoFar",
                 |"queue-size":"${memoryWasting.size()}",
                 |"description":"Hello. I'm so memory hungry. kthxbye!"
                 |}
              """.stripMargin)
          }
          case others => {
            response.setStatus(404)
            response.getWriter.write(
              """{
                |"error":"Not found",
                |"description":"This pitiful server only has /",
                |"home":{"href":"/"}
                |}
              """.stripMargin)
          }
        }
      } else {
        // Our pitiful service only handles GET
        response.sendError(405)
      }
      baseRequest.setHandled(true)
    }
  }

}
