package etl.engine.ems.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import etl.engine.ems.model.ResponseSingleDto;
import etl.engine.ems.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/etl-configs/0/0")
@RequiredArgsConstructor
@Slf4j
public class EtlConfigStubController {

    private final ObjectMapper mapper;

    @GetMapping("/data-extract")
    public ResponseEntity<JsonNode> getDataExtractConfig() throws IOException {
        final String PATH = "/etl-configs/data-extract-spec.json";

        InputStream is = getClass().getResourceAsStream(PATH);
        JsonNode specJson = mapper.readTree(is);

        ResponseSingleDto<Object> body = new ResponseSingleDto<>(
                ResponseStatus.ok,
                "Get data-extract specification",
                null
        );

        JsonNode responseJson = mapper.valueToTree(body);
        ((ObjectNode)responseJson).remove("payload");
        ((ObjectNode) responseJson).putIfAbsent("payload", specJson);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(responseJson);
    }

    @GetMapping("/structure-transform")
    public String getStructureTransformationConfig() {
        return "structure-transform";
    }

    @GetMapping("/data-transform")
    public String getDataTransformationConfig() {
        return "data-transform";
    }

    @GetMapping("/data-load")
    public String getDataLoadConfig() {
        return "data-load";
    }

}
