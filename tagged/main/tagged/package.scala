package object tagged {

  type @@[+T, +U] = T & Tagged[T, U]

  sealed trait Auto

  object auto {

    implicit def auto: Auto = null

    object typeclass {

      implicit def liftAnyTypeclass[Typeclass[_], T, Tag](implicit tc: Typeclass[T]): Typeclass[T @@ Tag] =
        tc.asInstanceOf[Typeclass[T @@ Tag]]

    }

  }

  /** Function-first-style tagging API.
   *
   * @tparam U type to tag with
   * @return `Tagger` instance that can be used for tagging */
  def apply[T, U](t: T): T @@ U = cast(t)

  implicit class TaggingExtensions[T](val t: T) extends AnyVal {

    /**
     * Tag with type `U`.
     *
     * @tparam U
     *   type to tag with
     * @return
     *   value tagged with `U`
     */
    def taggedWith[U]: T @@ U = t.asInstanceOf[T @@ U]

    /** Synonym operator for `taggedWith`. */
    def @@ [U]: T @@ U = taggedWith[U]
  }

  implicit class AndTaggingExtensions[T, U](val t: T @@ U) extends AnyVal {

    /**
     * Tag tagged value with type `V`.
     *
     * @tparam V
     *   type to tag with
     * @return
     *   value tagged with both `U` and `V`
     */
    def andTaggedWith[V]: T @@ (U & V) = t.asInstanceOf[T @@ (U & V)]

    /** Synonym operator for `andTaggedWith`. */
    def +@ [V]: T @@ (U & V) = andTaggedWith[V]

    def map[V](f: T => V): V @@ U = f(t).taggedWith[U]
  }

  implicit class TaggingExtensionsF[F[_], T](val ft: F[T]) extends AnyVal {

    /**
     * Tag intra-container values with type `U`.
     *
     * @tparam U
     *   type to tag with
     * @return
     *   container with nested values tagged with `U`
     */
    def taggedWithF[U]: F[T @@ U] = ft.asInstanceOf[F[T @@ U]]

    /** Synonym operator for `taggedWithF`. */
    def @@@ [U]: F[T @@ U] = taggedWithF[U]
  }

  implicit class AndTaggingExtensionsF[F[_], T, U](val ft: F[T @@ U]) extends AnyVal {

    /**
     * Tag tagged intra-container values with type `U`.
     *
     * @tparam V
     *   type to tag with
     * @return
     *   container with nested values tagged with both `U` and `V`
     */
    def andTaggedWithF[V]: F[T @@ (U & V)] = ft.asInstanceOf[F[T @@ (U & V)]]

    /** Synonym operator for `andTaggedWithF`. */
    def +@@ [V]: F[T @@ (U & V)] = andTaggedWithF[V]
  }

  @inline
  private def cast[T, V](v: T): V = v.asInstanceOf[V]
}
