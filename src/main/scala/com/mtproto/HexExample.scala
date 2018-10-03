package com.mtproto

import scodec._
import bits._

object HexExample {
  def fullHex(seq: Map[String, ByteVector]): ByteVector = seq.values.reduce(_ ++ _)

  val reqPQRequest = Map(
    "auth_key_id" -> ByteVector.fromValidHex("0000000000000000"), //hex"0x0000000000000000",
    "message_id" -> hex"0x4a967027c47ae551",
    "message_length" -> ByteVector.fromValidHex("14000000"), //hex"14000000",
    "req_pq" -> hex"0x78974660",
    "req_pq_multi" -> hex"0xf18e7ebe",
    "nonce" -> hex"0x3e0549828cca27e966b301a48fece2fc"
  )

  val reqPQResponse = Map(
    "message_size" -> hex"0x17",
    "auth_key_id" -> ByteVector.fromValidHex("0000000000000000"), //hex"0x0000000000000000",
    "message_id" -> hex"0x015c1ca1a869b25b",
    "message_length" -> ByteVector.fromValidHex("48000000"), //hex"14000000",
    "resPQ" -> hex"0x63241605",
    "nonce" -> hex"0x3e0549828cca27e966b301a48fece2fc",
    "server_nonce" -> hex"0x85a8c768432fd75ac13db684fdb01cc3",
    "pq" -> hex"0x081c23e37aed15428d000000",
    "vector long" -> hex"0x15c4b51c",
    "count" -> hex"0x02000000",
    "fingerprints[]" -> hex"0x029F4BA16D109296216BE86C022BB4C3"
  )


}
