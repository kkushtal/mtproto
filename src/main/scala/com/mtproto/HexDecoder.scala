package com.mtproto

import scodec._
import bits._
import codecs._
import com.typesafe.scalalogging.LazyLogging

trait EncodeRequest {
  def encode: BitVector
  def printEncode(): Unit
}

object HexEncoder extends LazyLogging {
  def encoded[T](value: T, codec: Codec[T], abridgedV: Boolean = true): BitVector = {
    val encValue = codec.encode(value)
      .getOrElse(throw new Exception(s"Decode error [value=$value] [codec=$codec]"))
    encValue
    if (abridgedV) addAbridgedBytes(encValue) else encValue
  }

  def logEncoded[T](value: T, codec: Codec[T], abridgedV: Boolean = true): Unit = {
    logger.debug(s"[encoded=${encoded(value, codec)}] [value=$value] [codec=$codec]")
  }

  def addAbridgedBytes(encMessage: BitVector): BitVector = {
    val messageLength = (encMessage.bytes.length / 4).toByte
    val encFirstByte = hex"0xEF".bits
    val encMessageLength = HexEncoder.encoded(messageLength, byte.as[Byte], abridgedV = false)
    println(s"[messageLength=$messageLength] [encFirstByte=$encFirstByte] [encMessageLength=$encMessageLength]")
    encFirstByte ++ encMessageLength ++ encMessage
  }
}

trait HexDecoder[T] extends LazyLogging {
  val codec: Codec[T]
  def decoded(hex: ByteVector): T = codec.decode(hex.bits).map(_.value)
    .getOrElse(throw new Exception(s"Decode error [hex=$hex] [codec=$codec]"))

  def logDecoded(hex: ByteVector): Unit = {
    logger.debug(s"[decoded=${decoded(hex)}] [hex=$hex] [codec=$codec]")

  }
}
