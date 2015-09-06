package com.equivi.mailsy.service.constant;


public enum dEmailerWebPropertyKey {

    DOMAIN_NAME("domain.name"),
    MAILSY_SERVER_URL("mailsy.server.url"),
    PAGING_MAX_RECORDS("paging.max.records"),
    FORGOT_PASSWORD_EMAIL_SUBJECT("forgot.password.email.subject"),
    PASSWORD_CHANGED_EMAIL_SUBJECT("password.changed.email.subject"),
    NEW_USER_EMAIL_SUBJECT("new.user.email.subject"),
    EMAIL_VERIFIER_IMPORT_LOCATION("email.verifier.import.location"),
    EMAIL_VERIFIER_SERVICE("email.verifier.service"),
    EMAIL_VERIFIER_API_KEY("email.verifier.api.key"),
    EMAIL_VERIFIER_API_TIMEOUT("email.verifier.api.timeout"),
    EMAIL_VERIFIER_URL("email.verifier.url"),
    MAILGUN_WEB_URL("mailgun.web.url"),
    REGISTRY_CHECK_WEB_URL("registry.check.url"),
    REGISTRATION_KEY("mailsy.registration.key"),
    MAILGUN_API_KEY("mailgun.api.key"),
    ADMIN_EMAIL_FROM("admin.email.from"),
    EMAIL_CRAWLING_STORAGE("email.crawling.storage.dir"),
    EMAIL_CONTENT_PARAMS("emailcontent.params"),
    EMAIL_CRAWLING_FILTER("emailcrawling.filter"),
    EMAIL_TEMPLATE_DIR("emailtemplate.dir");


    private String keyName;

    private dEmailerWebPropertyKey(String keyName) {

        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }


}
