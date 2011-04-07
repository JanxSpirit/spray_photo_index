/*
 * User: gregg
 * Date: 4/3/11
 * Time: 11:47 AM
 */
package com.janxspirit.photoindex.builder

import cc.spray.ServiceBuilder
import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId
import com.janxspirit.photoindex.marshalling.Unmarshallers._
import com.janxspirit.photoindex.marshalling.Marshallers._
import com.janxspirit.photoindex.representation.{PhotoCollection, Photo, TagCollection}

trait PhotoIndexBuilder extends ServiceBuilder {

  val mongo = MongoConnection("anduin", 27017)("phototagger")("photos")

  val photoService = {
    pathPrefix("test") {
      getFromDirectory("/tmp")
    } ~
    path("random") {
      get {
        val random = (math.random * 10).toInt.toString
        _.complete(random)
      }
    } ~
    path("photos" / "tags" / ".*".r) {
      id =>
        post {
          contentAs[TagCollection] {
            tagCollection =>
              tagCollection.tagList.foreach(tag => mongo.update(MongoDBObject("_id" -> new ObjectId(id)), $addToSet(("tags", tag))))
              _.complete(mongo.lastError.toString)
          }
        }
    } ~
    path("photos" / "search") {
      get {
        parameter('tag) { tag =>
          val q = MongoDBObject("tags" -> tag)
          val fields = MongoDBObject("path" -> 1)
          val cur = mongo.find(q, fields)
          val finds = PhotoCollection((for (photo <- cur) yield Photo(photo("path").toString)).toList)
          _.complete(finds)
        }
      }
    } ~
    path("photos" / "random") {
      get {
        //declaring a val here to hold either the random or the Mongo result caches that val across requests
        _.complete(PhotoCollection((for (photo <- mongo.find("tags" $exists false).skip(((math.random * (mongo.count("tags" $exists false) -1)).toInt)).limit(1)) yield Photo(photo("path").toString)).toList))
      }
    }
  }

}