package actor-fsms

import java.util.Calendar

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive

import scala.collection.mutable.ArrayBuffer

case class RegisterHandler(ref: ActorRef)

case object ACK

case object START

class TestWrapper(parallel_limit: Int, total_number: Int) extends Actor {
  var done = 0
  var next = 0
  val array_buffer = ArrayBuffer[ActorRef]()
  var start = 0L


  def send = if (next < total_number) {
    array_buffer(next % array_buffer.length) ! MatchIt(FileReader.get.toList)
    next += 1
  }

  override def receive: Receive = {
    case x: RegisterHandler => array_buffer.append(x.ref)
    case ACK => {
      send
      done += 1
      if (done == total_number) {
        println(Calendar.getInstance().getTimeInMillis + " Done")
        println("Time taken: " + (Calendar.getInstance().getTimeInMillis - start))
        Oracle.system.shutdown()
        Main.system.shutdown()
      }
    }
    case START => start = Calendar.getInstance().getTimeInMillis
      println(Calendar.getInstance().getTimeInMillis + " Start")
      for (i <- 1 to total_number) send

  }
}

class AmazonTestWrapper(parallel_limit: Int, total_number: Int) extends TestWrapper(parallel_limit, total_number) {
  override def send = if (next < total_number) {
    array_buffer(next % array_buffer.length) ! MatchString(FileReader.get)
    next += 1
  }
}
