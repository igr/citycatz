package parser

import enumeratum.*

case class CityData(name: CityName,
                    wikiDataId: WikiDataId,
                    country: Country,
                    population: Population,
                    tags: Seq[CityTag],
                    params: ParamsMap)

sealed trait CityTag extends EnumEntry
object CityTag extends Enum[CityTag] {
  override val values: IndexedSeq[CityTag] = findValues

  case object Cool extends CityTag
  case object Hip extends CityTag
  case object Tech extends CityTag
  case object Busy extends CityTag
  case object Noisy extends CityTag
  case object Old extends CityTag

}
