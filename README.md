# Akka Construction

## Build JAR
```bash
./gradlew shadowJar
```
Fat JAR including all dependencies will be generated in `build/libs` directory.

## Run construction simulation
Sample configuration files can be found in [`config`](config) directory. Provide path to the config file as a parameter. For instance:
```bash
 java -jar build/libs/bridge-construction-1.0-all.jar config/config.json
```

## Run tests
```bash
./gradlew test
```