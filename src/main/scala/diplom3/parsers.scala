package actor-fsms


import akka.actor.{ActorSystem, Actor, Props, ActorRef}
import akka.actor.Actor.Receive
import actor-fsms.regexp.{State, util}

import scala.collection.mutable
import scala.collection.mutable.HashMap

case class MatchIt(data: List[Char])

case class MatchString(data: String)

object Oracle {
  val system = ActorSystem("Magic")
  val cash = mutable.HashMap[mutable.ListBuffer[State], ActorRef]()
  var controller: ActorRef = null

  def get(states: mutable.ListBuffer[State]): ActorRef =
    if (cash.contains(states)) {
      cash(states)
    } else {
      val start = system.actorOf(Props(new ParallelParser(states, controller)))
      cash.put(states, start)
      start
    }

  def init(pattern: String): ActorRef = get(util.post2nfa(util.re_to_posix(pattern)))
}

class SequentialParser(start: mutable.ListBuffer[State]) extends Actor {
  def this(pattern: String) = this(util.post2nfa(util.re_to_posix(pattern)))

  override def receive: Actor.Receive = {
    case x: MatchIt =>
      var current = start
      for (c <- x.data) current = util.make_step(current, c)
      sender() ! ACK
  }
}

class CashingSequentialParser(start: mutable.ListBuffer[State]) extends Actor {
  def this(pattern: String) = this(util.post2nfa(util.re_to_posix(pattern)))

  val cash = mutable.HashMap[(mutable.ListBuffer[State], Char), mutable.ListBuffer[State]]()

  def make_step(metastate: mutable.ListBuffer[State], c: Char): mutable.ListBuffer[State] = {
    if (!cash.contains((metastate, c))) {
      cash.update((metastate, c), util.make_step(metastate, c))
    }
    cash((metastate, c))
  }

  override def receive: Actor.Receive = {
    case x: MatchIt =>
      var current = start
      for (c <- x.data) current = make_step(current, c)
      sender() ! ACK

    case y: MatchString =>
      var current = start
      for (c <- y.data.split("\\.").map(x => PoSTagger.resolve(x))) current = make_step(current, c)
      sender() ! ACK
  }
}


class ParallelParser(states: mutable.ListBuffer[State], controller: ActorRef) extends Actor {
  val transitions = mutable.HashMap[Char, ActorRef]()

  def make_step(c: Char) = {
    if (!(transitions contains c)) {
      transitions.put(c, Oracle.get(util.make_step(states, c)))
    }
    transitions(c)
  }

  override def receive: Actor.Receive = {
    case x: MatchIt => x.data match {
      case Nil => {
        controller ! ACK
      }
      case h :: t => make_step(h) ! MatchIt(t)

    }
    //for amazon
    case y: MatchString => y.data.split(" ", 2) match {
      case Array(x: String, y: String) => {
        if (y == null || x ==null) make_step(PoSTagger.resolve(x)) ! MatchIt(Nil)
        else make_step(PoSTagger.resolve(x)) ! MatchString(y)
      }
      case Array(x: String) => make_step(PoSTagger.resolve(x)) ! MatchIt(Nil)
    }
  }
}