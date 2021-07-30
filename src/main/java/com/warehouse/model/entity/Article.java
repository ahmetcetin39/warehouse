package com.warehouse.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * This is the article entity model which represents a part which can be used to construct a {@link Product}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @JsonProperty("art_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    long id;

    @NotEmpty(message = "name of an article can't be empty!")
    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    Integer stock;
}
