package org.openmrs.module.registrationcore.api;

public interface ErrorHandlingService {
    
    void handle(String messageBody, String destination, boolean failure, String failureReason);
}
