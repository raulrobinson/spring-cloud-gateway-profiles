# Certificados SSL para desarrollo local

- Generar certificado autofirmado
```bash
keytool -genkeypair \
  -alias backend-dev \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore backend-dev.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -dname "CN=localhost, OU=Development, O=MyCompany, L=City, S=State, C=US"
``` 

- Crear certificado `.cer` a partir del keystore
```bash
keytool -exportcert \
  -alias backend-dev \
  -keystore backend-dev.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -file backend-dev.cer
```

- Crear truststore desde certificado `.cer`
```bash
keytool -importcert \
  -alias backend-dev \
  -file backend-dev.cer \
  -keystore dev-truststore.p12 \
  -storetype PKCS12 \
  -storepass changeit
```

- Ejecutar por perfil
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
./gradlew bootRun --args='--spring.profiles.active=qa'
```

- Para GET:
```bash
curl -X GET "http://localhost:8085/proxy?endpoint=https://api.backend.com/customers" \
  -H "x-country: CO" \
  -H "x-channel: WEB" \
  -H "x-application: APP"
```

- Para POST:
```bash
curl -X POST "http://localhost:8085/proxy?endpoint=https://api.backend.com/customers" \
  -H "Content-Type: application/json" \
  -H "x-country: CO" \
  -d '{"customerId":"123"}'
```

# HTTP
```bash
curl.exe -i "http://localhost:8085/proxy?endpoint=https://httpbin.org/get"
```

# HTTPS (cert local, por eso -k)
```bash
curl.exe -k -i "https://localhost:8443/proxy?endpoint=https://httpbin.org/get"
```