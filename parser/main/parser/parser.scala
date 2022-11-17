package object parser {
  import tagged.TaggedType

  object CityName extends TaggedType[String]
  type CityName = CityName.Type

  object WikiDataId extends TaggedType[String]
  type WikiDataId = WikiDataId.Type

  object Country extends TaggedType[String]
  type Country = Country.Type

  object Population extends TaggedType[Int]
  type Population = Population.Type

  object ParserError extends TaggedType[String]
  type ParserError = ParserError.Type

  type ParamsMap = Map[String, String]
}
