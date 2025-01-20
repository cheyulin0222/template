package com.arplanets.template.log.enums;

public enum JwtActionType implements ActionType {

    INIT_PUBLIC_KEY("jwt.public.key.init"),
    EXTRACT_PAYLOAD_WITHOUT_VERIFICATION("jwt.payload.extract.without.verification"),
    DECODE_PAYLOAD("jwt.payload.decode"),
    PARSE_PAYLOAD("jwt.payload.parse"),
    GENERATE_TOKEN("jwt.token.generate"),
    LOAD_PRIVATE_KEY("jwt.private.key.load"),
    VERIFY_JWT("jwt.token.verify");

    private final String action;

    JwtActionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
