package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.AuthProvider;
import com.myshop.ecommerce.security.CustomOAuth2User;
import com.myshop.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info(">>> CustomOAuth2UserService: loadUser TRIGGERED <<<");
        OAuth2User oauth2User = super.loadUser(userRequest);
        log.debug("OAuth2User attributes: {}", oauth2User.getAttributes());

        String providerRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider authProvider = AuthProvider.valueOf(providerRegistrationId.toUpperCase());

        Map<String, Object> attributes = oauth2User.getAttributes();
        String providerSpecificId = attributes.get("sub").toString();
        String email = attributes.get("email").toString();
        String name = attributes.get("name") != null ? attributes.get("name").toString() : email;
        String firstName = attributes.get("given_name") != null ? attributes.get("given_name").toString() : "";
        String lastName = attributes.get("family_name") != null ? attributes.get("family_name").toString() : "";

        User user = userService.processOAuthPostLogin(
                email,
                name,
                providerSpecificId,
                authProvider,
                firstName,
                lastName
        );


        return new CustomOAuth2User(oauth2User, user);
    }
}