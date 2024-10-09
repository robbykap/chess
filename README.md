# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## Chess Server Design

[Chess Server Design](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGIASQActIYNTKIyYJzwjUALIwOnAUaYRnElknUG4lTlNA+BAIHEiFRshXM0kgSFyFBCuE3RkM7SK9Rs4ylBTSAXmxk6qh404GkmqUrGlCmhQ+MCpWHAYOpK3ym2G1mnB1OgVBkNusF63KnQEXMoRKFIqCRVSarBZ4HKwpHS4wa4dO6TcqrJ7hkP1CAAa3Q9am+3dlHL8GQ5nKACYnE5ujWhrKxjAG48ps3Uq2O2gu6sDugOKYvL5-AFoOxyTAADIQaJJAJpDJZQd5CvFKvVOpNVoGdQJNATqYy0YrL4vN4Pj6SYDkrEFClLKsem-Wtpz-J4AKWP8YABc4yxVXUUHKBAzyQNBYVPc9UXRWJsTTQwvRjH0yQpc1aVg0ZZkQ95rSJWN7Q5GAAHEAFF6mlDoRTFSUBKnOVvSVDN70w9VNW1cj+0gspKG8KBMCU-swKuKZRnUYByXGKYeKgVTyh8YII2gJAAC8UGWFCAG4vz6XTVH0+4jJM6BylhDg1GNYgCESGAIAAMxgFToExEDexBU5siHSoABYnAARmaaCXJZdzDL6YzTOlCyQys2z7L2Jy0AqABWMcMp07KDIbfLvJgXz-KgQKPxC8LIqgaKUOjNifU01UsMtbR3U9AkqJZP0TUyZNQ3G+RWKZYb404xMYEXEV6hqbaI0m9NMzQqtCLwwti3U064ukkorgGBiZznL5F2XTtZ27PpQIfW6BxyMBygqUcnD2atHrE56vqbCN3tXT71zMTht28PxAi8FB0BPM9fGYS90kyTAErvIpqEfaQeOPPieOaFo31UD9umrBdYfbTsJhisC2SU7S+jetnVw5gaNIwj01RgHD7FxgiceDYiMTI0b9Rm0kYCoPAkFCyxFrDVmV1W2040KB1uL4g6U20UVxSlfmV0GtbJNF8EYA1LUjook6gUuXrrq9v6tOZrK9MazyCvMxcSrskCKsy1ycqarycza1QAuwIKkjCiLE-6n6yb+4nAeStK6qDtyQ7yxOzKK1JI7KirqtqwO4-L5qk78lOOrTrrM96nP7cNkaZPNyMJoUyihtmowUG4BaI11i2Vv79iNvKCmqfqHjh7hq2RMXd3FJu8pCNxy6EBLG7B-u6tc+OKT-sS4GxzBnoN2RzxUb3SEOBPaFuOnVl8bXiJreZgotHy8RfC0ew04maZVtuzYCA0uae2zLzeBgtEGoT9myci2FoRcX-gRfB-95akX3uPB2qt1YdS1jrdBBtl7G04hA4eQlrZbwFkvdaTtxau3kkrO+PMfYizATmWODUPIVzDtXWu0dnLN0ka3Hy7dU7p26lnVSfdkHxRAUDFK6Um4SNykowqEcOqlTkZVGqTgS4KOMZXVqKjO5qJ7tnGKEk7R31waw0eAjpoT1VuSMABDdKwgYetJh5QWHQN0lwx2d1nYxLUOQlBwIj7EN0qfc+2CvG-V5kk1QhkKiZSSdyaQhlUrDgAMxJSeFeTI5o6wcyeDoBAoA2yNLgs0qYSTeTTg5mDRoN8+x3wLkDEGJcClFJKdOMpFTqm1KmPUs0T14JTFae0zpv5ul9F6f04CgzX5bnfruQI2AfBQGwNweA81DAhMMIAwmBccF5MqLUBotMkmwJZi2AWazdnTj6dszBsVuaH0Dug-5ezgUxRFgk8W-pTT3NhHAW59zSFYnIf4yhvo1Ya1oXPehcTPGRNNvxHalthI2z1ugYlcYeFYRdnJFJEFD7CIvrkvOvM7EJwKnob+kI0QYkseI4OiiHHh0suYqOjl5FGN5S1ZOqju49TcUg36-YxlFxsYYsV9i+XAAFSgIVsRLEVH0bY+VocWqSuKtKuu3QG46tFWXcVBUlXOJVRoqK7iVb0vhYyil8gsUyD9eURFmRkVJPCZJUlfIyA8XCPxJJdLL6JNmdIFlZw-blFRQGSN04sm+2zJfKCPSM3zJqeqvOmrdGVAmYHUp5TyiVKrQcJGxydxowCJYaeOFkgwAAFIQDwn-UYgQNkgDbMAgGLyuVvMpJAr5SQ4E0owf+NpvaoBwAgDhKAuUADqLBuQbxaAAIWPAoOAABpL4TbK1JWrbfVlOaIVrv+Vc4AW6d17sPce09F6r23qePeltCzhYcoDeUAAViOtAyLh14XRcakimKx7YsNuUahmttaErXTGkl7IolmyDcAHe1Lfl2w8f60mYtGV8KzUIxOxb0KiO5VaqRLV+UwEFahkV9U9UKpzLamu9r+Ol3jtatu7VOrBVcZozmGrRl1vNY3F1knOM5m47x4VsrKoWt1a6-VNqZFib006y1gmpPKJk13OTqqFMDWo2m8WpGQ3UZosE6csIm0EaNkRslMAklsJEk21NnK6NkmnKmARDLygUGYDcGAoVvDDB8fIbaqhuh9GWVsmc0BsuTo6asxjbLmMiLumWiTLcHHAAQCaDgYR5ArkUzW5TAM9FOCqZZozQnyh1Ya014ALWn1TVDQE3FfgtAFtGD56L2g-McXKJSKo0gFAbyC-N4NzmIvOyoCOrcY9UlVkQ-BwtagroVdo1BYZ+cVMNpfh2lGpyAheC-QOAMsBgDYCuYQeIwVHk3lnWxyoa9qa02MKC47YisElrizAEA3A8DSDPWE9zYaEdI6gDrRbK8Ig8QlDUAAapvRaa2ailZzeynJIP1M1fdU42TGcHM+qfSMnRHXKjWJ6xpkxHqmfqN7q1ujysJvhqx+aVQaOdukvCAT4nm9XQKAp0dl9qDqdw9pwJ3r1nHG2ZcSzvqwv2eFC1RZwzvOHH87s8z71RvRvpgw7GcXn37lS9x7L+XJOx0oHJ5T9X5WOVa+q26xVjObeC7Vbd2tnPzd09D9JjuAv5Os4ODt+H+2kCHdi2rtJH3kdnsiO0M+LGTfXZh1DjnD8HtHMwEAA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
