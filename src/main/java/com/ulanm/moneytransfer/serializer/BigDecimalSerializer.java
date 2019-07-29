package com.ulanm.moneytransfer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;

public class BigDecimalSerializer extends StdSerializer<BigDecimal> {

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public BigDecimalSerializer() {
        this(null);
    }

    public BigDecimalSerializer(Class<BigDecimal> t) {
        super(t);
    }

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(df.format(bigDecimal));
    }

}
