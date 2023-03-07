package com.dedsec.TheDedSec.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailService;

    @Override
    protected void doFilterInternal(
            @NonNull  HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        //Auth Header
        final String authHeader=request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String userName;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        //Header starts with Bearer xxxx , so we start after Bearer
        jwt=authHeader.substring(7);
        userName= jwtService.extractUserName(jwt);// extract the userEmail from token

        if(userName !=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userdetails=this.userDetailService.loadUserByUsername(userName);
            if(jwtService.isTokenValid(jwt,userdetails)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                  userdetails,
                  null,
                  userdetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request,response);
        }


    }
}
