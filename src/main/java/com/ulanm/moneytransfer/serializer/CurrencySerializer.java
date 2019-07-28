package com.ulanm.moneytransfer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Currency;

public class CurrencySerializer extends StdSerializer<Currency> {

    public CurrencySerializer() {
        this(null);
    }

    public CurrencySerializer(Class<Currency> t) {
        super(t);
    }

    @Override
    public void serialize(
            Currency currency, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(currency.getCurrencyCode());
    }

}
