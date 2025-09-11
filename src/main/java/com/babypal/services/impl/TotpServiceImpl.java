package com.babypal.services.impl;

import com.babypal.services.TotpService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TotpServiceImpl implements TotpService {

    private final GoogleAuthenticator gAuth;

    public TotpServiceImpl(GoogleAuthenticator gAuth) {
        this.gAuth = gAuth;
    }

    public TotpServiceImpl() {
        this.gAuth = new GoogleAuthenticator();
    }

    @Override
    public GoogleAuthenticatorKey generateSecret(){
        return gAuth.createCredentials();
    }

    @Override
    public String getQrCodeUrl(GoogleAuthenticatorKey secret, String username){
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("Babypal", username, secret);
    }

    @Override
    public boolean verifyCode(String secret, int code){
        return gAuth.authorize(secret, code);
    }
}
