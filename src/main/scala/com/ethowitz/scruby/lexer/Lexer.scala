package com.ethowitz.scruby.lexer

import com.ethowitz.scruby.exceptions.LexerError
import scala.util.parsing.combinator._

object Lexer extends RegexParsers {
  override def skipWhitespace = false

  def apply(code: String): Either[LexerError, List[Token]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, _) => Left(LexerError(msg))
      case Success(result, _) => Right(result.filter(_ != Whitespace))
    }
  }

  def ampersand = "&" ^^ (_ => Ampersand)
  def assigner = "=" ^^ (_ => Assigner)
  def ivarPrefix = "@" ^^ (_ => IvarPrefix)
  def scopeResolver = "::" ^^ (_ => ScopeResolver)
  def comma = "," ^^ (_ => Comma)
  def period = "." ^^ (_ => Period)
  def openingParenthesis = "(" ^^ (_ => OpeningParenthesis)
  def closingParenthesis = ")" ^^ (_ => ClosingParenthesis)
  def openingCurlyBracket = "{" ^^ (_ => OpeningCurlyBracket)
  def closingCurlyBracket = "}" ^^ (_ => ClosingCurlyBracket)
  def openingSquareBracket = "[" ^^ (_ => OpeningSquareBracket)
  def closingSquareBracket = "]" ^^ (_ => ClosingSquareBracket)
  def not = "!" ^^ (_ => Not)
  def colon = ":" ^^ (_ => Colon)
  def arrow = "=>" ^^ (_ => Arrow)
  def backslash = "\\" ^^ (_ => Backslash)
  def separator = "[\n|;|\r\f]+".r ^^ (_ => Separator)
  def whitespace = "[ ]+".r ^^ (_ => Whitespace)

  def identifier: Parser[IdentifierToken] = {
    "[a-zA-Z_=<>%&\\*\\|][a-zA-Z0-9_]*[?!]?".r ^^ { s => IdentifierToken(s) }
  }

  def string: Parser[StringLiteral] = {
    "'[^']*'".r ^^ { s => StringLiteral(s.substring(1, s.length - 1)) }
  }

  def symbol: Parser[SymbolLiteral] = {
    ":[a-zA-Z_=<>%&\\*\\|][a-zA-Z0-9_]*[?!]?".r ^^ { s => SymbolLiteral(s) }
  }

  def integer: Parser[IntegerLiteral] = {
    "0|[1-9]+[0-9]*".r ^^ { n => IntegerLiteral(n.toInt) }
  }

  def float: Parser[FloatLiteral] = {
    "(0|[1-9]+[0-9]*)+\\.[0-9]+".r ^^ { n => FloatLiteral(n.toFloat) }
  }

  private def parsingGroupOne = string | symbol | identifier | integer | float

  private def parsingGroupTwo = assigner | ampersand | ivarPrefix | scopeResolver | comma | period |
    openingParenthesis | closingParenthesis | openingCurlyBracket | closingCurlyBracket |
    openingSquareBracket | closingSquareBracket | not | colon | separator | whitespace

  private def tokens: Parser[List[Token]] = phrase(rep1(parsingGroupOne | parsingGroupTwo))
}
