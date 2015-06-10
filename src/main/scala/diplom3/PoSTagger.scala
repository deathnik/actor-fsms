package actor-fsms

import java.io.FileInputStream
import opennlp.tools.postag.{POSTaggerME, POSModel}

object PoSTagger {
  val path = "en-pos-maxent.bin"
  val tagger = new POSTaggerME(new POSModel(new FileInputStream(path)))
  val m = collection.immutable.HashMap(1043 -> "RBR", 1066 -> "JJS", 11055 -> "VBN", 11286 -> "VBP", 12114 -> "TO", 1257 -> "JJR", 1267 -> ",", 1502 -> "RP", 15242 -> "VBD", 18267 -> "CC", 18739 -> "VBZ", 19421 -> "VB", 23223 -> "NNS", 260 -> "FW", 26262 -> "PRP", 2637 -> "WDT", 26641 -> "NNP", 268 -> "NNPS", 28328 -> "RB", 3010 -> "WP", 3262 -> "WRB", 397 -> "PDT", 41629 -> "JJ", 425 -> "RBS", 55683 -> "DT", 5653 -> "CD", 60341 -> "IN", 618 -> ":", 6641 -> "MD", 6859 -> "PRP$", 8398 -> "VBG", 843 -> ".", 867 -> "EX", 91667 -> "NN")
  val map = m.map(x => x._2).zip("qwertyuiopasdfghjklzxcvbnm1234567890").toMap
  def resolve(s: String): Char = {
    val sp = s.split(" ")
    if(s == null || s =="" || sp==null) return '.'
   // println(s)
    try {
      val tags = tagger.tag(sp)
      if(!map.contains(tags(0))) '`' else map(tags(0))
    } catch {
      case x:java.lang.NullPointerException => {
        '.'
      }
    }

  }

  def res(s: String) = tagger.tag(s.split(" "))
}
