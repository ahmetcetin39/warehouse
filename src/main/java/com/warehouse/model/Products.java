package com.warehouse.model;

import com.warehouse.model.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is the model wrapping a list of products which matches with the
 * JSON file which is used to upload new projects into the warehouse.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Data
@NoArgsConstructor
public class Products {
    List<Product> products;
}
