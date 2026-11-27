package br.edu.fatecpg.appkeys.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter: TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter?, value: LocalDate?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }

    override fun read(reader: JsonReader?): LocalDate? {
        if (reader?.peek() == JsonToken.NULL) {
            reader?.nextNull()
            return null
        }

        return LocalDate.parse(reader?.nextString(), DateTimeFormatter.ISO_LOCAL_DATE)
    }
}