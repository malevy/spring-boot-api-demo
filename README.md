# hyperdemo
A simple api for experimenting with hypermedia apis on spring-boot 

The API consists of a number of topics to trial
-  supporting [HAL](https://tools.ietf.org/id/draft-kelly-json-hal-01.html) and [Siren](https://github.com/kevinswiber/siren) media types 
   via content negotiation and [WeSTL](https://github.com/RWCBook/wstl-spec)
-  decomposing the typical 'service' component using the Command Pattern with
   a [dynamic dispatch](net/malevy/hyperdemo/commands/impl/CommandDispatcherImpl.java)
-  authorization delegated to [Open Policy Agent](https://www.openpolicyagent.org/)

## security
the API uses Basic Auth for authentication. OpenPolicyAgent (OPA) enforces a policy
requiring all calls to be made by an authenticated user.

The [OPA](src/opa) folder contains the policies that OPA uses and tests against
those policies.

To run the tests, make sure that the OPA folder is your current working folder
and use the following command:

```shell
bash run-tests.sh
```

see the section on [Policy Testing](https://www.openpolicyagent.org/docs/latest/policy-testing/)
in the OPA Documentation for more information about writing tests for 
OPA policies

## building
the project and be built and all tests executed with Maven:

```shell
mvn clean integration-test
```

The Maven Spring Boot plug-in is used to package the service into a container

```shell
mvn spring-boot:build-image
```

# deployment
there are two docker-compose files in the root
- [docker-compose.yml](docker-compose.yml) will spin up the task-api service and 
include OpenPolicyAgent as a sidecar. The API is exposed on port 8080
- [docker-compose-svcs.yml](docker-compose-svcs.yml) will spin up the task-api and
OpenPolicyAgent as two separate containers. The API is exposed on port 8080
  and OPA on port 8181

## API testing
a [file](src/test/java/net/malevy/hyperdemo/sample-calls.http) 
of sample calls can be fround in the test folder

