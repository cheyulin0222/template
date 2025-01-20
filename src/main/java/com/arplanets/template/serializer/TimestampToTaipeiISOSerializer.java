package com.arplanets.template.serializer;

import com.arplanets.template.log.Logger;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.arplanets.template.exception.ErrorType.SYSTEM;
import static com.arplanets.template.log.enums.BasicActionType.SERIALIZE_DATA;

public class TimestampToTaipeiISOSerializer extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) {
        try {
            OffsetDateTime offsetDateTime = value.toInstant().atOffset(ZoneOffset.ofHours(8));
            String formattedDate = offsetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
            gen.writeString(formattedDate);
        } catch (Exception e) {
            Map<String, Object> context = new HashMap<>();
            context.put("raw_value", value);
            Logger.error("Failed to serialize timestamp", SERIALIZE_DATA, SYSTEM, context);
        }
    }
}
