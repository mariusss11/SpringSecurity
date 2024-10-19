package com.example.demo.config;

import com.example.demo.service.JWTService;
import com.example.demo.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

//    @Autowired
//    private String secretKey;

//     Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdm5pIiwiaWF0IjoxNzI4ODQ2MDU1LCJleHAiOjE3Mjg4NDYxNjN9.f9-NaJyIY-wShzIB89KFwWbnOXDxAWDi02nAs6rK8HE


    /**
     * Getting the token from the user
     * Check if the toke is valid
     * Extract the info we need
     * And verify with the db
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;

        if (authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        final String username = jwtService.extractUsername(token);

        //                          verify if the user is authenticated, it should not be
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = context
                    .getBean(MyUserDetailsService.class)
                    .loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().
                                buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response); //we pass it to the next filters
    }
}
