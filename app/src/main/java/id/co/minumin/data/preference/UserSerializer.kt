package id.co.minumin.data.preference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import id.co.minumin.datastore.UserSetting
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by pertadima on 31,January,2021
 */

object UserSerializer : Serializer<UserSetting> {
    override suspend fun readFrom(input: InputStream): UserSetting {
        try {
            return UserSetting.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Error deserializing proto", exception)
        }
    }

    override suspend fun writeTo(t: UserSetting, output: OutputStream) = t.writeTo(output)

    override val defaultValue: UserSetting
        get() = UserSetting.getDefaultInstance()
}