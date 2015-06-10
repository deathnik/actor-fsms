package actor-fsms.regexp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class State(var symbol: String, var out: mutable.ListBuffer[State]) {
  def patch(s: State): Unit = {
    symbol = "#e"
    out = ListBuffer[State](s)
  }

  def epsilon_step: mutable.ListBuffer[State] = {
    if(symbol!="#e") ListBuffer[State](this)
    else util.move_epsilon(out)
  }
}

object State {
  def apply(symbol: Char, out: mutable.ListBuffer[State]) = new State(symbol + "", out)

  def apply(symbol: Char, out: State) = new State(symbol + "", mutable.ListBuffer[State](out))

  def apply(symbol: String, out: mutable.ListBuffer[State]) = new State(symbol, out)

  def apply() = new State("", mutable.ListBuffer[State]())
}

class NFAFragment(val start: State, var out: mutable.ListBuffer[State]) {
  def this(start: State, out: State) = this(start, mutable.ListBuffer[State](out))
}

object NFAFragment {
  def apply(start: State, out: State) = new NFAFragment(start, out)

  def apply(start: State, out: mutable.ListBuffer[State]) = new NFAFragment(start, out)
}