package controllers

import java.util.Date
import java.text.SimpleDateFormat
import javax.inject.{Inject, Singleton}

import play.api._
import play.api.libs.json.{JsValue, JsObject, JsString, Json}
import play.api.mvc._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoComponents, ReactiveMongoApi}
import reactivemongo.play.json._
import play.modules.reactivemongo.json.collection._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.mvc.Http

import scala.concurrent.Future

@Singleton
class Application @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("fpData")

  def index = Action.async {
    Future.successful(Ok(views.html.index("Your new application is ready.")))
  }

  def storeFingerprint = Action.async { request =>
    val result = request.body.asJson.flatMap {
      _.asOpt[JsObject].map { fingerprint =>
        // Add timestamp + HTTP headers to the fingerprint
        val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
        val completeFingerprint = fingerprint ++ Json.obj(
          "date" -> date,
          "userAgentHttp" -> request.headers.get("User-Agent").get,
          "acceptHttp" -> request.headers.get("Accept").get,
          "encodingHttp" -> request.headers.get("Accept-Encoding").get,
          "languageHttp" -> request.headers.get("Accept-Language").get,
          "connectionHttp" -> request.headers.get("Connection").get,
          "headers" -> request.headers.headers.mkString(";")
        )

        // Store into db
        collection.insert(completeFingerprint)
          .map { result =>
            if (result.hasErrors) {
              InternalServerError("Failed to store fingerprint")
            } else {
              Ok("")
            }
          }
      }
    }

    result match {
      case Some(r) => r
      case None => Future.successful(BadRequest(""))
    }
  }

  def fp = Action.async {
    Future.successful(Ok(views.html.fp()))
  }

}