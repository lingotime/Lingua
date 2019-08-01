package com.lingua.lingua.notifyAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class TwilioFunctionsAPI {
    // The URL below should be the domain for your Twilio Functions, without the trailing slash:
    // Example: https://sturdy-concrete-1234.twil.io
    public final static String BASE_SERVER_URL = "https://manatee-bloodhound-4936.twil.io";

    /**
     * A resource defined to register Notify bindings using the Twilio Notify Quickstart Template
     */
    interface FunctionsService {
        @POST("/register-binding")
        Call<CreateBindingResponse> register(@Body Binding binding);

        // Send notifications to Twilio Notify registrants
        @POST("/send-notification")
        Call<Void> sendNotification(@Body Notification notification);
    }

    private static FunctionsService functionsService = new Retrofit.Builder()
            .baseUrl(BASE_SERVER_URL)
            .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)))
            .build()
            .create(FunctionsService.class);

    public static Call<CreateBindingResponse> registerBinding(final Binding binding) {
        return functionsService.register(binding);
    }

    public static Call<Void> notify(Notification notification) {
        return functionsService.sendNotification(notification);
    }

}
