package etl.engine.worker;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import etl.engine.worker.exception.InvalidNodeTypeException;
import etl.engine.worker.exception.InvalidNodeValueException;
import etl.engine.worker.exception.MissingNodeException;

import java.time.OffsetDateTime;
import java.util.UUID;

public class JsonUtils {

    public static String readValueAsString(JsonNode document, String path)
            throws MissingNodeException, InvalidNodeTypeException {
        JsonNode node = document.at(JsonPointer.compile(path));
        if (node instanceof NullNode) {
            return null;
        }
        if (node instanceof MissingNode) {
            throw MissingNodeException.forPath(path);
        }
        if (node instanceof TextNode) {
            return document.at(JsonPointer.compile(path)).asText();
        } else {
            throw InvalidNodeTypeException.forPathAndType(path, "text");
        }
    }

    public static UUID readValueAsUUID(JsonNode document, String path)
            throws MissingNodeException, InvalidNodeTypeException, InvalidNodeValueException {
        JsonNode node = document.at(JsonPointer.compile(path));
        if (node instanceof NullNode) {
            throw new InvalidNodeValueException("The value for UUID cannot be NULL in the path = '" + path + "'!");
        }
        if (node instanceof MissingNode) {
            throw MissingNodeException.forPath(path);
        }
        if (node instanceof TextNode) {
            String value = document.at(JsonPointer.compile(path)).asText();
            try {
                return UUID.fromString(value);
            } catch (IllegalArgumentException e) {
                throw new InvalidNodeValueException("The value '" + value + "' cannot be read as UUID due to '" + e.getMessage() + "' error!");
            }
        } else {
            throw InvalidNodeTypeException.forPathAndType(path, "text");
        }
    }

    public static long readValueAsLong(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asLong();
    }

    public static int readValueAsInt(JsonNode document, String path) {
        return document.at(JsonPointer.compile(path)).asInt();
    }

    public static OffsetDateTime readValueAsOffsetDateTime(JsonNode document, String path) {
        String value = document.at(JsonPointer.compile(path)).asText();
        return OffsetDateTime.parse(value);
    }

    public static JsonNode getNode(JsonNode document, String path) throws MissingNodeException {
        JsonNode node = document.at(JsonPointer.compile(path));
        if (node instanceof NullNode) {
            return null;
        }
        if (node instanceof MissingNode) {
            throw MissingNodeException.forPath(path);
        }
        return node;
    }

}
