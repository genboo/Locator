package ru.devsp.app.locator.model.tools

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class ResultTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): T {
                var jsonElement = elementAdapter.read(`in`)
                if (jsonElement.isJsonObject) {
                    val jsonObject = jsonElement.asJsonObject
                    if (jsonObject.has(RESULT_TAG_CARD)) {
                        jsonElement = jsonObject.get(RESULT_TAG_CARD)
                    } else if (jsonObject.has(RESULT_TAG_SETS)) {
                        jsonElement = jsonObject.get(RESULT_TAG_SETS)
                    }
                }
                return delegate.fromJsonTree(jsonElement)
            }
        }.nullSafe()
    }

    companion object {
        const val RESULT_TAG_CARD = "cards"
        const val RESULT_TAG_SETS = "sets"
    }
}