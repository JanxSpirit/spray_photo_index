/*
 * User: gregg
 * Date: 4/3/11
 * Time: 9:05 PM
 */
package com.janxspirit.photoindex.representation

case class Tag(tagname: String)

case class TagCollection(tags: List[Tag]) {
  def tagList = tags.map(_.tagname)
}

case class Photo(path: String)

case class PhotoCollection(photos: List[Photo])