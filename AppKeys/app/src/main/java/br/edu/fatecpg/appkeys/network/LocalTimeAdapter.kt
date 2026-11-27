package br.edu.fatecpg.appkeys.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeAdapter: TypeAdapter<LocalTime>() {
    override fun write(out: JsonWriter?, value: LocalTime?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.format(DateTimeFormatter.ISO_LOCAL_TIME))
        }
    }

    override fun read(reader: JsonReader?): LocalTime? {
        if (reader?.peek() == JsonToken.NULL) {
            reader?.nextNull()
            return null
        }

        return LocalTime.parse(reader?.nextString(), DateTimeFormatter.ISO_LOCAL_TIME)
    }
}