package actor-fsms.regexp

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

//import MismatchedParentheses, UnrecognizedToken
//
//
//from classes import NFA, NFAFragment, State, FinalState
//import string
//import sys
object util {
  def precedence(c: Char) = c match {
    case '|' => 1
    case '.' => 2
    case '*' => 3
    case '+' => 3
    case '?' => 3
  }

  def re_to_posix(str: String): String = {
    val operators = Set[Char]('|', '.', '*', '+', '?')
    val binary_operators = Set('|', '.')
    val unary_operators = Set('?', '*', '+')
    var output: String = ""
    val stack = new mutable.Stack[Char]()
    for (c <- str) {
      if (operators contains c) {

        val o1 = c
        breakable {
          while (stack.nonEmpty && operators.contains(stack.top)) {
            val o2 = stack.top
            if ((binary_operators contains o1) && precedence(o2) >= precedence(o1) ||
              (unary_operators contains o1) && precedence(o2) > precedence(o1)) {
              output += stack.pop()
            } else {
              break()
            }
          }
        }
        stack.push(c)


      } else if (c == '(') {
        stack.push(c)
      } else if (c == ')') {
        var top = stack.pop()
        while (top != '(') {
          output += top
          top = stack.pop()
        }
      } else {
        output += c
      }
      //println(output)
    }

    for (top <- stack) {
      if (top == '(') {
        throw new MismatchedParentheses("Unclosed left parenthesis.")
      }
      output += top
    }
    output
  }

  def post2nfa(postfix: String): mutable.ListBuffer[State] = {
    val stack = new mutable.Stack[NFAFragment]()
    for (e <- postfix) {
      e match {
        case '.' => {
          val f2 = stack.pop()
          val f1 = stack.pop()
          f1.out.foreach(_.patch(f2.start))
          stack.push(NFAFragment(f1.start, f2.out))
        }
        case '|' => {
          val f2 = stack.pop()
          val f1 = stack.pop()
          val out: ListBuffer[State] = (f1.start :: f2.start :: Nil).to[ListBuffer]
          val s: State = State("#e", out)
          stack.push(NFAFragment(s, f1.out ++ f2.out))
        }
        case '?' => {
          val f = stack.pop()
          val tmp = State()
          val s = State("#e", (f.start :: tmp :: Nil).to[ListBuffer])
          f.out += tmp
          stack.push(NFAFragment(s, f.out))
        }
        case '*' => {
          val f = stack.pop()
          val tmp = State()
          val s = State("#e", (f.start :: tmp :: Nil).to[ListBuffer])
          f.out.foreach(_.patch(s))
          stack.push(NFAFragment(s, ListBuffer[State](tmp)))
        }
        case '+' => {
          val f = stack.pop()
          val tmp = State()
          val s = State("#e", (f.start :: tmp :: Nil).to[ListBuffer])
          f.out.foreach(_.patch(s))
          stack.push(NFAFragment(f.start, ListBuffer[State](tmp)))
        }
        case _ => {
          val out = State()
          stack.push(NFAFragment(State(e, out), out))
        }
      }
    }
    val final_state = State("#F", mutable.ListBuffer[State]())
    val frag = stack.pop()
    frag.out.foreach(_.patch(final_state))
    move_epsilon(mutable.ListBuffer[State](frag.start))
  }

  def move_epsilon(in: ListBuffer[State]): ListBuffer[State] = in.map(_.epsilon_step).flatten.toSet[State].to[ListBuffer]

  def make_step(in: ListBuffer[State], c: Char): ListBuffer[State] = move_epsilon(in.filter(_.symbol == c + "").map(_.out).flatten)

  def `match`(pattern: String, string: String): Boolean = {
    val pattern_postfix = re_to_posix(pattern)
    var nfa: mutable.ListBuffer[State] = post2nfa(pattern_postfix)
    for (c <-string) {
      nfa = make_step(nfa,c)
    }
    nfa.exists(_.symbol == "#F")
  }
}
