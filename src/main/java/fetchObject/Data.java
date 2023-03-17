package fetchObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties()
public record Data(String type, JsonIgnoreProperties.Value value) {
}
