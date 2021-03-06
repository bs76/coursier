package coursier.launcher

import dataclass.data

import scala.collection.mutable

@data class ClassLoaderContent(
  entries: Seq[ClassPathEntry],
  loaderName: String = ""
)

object ClassLoaderContent {

  def fromUrls(urls: Seq[String]): ClassLoaderContent =
    ClassLoaderContent(urls.map(ClassPathEntry.Url(_)))

  def withUniqueFileNames(content: Seq[ClassLoaderContent]): Seq[ClassLoaderContent] = {

    val seen = new mutable.HashMap[String, Int]

    content.map { c =>
      c.withEntries(
        c.entries.map {
          case r: ClassPathEntry.Resource =>
            val n = seen.getOrElse(r.fileName, 0)
            seen(r.fileName) = n + 1
            if (n == 0)
              r
            else {
              val extIdx = r.fileName.lastIndexOf('.')
              val fileName0 =
                if (extIdx < 0)
                  s"${r.fileName}-$n"
                else
                  s"${r.fileName.take(extIdx)}-$n.${r.fileName.drop(extIdx + 1)}"

              r.withFileName(fileName0)
            }
          case e =>
            e
        }
      )
    }
  }

}
