#!/usr/bin/env bash

# make sure you run this from the opa folder
docker run --rm -d \
  -v $(pwd):/policy-root \
  -p 8181:8181 \
  openpolicyagent/opa run --server /policy-root


#POST /v1/data/api/allow HTTP/1.1
#Host: localhost:8181
#Content-Type: application/json
#
#{
#    "input": {
#        "user":{
#            "name":"jack",
#            "isAuthenticated":false
#        }
#    }
#}

## result
#{
#    "result": false
#}
