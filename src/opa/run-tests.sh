#!/usr/bin/env bash

# make sure you run this from the opa folder
docker run -it --rm -v $(pwd):/policy-root openpolicyagent/opa test -v --explain full /policy-root
#docker run -it --rm -v $(pwd):/policy-root openpolicyagent/opa test --help