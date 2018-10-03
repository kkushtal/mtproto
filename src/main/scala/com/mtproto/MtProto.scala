package com.mtproto

import scodec._
import bits._
import codecs._
import scodec.codecs.bytes
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.util.Random


object MtProto {

  def nextNonce(size: Int): ByteVector = ByteVector(Array.fill(size)(nextByte))

  def nextByte: Byte = (Random.nextInt(256) - 128).toByte

  def nextMessageId: Long = 5901257869632771658L // муляж генерации message_id
}

/* ********** REQUEST REQ_PQ ********** */
case class ReqPQRequest(authKeyId: Long,
                        messageId: Long,
                        messageLength: Long,
                        reqPQ: ByteVector,
                        nonce: ByteVector,
                       ) {
  def encode: BitVector = HexEncoder.encoded(this, ReqPQRequest.codec)

  def logEncode(): Unit = HexEncoder.logEncoded(this, ReqPQRequest.codec)
}

object ReqPQRequest extends HexDecoder[ReqPQRequest] {
  val codec: Codec[ReqPQRequest] = {
    (longL(64) :: longL(64) :: longL(32) :: bytes(4) :: bytes(16)).as[ReqPQRequest]
  }
}


/* ********** RESPONSE REQ_PQ ********** */
case class ReqPQResponse(message_size: Byte,
                         auth_key_id: Long,
                         messageId: Long,
                         messageLength: Long,
                         resPQ: ByteVector,
                         nonce: ByteVector,
                         server_nonce: ByteVector,
                         pq: ByteVector,
                         vector_long: ByteVector,
                         count: Long,
                         fingerprints: ByteVector,
                        )

object ReqPQResponse extends HexDecoder[ReqPQResponse] {
  val codec: Codec[ReqPQResponse] = {
    (byte :: longL(64) :: longL(64) :: longL(32) :: bytes(4) :: bytes(16) :: bytes(16) ::
      bytes(12) :: bytes(4) :: longL(32) :: bytes(8)).as[ReqPQResponse]
  }
}


/* ********** REQUEST INNER_DATA_PQ ********** */
case class InnerDataPQRequest(pq_inner_data: ByteVector,
                              pq: ByteVector,
                              p: ByteVector,
                              q: ByteVector,
                              nonce: ByteVector,
                              server_nonce: ByteVector,
                              new_nonce: ByteVector
                      ) extends EncodeRequest {
  def encode: BitVector = HexEncoder.encoded(this, InnerDataPQRequest.codec)

  def printEncode(): Unit = HexEncoder.logEncoded(this, InnerDataPQRequest.codec)
}

object InnerDataPQRequest extends HexDecoder[InnerDataPQRequest] {
  val codec: Codec[InnerDataPQRequest] = {
    (bytes(4) :: bytes(12) :: bytes(8) :: bytes(8) :: bytes(16) :: bytes(16) :: bytes(32)).as[InnerDataPQRequest]
  }
}


/* ********** PQ VALUES ********** */
case class PQ(sizeOfValue: Byte, // single-byte prefix denoting length
              value: Long, // N-byte string (pq: 8-bytes; p: 4-bytes; q: 4-bytes)
              padding: ByteVector = hex"0x000000" //three bytes of padding
             ) extends EncodeRequest {
  def encode: BitVector = HexEncoder.encoded(this, PQ.codec(sizeOfValue), abridgedV = false)

  def printEncode(): Unit = HexEncoder.logEncoded(this, PQ.codec(sizeOfValue), abridgedV = false)
}

object PQ extends LazyLogging {
  def codec(sizeOfValue: Int): Codec[PQ] = (byte :: long(8 * sizeOfValue) :: bytes(3)).as[PQ]

  def decode(hex: ByteVector, sizeOfValue: Int): PQ = {
    val message = codec(sizeOfValue).decode(hex.bits)
      .getOrElse(throw new Exception(s"Decode error [hex=$hex] [codec=${codec(sizeOfValue)}]"))
    message.value
  }

  def decompose(encPQ: ByteVector): (PQ, PQ) = {
    val pqValue: Long = PQ.decode(encPQ, sizeOfValue = 8).value
    val List(pValue, qValue) = PrimeFactors.calculate(pqValue).asScala.toList
    val decP = PQ(value = pValue, sizeOfValue = 4)
    val decQ = PQ(value = qValue, sizeOfValue = 4)
    logger.debug(s"[pqValue=$pqValue] [pValue=$pValue] [qValue=$qValue] [decP=$decP] [decQ=$decQ] [encP=${decP.encode}] [encQ=${decQ.encode}]")
    (decP, decQ)
  }
}


