package fetchObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties()
public record Data(String type, JsonIgnoreProperties.Value value) {
}
