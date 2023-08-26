package com.sage.sage.microservices.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyAzureFunction {

    // Plain Spring bean - not a Spring Cloud Functions!
    @Autowired
    private Function<String, String> uppercase;

    // The FunctionCatalog leverages the Spring Cloud Function framework.
    @Autowired private FunctionCatalog functionCatalog;

    @FunctionName("spring")
    public String plainBean(
            @HttpTrigger(name = "req", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        return this.uppercase.apply(request.getBody().get());
    }

    @FunctionName("scf")
    public String springCloudFunction(
            @HttpTrigger(name = "req", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        // Use SCF composition. Composed functions are not just spring beans but SCF such.
        Function composed = this.functionCatalog.lookup("reverse|uppercase");

        return (String) composed.apply(request.getBody().get());
    }
}