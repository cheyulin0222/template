package com.arplanet.template.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class TimestampToTaipeiISOSerializer extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) {
        try {
            OffsetDateTime offsetDateTime = value.toInstant().atOffset(ZoneOffset.ofHours(8));
            String formattedDate = offsetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
            gen.writeString(formattedDate);
        } catch (Exception e) {
            log.error("Failed to serialize timestamp: {}", value, e);
        }
    }
}
