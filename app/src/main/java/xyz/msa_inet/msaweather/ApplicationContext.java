package xyz.msa_inet.msaweather;

/**
 * Created by moiseev on 24.07.2017.
 */

public class ApplicationContext extends android.app.Application{
//    @NotNull
    private static ApplicationContext instance;

    public ApplicationContext() {
        instance = this;
    }

//    @NotNull
    public static ApplicationContext getMyContext() {
        return instance;
    }
}
