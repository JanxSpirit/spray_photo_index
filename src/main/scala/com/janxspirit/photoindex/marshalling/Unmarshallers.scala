/*
 * User: gregg
 * Date: 4/4/11
 * Time: 2:13 PM
 */
package com.janxspirit.photoindex.marshalling

import cc.spray.marshalling.UnmarshallerBase
import net.liftweb.json.JsonAST.JValue
import cc.spray.http.{HttpContent, ContentTypeRange}
import cc.spray.http.MediaRanges._
import cc.spray.http.MediaTypes._
import net.liftweb.json.JsonParser._
import com.janxspirit.photoindex.representation.TagCollection
import net.liftweb.json.DefaultFormats
import java.io.{InputStreamReader, StringReader}

trait Unmarshallers {

  implicit val formats = DefaultFormats // Brings in default date formats etc.

  implicit object TagCollectionUnmarshaller extends UnmarshallerBase[TagCollection] {
    val canUnmarshalFrom = ContentTypeRange(`application/json`) :: Nil

    def unmarshal(content: HttpContent) = protect {parse(new InputStreamReader(content.inputStream)).extract[TagCollection]}
  }

}

object Unmarshallers extends Unmarshallers