package citiz.model

import parser.Population

case class GetCityData(data: GeoCityDetails)

case class GeoCityDetails(name: String,
                          country: String,
                          countryCode: String,
                          region: String,
                          regionCode: String,
                          elevationMeters: Option[Int],
                          latitude: Double,
                          longitude: Double,
                          population: Population,
                          timezone: String,
                          deleted: Boolean)
