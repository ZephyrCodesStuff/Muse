package com.zeph.muse.classes

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.muse.Songs
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SongsSerializer : Serializer<Songs> {
    override val defaultValue: Songs = Songs.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Songs {
        try {
            return Songs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    override suspend fun writeTo(t: Songs, output: OutputStream) = t.writeTo(output)
}
