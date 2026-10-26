package com.bikalpa.nepmart.infra.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Web Push (VAPID) configuration. Symmetric with the existing Cloudinary and
 * mail properties: one nested {@link Vapid} object, three secrets. Only the
 * subject is public; the public/private VAPID keys are server secrets.
 */
@ConfigurationProperties(prefix = "app.webpush")
@Validated
public class WebPushConfig {

    private Vapid vapid = new Vapid();

    public Vapid getVapid() {
        return vapid;
    }

    public void setVapid(Vapid vapid) {
        this.vapid = vapid;
    }

    /**
     * VAPID sign-in details: themailto: address identifies the sender.
     * The public and private keys are used to sign push payloads and must be
     * generated (web-push generate-vapid-key) and set at deploy time.
     */
    public static class Vapid {

        @NotBlank
        private String subject;

        @NotBlank
        private String publicKey;

        @NotBlank
        private String privateKey;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }
}
