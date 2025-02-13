package Capstone.Users.bo.Register.Request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RegisterRequestDeserializer extends JsonDeserializer<RegisterRequest> {

    @Override
    public RegisterRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        if (root.has("civilId")) {
            return mapper.treeToValue(root, RegisterPersonalRequest.class);
        } else if (root.has("businessId") && !root.has("bankAccount")) {
            return mapper.treeToValue(root, RegisterAssociateRequest.class);
        } else if (root.has("businessId") && root.has("bankAccount")) {
            return mapper.treeToValue(root, RegisterBusinessRequest.class);
        } else if (root.has("fullName") && !root.has("businessId")) {
            return mapper.treeToValue(root, RegisterAdminRequest.class);
        } else {
            throw new IllegalArgumentException("Unknown request type");
        }
    }
}
