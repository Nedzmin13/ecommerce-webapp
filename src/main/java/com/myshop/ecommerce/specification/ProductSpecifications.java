package com.myshop.ecommerce.specification;

import com.myshop.ecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component; // Opzionale, ma utile se vuoi iniettarla
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate; // Importa Predicate
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List; // Importa List

@Component // Rende questa classe un bean, se necessario per iniezione (non strettamente per metodi statici)
public class ProductSpecifications {

    // Specifica per cercare per keyword nel nome o nella descrizione
    public static Specification<Product> keywordSearch(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction(); // Nessun filtro se la keyword è vuota (restituisce tutto)
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
            Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
            return criteriaBuilder.or(namePredicate, descriptionPredicate);
        };
    }

    // Specifica per filtrare per ID categoria
    public static Specification<Product> categoryFilter(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction(); // Nessun filtro se categoryId è nullo
            }
            // Unisciti all'entità Category e filtra per il suo ID
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    // Specifica per filtrare per prezzo minimo
    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction(); // Nessun filtro se minPrice è nullo
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    // Specifica per filtrare per prezzo massimo
    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction(); // Nessun filtro se maxPrice è nullo
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    /**
     * Metodo di utilità per combinare più specifiche con un AND logico.
     * @param keyword La keyword per la ricerca (può essere null o vuota).
     * @param categoryId L'ID della categoria (può essere null).
     * @param minPrice Il prezzo minimo (può essere null).
     * @param maxPrice Il prezzo massimo (può essere null).
     * @return Una Specification<Product> combinata.
     */
    public static Specification<Product> buildProductSpecification(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Specification<Product>> specs = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            specs.add(keywordSearch(keyword));
        }
        if (categoryId != null) {
            specs.add(categoryFilter(categoryId));
        }
        if (minPrice != null) {
            specs.add(priceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            specs.add(priceLessThanOrEqual(maxPrice));
        }

        if (specs.isEmpty()) {
            return Specification.where(null); // Nessun filtro, restituisce tutto
        }

        // Combina tutte le specifiche con AND
        Specification<Product> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}