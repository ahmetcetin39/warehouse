package com.warehouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.warehouse.model.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * This is the model wrapping a list of articles which matches with the
 * JSON file which is used to upload new articles into the inventory.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Articles {
    @NotEmpty(message = "articles can't be empty!")
    @JsonProperty("inventory")
    List<Article> articles;
}
