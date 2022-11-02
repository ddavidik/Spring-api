package com.job.task.filter;

import com.job.task.entity.user.User;
import com.job.task.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class XHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static UserRepository userRepository;

    XHeaderAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("X-Authorization");
        Optional<User> user = userRepository.findByAccessToken(accessToken);

        if (user.isEmpty()) {
           response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token invalid.");
        } else {
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.get(), null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
    }
}
