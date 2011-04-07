/*
* User: gregg
* Date: 4/2/11
* Time: 12:03 PM
*/

package com.janxspirit.photoindex.script

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoConnection}
import java.io.File
import java.util.Date
import javax.swing.{JFileChooser, JOptionPane}
import org.apache.sanselan.common.RationalNumber
import org.apache.sanselan.formats.tiff.TiffImageMetadata.Item
import org.apache.sanselan.Sanselan
import scala.collection.JavaConversions._

object Ingest {

  val mongo = MongoConnection("anduin.janxspirit.com", 27017)("phototagger")("photos")

  def main(args: Array[String]) {

    JOptionPane.showMessageDialog(null, "Enter directory to scan")

    val chooser = new JFileChooser()
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    if (chooser.showOpenDialog(chooser) == JFileChooser.CANCEL_OPTION) System.exit(0)

    val dir = chooser.getSelectedFile

    dir.listFiles.foreach(x => {
      println(x);
      println(x.getName.toLowerCase.endsWith("jpg"))
    })

    processDir(dir)
  }

  def processDir(dir: File): Unit = {
    dir.listFiles.foreach(f => f match {
      case dir: File if dir.isDirectory => processDir(dir)
      case f: File => {
        try {
          processFile(f)
        }
        catch {
          case ex => {
            println("problem with file %s - %s" format (f.getAbsolutePath, ex.getStackTraceString))
            val builder = MongoDBObject.newBuilder
            builder += "name" -> f.getName
            builder += "path" -> f.getAbsolutePath
            builder += "dir" -> f.getParent
            builder += "Modify Date" -> new Date(f.lastModified)
            mongo += builder.result
          }
        }
      }
    })
  }

  def processFile(f: File) = f match {
    case file if file.getName.toLowerCase.endsWith("jpg") || file.getName.toLowerCase.endsWith("jpeg") => {
      val metadata = Sanselan.getMetadata(file)
      val items = metadata.getItems
      val builder = MongoDBObject.newBuilder
      builder += "name" -> f.getName
      builder += "path" -> f.getAbsolutePath
      builder += "dir" -> f.getParent
      items.map(x => x.asInstanceOf[Item].getTiffField).foreach(md => {
        if (md.getTagName != "Maker Note") {
          builder += md.getTagName -> (md.getValue match {
            case x: RationalNumber => x.floatValue
            case x => x
          })
        }
      })

      mongo += builder.result
    }
    case _ =>
  }
}