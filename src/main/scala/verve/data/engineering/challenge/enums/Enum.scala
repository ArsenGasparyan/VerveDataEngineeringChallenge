package verve.data.engineering.challenge.enums

import scala.reflect._

abstract class Enum[Enum: ClassTag] {
  self =>
  val values: Iterable[Enum] = {
    import runtime.universe._
    val mirror = runtimeMirror(self.getClass.getClassLoader)
    val classSymbol = mirror.classSymbol(self.getClass)

    classSymbol.toType.members
      .filter(_.isModule)
      .map(symbol => mirror.reflectModule(symbol.asModule).instance)
      .collect { case v: Enum => v }
  }

  def forName(name: String): Option[Enum] = values.find(_.name == name)

  implicit class RichardEnum(enum: Enum) {
    def name: String = enum.toString
  }

}