package actor-fsms
import java.io.File


object FileReader {
  val lines = scala.io.Source.fromFile("amazon2.txt").getLines().toArray
  val l = lines.length
  var position = -1
  def get = {
    position +=1
    lines(position % l)
  }
}
