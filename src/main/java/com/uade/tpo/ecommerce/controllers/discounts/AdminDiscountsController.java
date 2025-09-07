package com.uade.tpo.ecommerce.controllers.discounts;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.service.DiscountAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("admin/discounts")
public class AdminDiscountsController {

    @Autowired
    private DiscountAdminService discountAdminService;

    @GetMapping
    public ResponseEntity<List<Discount>> list() {
        return ResponseEntity.ok(discountAdminService.getDiscounts());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody DiscountRequest request)
            throws DiscountDuplicateException {

        Discount created = discountAdminService.create(request);
        return ResponseEntity
                .created(URI.create("/admin/discounts/" + created.getId()))
                .body(created);
    }
}
