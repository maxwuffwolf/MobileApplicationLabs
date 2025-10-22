package com.vovan.lab7.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vovan.lab7.data.entity.TextPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * GeminiAIRepository - the class which includes model and logic how to communicate with it
 * [aiModel] - the AI (Firebase AI Gemini) model
 * [gson] - the parser JSON to data class (TextPair / List<TextPair>)
 * */
class GeminiAIRepository {
    companion object{
        private const val MODEL = "gemini-2.5-flash" // here you set the model in constant (from docs)
        // the example of Prompt to generate JSON of text pairs
        private val PROMPT_TEXT_PAIR_LIST = """
            Generate 5 random text1 to text2 pairs.
            Return ONLY a valid JSON array in this format:
            [
              {"text1": "string", "text2": "string"},
              ...
            ]
        """.trimIndent()

        // you can make several prompts for different requests (e.g. PROMPT_TRIVIA_HISTORY, etc)
        private val PROMPT_TRIVIA_HISTORY = """
            Generate 5 trivia questions with short answers related to world history.
            Return ONLY a valid JSON array in this format:
            [
              {"question": "string", "answer": "string"},
              ...
            ]
        """.trimIndent()
    }

    // initialization of aiModel
    private val aiModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(MODEL)

    // initialization of gson parser
    private val gson = Gson()

    /**
     * generateTextParList(): List<TetPairs>? - the function which makes request
     *          to the AI model to generate abstract text pairs bby using prompt
     * */
    suspend fun generateTextParList(): List<TextPair>? {
        // The request must be surrounded in try-catch to handle the failure
        // For example: if the result is weird and can't be parsed to List<TextPair> it will be properly handled and return Null
        return try {
            // withContext(Dispatchers.IO) will redirect request to non-UI thread (to avoid blocking UI)
            withContext(Dispatchers.IO) {
                // request to model
                val response = aiModel.generateContent(PROMPT_TEXT_PAIR_LIST)

                // get raw response
                val outputRaw = response.text ?: ""

                // clear response to JSON
                val outputJson = outputRaw
                    .replace(Regex("```json|```"), "")
                    .trim()


                // parse JSON text to List<TextPair>
                val type = object : TypeToken<List<TextPair>>() {}.type
                val textPairList: List<TextPair> = gson.fromJson(outputJson,type)

                // log to see the parsed list in LogCat
                Log.i("GeminiAIRepository", "textPairList = $textPairList")

                // return result
                textPairList
            }
        } catch (e: Exception) {
            // log exception if it occurs
            Log.e("GeminiAIRepository", "error - $e")
            // return Null if error occurs
            null
        }
    }
}
