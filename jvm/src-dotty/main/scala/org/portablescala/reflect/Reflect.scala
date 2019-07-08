package org.portablescala.reflect

import scala.collection.mutable

import java.lang.reflect._

import org.portablescala.reflect.annotation._

object Reflect extends ReflectCommons {

  /** Reflectively looks up a loadable module class using the current class
   *  loader.
   *
   *  A module class is the technical term referring to the class of a Scala
   *  `object`. The object or one of its super types (classes or traits) must
   *  be annotated with
   *  [[org.portablescala.reflect.annotation.EnableReflectiveInstantiation @EnableReflectiveInstantiation]].
   *  Moreover, the object must be "static", i.e., declared at the top-level of
   *  a package or inside a static object.
   *
   *  If the module class cannot be found, either because it does not exist,
   *  was not `@EnableReflectiveInstantiation` or was not static, this method
   *  returns `None`.
   *
   *  This method is equivalent to calling
   *  {{{
   *  Reflect.lookupLoadableModuleClass(fqcn, this.getClass.getClassLoader)
   *  }}}
   *
   *  @param fqcn
   *    Fully-qualified name of the module class, including its trailing `$`
   */
  def lookupLoadableModuleClass(fqcn: String): Option[LoadableModuleClass] = ???

  /** Reflectively looks up an instantiatable class using the current class
   *  loader.
   *
   *  The class or one of its super types (classes or traits) must be annotated
   *  with
   *  [[org.portablescala.reflect.annotation.EnableReflectiveInstantiation @EnableReflectiveInstantiation]].
   *  Moreover, the class must not be abstract, nor be a local class (i.e., a
   *  class defined inside a `def` or inside an anonymous function). Inner
   *  classes (defined inside another class) are supported.
   *
   *  If the class cannot be found, either because it does not exist,
   *  was not `@EnableReflectiveInstantiation` or was abstract or local, this
   *  method returns `None`.
   *
   *  This method is equivalent to calling
   *  {{{
   *  Reflect.lookupInstantiatableClass(fqcn, this.getClass.getClassLoader)
   *  }}}
   *
   *  @param fqcn
   *    Fully-qualified name of the class
   */
  def lookupInstantiatableClass(fqcn: String): Option[InstantiatableClass] = ???
}
