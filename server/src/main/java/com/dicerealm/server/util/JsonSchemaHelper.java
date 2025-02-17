package com.dicerealm.server.util;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSchemaHelper {
	public static void makeAllFieldsRequiredRecursively(ObjectNode schemaNode, ObjectMapper mapper) {
		if (schemaNode.has("properties")) {
				ObjectNode properties = (ObjectNode) schemaNode.get("properties");

				// Create "required" array and add all field names
				ArrayNode requiredArray = mapper.createArrayNode();
				Iterator<String> fieldNames = properties.fieldNames();
				while (fieldNames.hasNext()) {
						String fieldName = fieldNames.next();
						requiredArray.add(fieldName);

						// Recursively apply to nested objects
						JsonNode fieldNode = properties.get(fieldName);
						if (fieldNode.isObject()) {
							ObjectNode fieldObjectNode = (ObjectNode) fieldNode;
							
							// Handle nested objects inside arrays
							if (fieldObjectNode.has("type") && "array".equals(fieldObjectNode.get("type").asText()) 
									&& fieldObjectNode.has("items") && fieldObjectNode.get("items").isObject()) {
									makeAllFieldsRequiredRecursively((ObjectNode) fieldObjectNode.get("items"), mapper);
							}

							// Handle nested objects
							if (fieldObjectNode.has("properties")) {
									makeAllFieldsRequiredRecursively(fieldObjectNode, mapper);
							}
						}
				}
				schemaNode.set("required", requiredArray);
			}
	}

	public static String makeAllFieldsRequired(String jsonSchema) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonSchema);
			makeAllFieldsRequiredRecursively((ObjectNode) rootNode, mapper);
			return mapper.writeValueAsString(rootNode);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonSchema;
		}
	}
}
