package dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Integer id;
    private String title;
    private Integer price;
    private String categoryTitle;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
