package com.ethowitz.scruby.core

import com.ethowitz.scruby.evaluator.MethodMap
import com.ethowitz.scruby.evaluator.ScrubyMethod
import com.ethowitz.scruby.parser.ScrubyObjectContainer

// TODO: do we need to store the name here?
class ScrubyObject(val klass: Symbol, val name: Option[Symbol], val ms: MethodMap) {
  def methods: MethodMap = predefMethods ++ ms

  def withMethod(method: (Symbol, ScrubyMethod)): ScrubyObject =
    ScrubyObject(klass, name, ms + method)

  protected def predefMethods: MethodMap = {
    val _klass: (Symbol, ScrubyMethod) = 'class ->
      ScrubyMethod(ScrubyObjectContainer(ScrubyString(klass.name)))
    val _initialize: (Symbol, ScrubyMethod) = 'new ->
      ScrubyMethod(ScrubyObjectContainer(ScrubyObject(klass, name, ms)))
    val nil: (Symbol, ScrubyMethod) = Symbol("nil?") ->
      ScrubyMethod(ScrubyObjectContainer(ScrubyFalseClass))

    MethodMap(nil, _klass, _initialize)
  }
}

object ScrubyObject {
  def apply(klass: Symbol, name: Option[Symbol], ms: MethodMap): ScrubyObject =
    new ScrubyObject(klass, name, ms)

  def apply(klass: Symbol): ScrubyObject = apply(klass, None, MethodMap())
  def apply(klass: Symbol, ms: MethodMap): ScrubyObject = apply(klass, None, ms)
  def apply(klass: Symbol, name: Symbol, ms: MethodMap): ScrubyObject =
    apply(klass, Some(name), ms)
}