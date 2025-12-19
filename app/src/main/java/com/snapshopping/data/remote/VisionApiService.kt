package com.snapshopping.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.snapshopping.data.model.DetectedFood
import com.snapshopping.data.model.VisionResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionApiService @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    private val gson = Gson()

    companion object {
        /**
         * Prompt sent to Gemini Vision API for food detection
         */
        const val VISION_PROMPT = """
Analyze the image of a refrigerator.
List only visible food items.
Ignore containers, plates, and non-food objects.
Use generic food names in singular form.
Return a JSON object with an "items" array containing objects with:
- name: string (generic food name)
- category: string (one of: dairy, meat, vegetables, fruits, beverages, condiments, leftovers, snacks, frozen, other)
- confidence: number (0-1)

Example response format:
{"items":[{"name":"milk","category":"dairy","confidence":0.95}]}

Return ONLY valid JSON, no explanations or markdown.
"""
    }

    /**
     * Analyze images and detect food items using Gemini Vision
     * @param images List of compressed bitmap images to analyze
     * @return Result containing list of detected foods or error
     */
    suspend fun analyzeImages(images: List<Bitmap>): Result<List<DetectedFood>> {
        return try {
            val inputContent = content {
                images.forEach { bitmap ->
                    image(bitmap)
                }
                text(VISION_PROMPT)
            }

            val response = generativeModel.generateContent(inputContent)
            val responseText = response.text ?: return Result.failure(
                Exception("Empty response from Vision API")
            )

            // Clean the response (remove markdown code blocks if present)
            val cleanedJson = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            parseVisionResponse(cleanedJson)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parse JSON response from Vision API
     */
    private fun parseVisionResponse(json: String): Result<List<DetectedFood>> {
        return try {
            val response = gson.fromJson(json, VisionResponse::class.java)
            Result.success(response.items)
        } catch (e: JsonSyntaxException) {
            // Try to extract JSON if it's wrapped in other text
            val jsonPattern = """\{.*"items".*\}""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = jsonPattern.find(json)
            if (match != null) {
                try {
                    val response = gson.fromJson(match.value, VisionResponse::class.java)
                    Result.success(response.items)
                } catch (e2: Exception) {
                    Result.failure(Exception("Failed to parse Vision API response: ${e2.message}"))
                }
            } else {
                Result.failure(Exception("Invalid JSON response from Vision API"))
            }
        }
    }
}
