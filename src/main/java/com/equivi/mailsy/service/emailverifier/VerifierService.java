package com.equivi.mailsy.service.emailverifier;


import java.util.List;

public interface VerifierService {

    /**
     *
     * @param emailList
     * @return List of EmailVerifierResponse
     */
    List<EmailVerifierResponse> filterValidEmail(List<String> emailList);

    /**
     *
     * @param emailAddress
     * @return EmailVerifierResponse
     */
    EmailVerifierResponse getEmailAddressStatus(String emailAddress);
}
