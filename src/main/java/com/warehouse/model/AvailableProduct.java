package com.warehouse.model;

import com.warehouse.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This model represent a product which is available including how many product available with the existing inventory.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProduct extends ProductBase {
    Product product;
    int count;
}
