package com.example.mobilki.data.type_converters

import androidx.room.TypeConverter
import org.json.JSONObject
import java.sql.Timestamp

class TimeStampTypeConverter {
    @TypeConverter
    fun fromSource(source: Timestamp): String {
        return JSONObject().apply {
            put("time", source.time)
        }.toString()
    }

    @TypeConverter
    fun toSource(source: String): Timestamp {
        val json = JSONObject(source)
        return Timestamp(json.get("time") as Long)
    }
}