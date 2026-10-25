package za.ac.cput.univentbackend.utilTest;

import za.ac.cput.domain.User;
import za.ac.cput.util.JwtUtil;

public final class TestJwtTokenFactory {
    private TestJwtTokenFactory() {
    }

    public static String tokenFor(JwtUtil jwtUtil, User user) {
        return jwtUtil.generateToken(user);
    }
}
