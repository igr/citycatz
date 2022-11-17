package tagged

/**
 * Base tagged type trait.
 *
 * @tparam R
 *   raw value type
 */
trait TaggedType[R] {

  /** Tagged value tag. */
  sealed trait Tag

  /** Raw value type. */
  type Raw = R

  /** Tagged value type. */
  type Type = Raw @@ Tag

  /** Create tagged value from raw value. */
  def apply(raw: Raw): Type = raw.@@[Tag]
}
