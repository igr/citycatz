package citiz.model

import parser.{CityName, Country, Population}

case class GetCityData(data: GeoCityDetails)

// todo add tagged types for remaining fields
case class GeoCityDetails(name: CityName,
                          country: Country,
                          countryCode: String,
                          region: String,
                          regionCode: String,
                          elevationMeters: Option[Int],
                          latitude: Latitude,
                          longitude: Longitude,
                          population: Population,
                          timezone: String,
                          deleted: Boolean)
