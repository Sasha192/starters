package org.wpstarters.jwtauthprovider.service;

import org.wpstarters.jwtauthprovider.dto.CustomUserDetails;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import java.util.function.Predicate;

@FunctionalInterface
public interface IUserEnablementPredicate extends Predicate<CustomUserDetails> {

    @Override
    boolean test(CustomUserDetails userDetails) throws ExtendedAuthenticationException;
}
