package com.warehouse.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warehouse.model.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a part to construct a product which contains
 * the {@link Article} id and count to construct a specific product.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    @JsonProperty("art_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    long id;

    @JsonProperty("amount_of")
    int count;
}
