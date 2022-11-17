package citiz

import tagged.TaggedType

package object model {

  object FileName extends TaggedType[String]
  type FileName = FileName.Type

  object XmlContent extends TaggedType[String]
  type XmlContent = XmlContent.Type

  object Latitude extends TaggedType[Double]
  type Latitude = Latitude.Type

  object Longitude extends TaggedType[Double]
  type Longitude = Longitude.Type
}
