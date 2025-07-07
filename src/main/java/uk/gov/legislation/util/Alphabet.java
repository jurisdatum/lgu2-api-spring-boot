package uk.gov.legislation.util;

public enum Alphabet {
    A, B, C, D, E, F, G, H, I, J,
    K, L, M, N, O, P, Q, R, S, T,
    U, V, W, X, Y, Z;

    public static Alphabet from(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Alphabet.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}