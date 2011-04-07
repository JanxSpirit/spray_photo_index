/*
 * User: gregg
 * Date: 4/4/11
 * Time: 10:03 PM
 */
package com.janxspirit.photoindex.marshalling

import cc.spray.marshalling.MarshallerBase
import cc.spray.http.MediaTypes._
import cc.spray.http.{HttpContent, ContentType}
import net.liftweb.json._
import net.liftweb.json.Serialization._
import com.janxspirit.photoindex.representation.{Photo, PhotoCollection}

trait Marshallers {

  implicit object PhotoCollectionMarshaller extends MarshallerBase[PhotoCollection] {

    implicit val formats = Serialization.formats(NoTypeHints)

    val canMarshalTo = List(ContentType(`application/json`))

    def marshal(value: PhotoCollection, ct: ContentType) = HttpContent(ct, write(value))

  }

  implicit object PhotoMarshaller extends MarshallerBase[Photo] {

    implicit val formats = Serialization.formats(NoTypeHints)

    val canMarshalTo = List(ContentType(`application/json`))

    def marshal(value: Photo, ct: ContentType) = HttpContent(ct, write(value))

  }

}

object Marshallers extends Marshallers