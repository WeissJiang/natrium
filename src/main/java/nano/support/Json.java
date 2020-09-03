package nano.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Json {

    static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(Date.class, new DateSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());
        mapper.registerModule(module);
    }

    public static String encode(Object obj) {
        return invoke(() -> mapper.writeValueAsString(obj));
    }

    public static ByteBuffer encodeToBuffer(Object obj) {
        return invoke(() -> ByteBuffer.wrap((mapper.writeValueAsBytes(obj))));
    }

    public static <T> T decodeValue(String str, Class<T> clazz) {
        return invoke(() -> mapper.readValue(str, clazz));
    }

    public static <T> T decodeValue(String str, TypeReference<T> type) {
        return invoke(() -> mapper.readValue(str, type));

    }

    public static <T> T decodeValue(ByteBuffer buf, TypeReference<T> type) {
        return invoke(() -> mapper.readValue(buf.array(), type));
    }

    public static <T> T decodeValue(ByteBuffer buf, Class<T> clazz) {
        return invoke(() -> mapper.readValue(buf.array(), clazz));
    }

    @SneakyThrows
    private static <T> T invoke(ATE<T> actionThrowsException) {
        return actionThrowsException.invoke();
    }

    interface ATE<T> {

        T invoke() throws Exception;
    }

    private static class InstantSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
        }
    }

    private static class DateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(DateTimeFormatter.ISO_INSTANT.format(value.toInstant()));
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {
        private final Base64.Encoder BASE64 = Base64.getEncoder();

        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(BASE64.encodeToString(value));
        }
    }


}
