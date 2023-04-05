package com.infomedia.hikvisiondemo.util;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

@Log4j2
public class ApiKeyFilter extends OncePerRequestFilter {

    @Setter
    private Validator validator;

    public ApiKeyFilter(Validator validator) {
        this.validator = validator;
    }

    public ApiKeyFilter() {
        validator = apiKey -> new ValidationResult(true, apiKey);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            authorizeRequest(request);
        }catch (Exception e){
            log.error(e);
        }
        filterChain.doFilter(request, response);
    }

    private void authorizeRequest(HttpServletRequest servletRequest) {

        String apiKey = getApiKey(servletRequest);

        if(!apiKey.isBlank()){
            ValidationResult validationResult = validator.validate(apiKey);
            if(validationResult.isValid()){
                log.info("Using Api Key: "+apiKey);
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(validationResult.getPrincipal()
                        , null, new HashSet<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    private String getApiKey(HttpServletRequest request){
        String apiKey = request.getHeader("X-Api-Key");
        if(apiKey!=null){
            return apiKey;
        }
        return "";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    @Data
    public static class ValidationResult{
        private boolean valid;
        private Object principal;
    }

    public interface Validator{
        ValidationResult validate(String apiKey);
    }
}