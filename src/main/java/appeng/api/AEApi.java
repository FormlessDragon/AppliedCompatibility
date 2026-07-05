package appeng.api;

import appeng.core.Api;

public final class AEApi {

    private static final IAppEngApi INSTANCE = Api.INSTANCE;

    private AEApi() {
    }

    public static IAppEngApi instance() {
        return INSTANCE;
    }
}
