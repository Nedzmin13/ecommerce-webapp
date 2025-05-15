package com.myshop.ecommerce.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input) {
        if (input == null) {
            return ""; // O lanciare eccezione se uno slug non può essere vuoto
        }
        // Rimuove spazi bianchi iniziali/finali
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        // Normalizza caratteri accentati ecc. in ASCII base
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        // Rimuove caratteri non latini (tranne lettere, numeri, trattino)
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        // Rimuove trattini all'inizio o alla fine
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        // Converte in minuscolo
        return slug.toLowerCase(Locale.ENGLISH);
    }
}