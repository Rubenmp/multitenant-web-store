package com.mws.back_end.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.mws.back_end.framework.dto.WebResult;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
    private static ObjectMapper objectMapper;

    protected static final Long TENANT_ID = 1L;

    private static final Random rand = new Random();

    protected static Long getRandomLong() {
        return rand.nextLong();
    }


    protected WebResult<Serializable> toWebResult(final ResponseEntity<String> responseEntity) {
        return toWebResult(responseEntity, Serializable.class);
    }

    protected <E extends Serializable> WebResult<E> toWebResult(final ResponseEntity<String> response, final Class<E> dataClass) {
        WebResult<E> result = convertStringToObject(response.getBody(), WebResult.class);
        if (result.getData() instanceof Map) {
            final String dataJson = toJson((Map<String, Object>) result.getData());
            E data = convertStringToObject(dataJson, dataClass);
            result.setData(data);
        } else if (result.getData() != null) {
            E data = convertStringToObject(result.getData().toString(), dataClass);
            result.setData(data);
        }

        return result;
    }

    protected <E extends Serializable> WebResult<ArrayList<E>> toWebResultWithList(final ResponseEntity<String> response, final Class<E> dataClass) {
        WebResult<ArrayList<E>> result = convertStringToObject(response.getBody(), WebResult.class);
        if (result.getData() != null) {
            ArrayList<E> actualData = (ArrayList<E>) ((List<?>) result.getData()).stream().map(t -> (Map<String, Object>) t).map(this::toJson)
                    .map(json -> convertStringToObject(json, dataClass)).collect(Collectors.toList());

            result.setData(actualData);
        }

        return result;
    }

    private String toJson(final Map<String, Object> properties) {
        return toJsonInternal(properties, 0);
    }

    private String toJsonInternal(final Map<String, Object> properties, final int depth) {
        if (depth > 10) {
            fail("Max depth serializing object.");
        }
        int i = 0;
        final int mapSize = properties.size();

        final StringBuilder json = new StringBuilder("{");
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");

            if (entry.getValue() != null) {
                if (entry.getValue() instanceof Map) {
                    json.append(toJsonInternal((Map<String, Object>) entry.getValue(), depth + 1));
                } else {
                    json.append("\"").append(entry.getValue().toString()).append("\"");
                }
            } else {
                json.append("null");
            }

            if (i < (mapSize - 1)) {
                json.append(",");
            }
            i++;
        }
        json.append("}");

        return json.toString();
    }

    protected String toJson(final Object objectToConvert) {
        String convertedObject = "{}";
        try {
            convertedObject = getObjectMapper().writeValueAsString(objectToConvert);
        } catch (JsonProcessingException ignored) {
            fail("toJson method failed");
        }

        return convertedObject;
    }

    // Important: We need @NoArgsConstructor in valueType class
    protected <T> T convertStringToObject(final String data, final Class<T> valueType) {
        final ObjectMapper mapper = new ObjectMapper();
        T object = null;

        try {
            object = mapper.readValue(data, valueType);
        } catch (JsonProcessingException e) {
            fail("It was not possible to parse object" + e);
        }

        return object;
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }

        objectMapper = new ObjectMapper();
        configureObjectMapperDateTimeFormatterToISO(objectMapper);
        return objectMapper;
    }

    private static void configureObjectMapperDateTimeFormatterToISO(final ObjectMapper objectMapper) {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));

        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

}
