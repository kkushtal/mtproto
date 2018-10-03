package com.mtproto

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.util.ByteString
import scodec._
import bits._

import scala.concurrent.Future
import scala.util.Random

object Main extends App {
  val reqPQ = exampleReqPQRequest()
  val resPQ = exampleReqPQResponse()
  val inDataPQ = exampleInnerDataPQRequest(resPQ)
  //notWorkingTCP()


  def exampleReqPQRequest(): ReqPQRequest = {
    val reqPQ = ReqPQRequest(
      authKeyId = 0,
      messageId = MtProto.nextMessageId,
      messageLength = 20,
      reqPQ = hex"0xbe7e8ef1".reverse,
      nonce = MtProto.nextNonce(size = 16)
    )
    reqPQ.logEncode()
    reqPQ
  }

  def exampleReqPQResponse(): ReqPQResponse = {
    // TODO Заменить муляж ответ на реальный ответ из Akka-Stream
    val hexResPQ: ByteVector = HexExample.fullHex(HexExample.reqPQResponse)
    val resPQ: ReqPQResponse = ReqPQResponse.decoded(hexResPQ)
    ReqPQResponse.logDecoded(hexResPQ)
    resPQ
  }

  def exampleInnerDataPQRequest(resPQ: ReqPQResponse): InnerDataPQRequest = {
    val (decP, decQ) = PQ.decompose(resPQ.pq)
    val innerDataPQ = InnerDataPQRequest(
      pq_inner_data = hex"0x83c95aec".reverse, // ec5ac983
      pq = resPQ.pq,
      p = decP.encode.bytes,
      q = decQ.encode.bytes,
      nonce = resPQ.nonce,
      server_nonce = resPQ.server_nonce ,
      new_nonce = MtProto.nextNonce(32)
    )
    innerDataPQ.printEncode()
    innerDataPQ
  }

  def notWorkingTCP = {
    implicit val system: ActorSystem = ActorSystem("mtprotoSystem")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val connections: Source[IncomingConnection, Future[ServerBinding]] =
      Tcp().bind("149.154.167.40", 443)

    connections runForeach { connection =>
      println(s"New connection from: ${connection.remoteAddress}")

      val hexQeqPQ = reqPQ.encode.toByteArray.toString
      //ByteString(hexQeqPQ)
      val echo = Flow[ByteString]
        .via(Framing.delimiter(
          ByteString("\n"),
          maximumFrameLength = 256,
          allowTruncation = true))
        .map { v => println(hexQeqPQ); v.utf8String }
        .map(ByteString(_))

      connection.handleWith(echo)
    }
  }
}

