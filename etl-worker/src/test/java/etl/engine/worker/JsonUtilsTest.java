package etl.engine.worker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import etl.engine.worker.exception.InvalidNodeTypeException;
import etl.engine.worker.exception.InvalidNodeValueException;
import etl.engine.worker.exception.MissingNodeException;
import etl.engine.worker.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class JsonUtilsTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    void readNonEmptyTextNodeAsString_ValueReturned() throws Exception {
        final String json = """
                {
                  "key": "value"
                }
                """;
        String value = JsonUtils.readValueAsString(
                mapper.readTree(json),
                "/key");
        assertEquals("value", value);
    }

    @Test
    void readEmptyTextNodeAsString_EmptyValueReturned() throws Exception {
        final String json = """
                {
                  "key": ""
                }
                """;
        String value = JsonUtils.readValueAsString(
                mapper.readTree(json),
                "/key");
        assertEquals("", value);
    }

    @Test
    void readNullTextNodeAsString_NullReturned() throws Exception {
        final String json = """
                {
                  "key": null
                }
                """;
        String value = JsonUtils.readValueAsString(
                mapper.readTree(json),
                "/key");
        assertNull(value);
    }

    @Test
    void readUnknownTextNodeAsString_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": "value"
                }
                """;
        assertThrowsExactly(MissingNodeException.class, () -> {
            JsonUtils.readValueAsString(
                    mapper.readTree(json),
                    "/unknownKey");
        });
    }

    @Test
    void readNumberTextNodeAsString_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": 123
                }
                """;
        assertThrowsExactly(InvalidNodeTypeException.class, () -> {
            JsonUtils.readValueAsString(
                    mapper.readTree(json),
                    "/key");
        });
    }

    @Test
    void readTextWithValidValueNodeAsUUID_UuidReturned() throws Exception {
        final UUID expectedValue = UUID.fromString("ea162f97-dff9-4ff2-a96d-19dcdfb861b9");
        final String json = """
                {
                  "key": "ea162f97-dff9-4ff2-a96d-19dcdfb861b9"
                }
                """;
        final UUID value = JsonUtils.readValueAsUUID(
                mapper.readTree(json),
                "/key");
        assertEquals(expectedValue, value);
    }

    @Test
    void readEmptyTextNodeAsUUID_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": ""
                }
                """;
        assertThrowsExactly(InvalidNodeValueException.class, () -> {
            JsonUtils.readValueAsUUID(
                    mapper.readTree(json),
                    "/key");
        });
    }

    @Test
    void readNullTextNodeAsUUID_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": null
                }
                """;
        assertThrowsExactly(InvalidNodeValueException.class, () -> {
            JsonUtils.readValueAsUUID(
                    mapper.readTree(json),
                    "/key");
        });
    }

    @Test
    void readNumberNodeAsUUID_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": 123
                }
                """;
        assertThrowsExactly(InvalidNodeTypeException.class, () -> {
            JsonUtils.readValueAsUUID(
                    mapper.readTree(json),
                    "/key");
        });
    }

    @Test
    void readUnknownNodeAsUUID_exceptionThrown() throws Exception {
        final String json = """
                {
                  "key": 123
                }
                """;
        assertThrowsExactly(MissingNodeException.class, () -> {
            JsonUtils.readValueAsUUID(
                    mapper.readTree(json),
                    "/unknownKey");
        });
    }

    @Test
    void getExistingNode() throws Exception {
        final String json = """
                {
                  "key": {}
                }
                """;
        JsonNode node = JsonUtils.getNode(mapper.readTree(json), "/key");
        assertNotNull(node);
    }

    @Test
    void getNonExistingNode() throws Exception {
        final String json = """
                {
                  "key": {}
                }
                """;
        assertThrowsExactly(MissingNodeException.class, () -> {
            JsonUtils.getNode(
                    mapper.readTree(json),
                    "/unknownKey");
        });
    }

    @Test
    void isNodeMissing() throws Exception {
        final String json = """
                {
                  "key": {}
                }
                """;
        assertTrue(JsonUtils.isNodeMissing(
                mapper.readTree(json),
                "/unknownKey"
        ));
    }

    @Test
    void isNodeEmpty() throws Exception {
        final String json = """
                {
                  "key": {}
                }
                """;
        assertTrue(JsonUtils.isNodeEmpty(
                mapper.readTree(json),
                "/key"
        ));
    }

    @Test
    void isNodeEmpty_forNonEmpty() throws Exception {
        final String json = """
                {
                  "key": {
                    "id": 1
                  }
                }
                """;
        assertFalse(JsonUtils.isNodeEmpty(
                mapper.readTree(json),
                "/key"
        ));
    }

}