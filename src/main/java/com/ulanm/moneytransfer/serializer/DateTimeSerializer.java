package com.ulanm.moneytransfer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeSerializer extends StdSerializer<TemporalAccessor> {

    public DateTimeSerializer() {
        this(null);
    }

    public DateTimeSerializer(Class<TemporalAccessor> t) {
        super(t);
    }

    @Override
    public void serialize(
            TemporalAccessor temporalAccessor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        jsonGenerator.writeString(df.format(temporalAccessor));
    }
}
