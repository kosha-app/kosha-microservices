# sage-mca


# deploying

az spring app deploy \
--resource-group VisualStudioOnline-7ADA98DAA42E42A1A5CDCB2F11A79EF4 \
--service gelo-corp-m \
--name sage \
--artifact-path sage-microservices-0.0.1-SNAPSHOT.jar


# api url

https://sage.redocean-171801c3.centralus.azurecontainerapps.io

### Sequence Diagrams

```mermaid
sequenceDiagram
    participant Client
    participant UserService
    participant UserRepository
    participant OtpRepository
    participant JavaMailSender

    Client->>UserService: checkEmail(email)
    UserService->>UserRepository: existsByEmail(email)
    alt Email exists
        UserService-->>Client: Error (email already registered)
    else Email does not exist
        UserService->>UserService: generateSixDigitOTP()
        UserService->>JavaMailSender: sendOtp(otp, email)
        UserService->>OtpRepository: saveOtp(otp, email)
        UserService-->>Client: CheckEmailResponse(otpId)
    end
```

```mermaid
sequenceDiagram
    participant Client
    participant UserService
    participant UserRepository
    participant DeviceModel

    Client->>UserService: signUserIn(request)
    UserService->>UserRepository: existsByEmail(email)
    alt Email not found
        UserService-->>Client: Error (user not found)
    else Email found
        UserService->>UserRepository: findByEmail(email)
        alt Password matches
            UserService->>UserRepository: save(updated User with new device)
            UserService-->>Client: DefaultResponse(success)
        else Password does not match
            UserService-->>Client: Error (incorrect password)
        end
    end
```
