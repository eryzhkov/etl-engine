package etl.engine.worker.service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class CustomJsonSerializer extends JsonSerializer<Object> {
    public static final ObjectMapper objectMapper = customizedObjectMapper();

    public CustomJsonSerializer() {
        super(objectMapper);
    }

    public static ObjectMapper customizedObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

}
