package parser

import scala.xml.{Elem, Node, NodeSeq, XML}

object CityParser {

  def parse(xml: String): Either[ParserError, CityData] = {
    implicit val doc: Elem = XML.loadString(xml)

    val params = parameters.map(node => parameterName(node) -> parameterValue(node)).toMap

    Right(
      CityData(
        name = CityName(name),
        wikiDataId = WikiDataId(wikiDataId),
        country = Country(country),
        population = Population(population.toInt),
        tags = tags.map(tag => CityTag.withName(tag)),
        params = params
      )
    )
  }

  private def name(
    implicit doc: Elem): String = (doc \\ "name").text

  private def wikiDataId(
    implicit doc: Elem): String = (doc \\ "wikiDataId").text

  private def country(
    implicit doc: Elem): String = (doc \\ "country").text

  private def population(
    implicit doc: Elem): String = (doc \\ "population").text

  private def tags(
    implicit doc: Elem): Seq[String] = (doc \\ "tags" \\ "tag").map(_.text).map(_.capitalize)

  private def parameters(
    implicit doc: Elem): NodeSeq = doc \\ "params" \ "_"

  private def parameterName(
    implicit parameter: Node): String = (parameter \\ "@name").text

  private def parameterValue(
    implicit parameter: Node): String = (parameter \\ "name").text

}
