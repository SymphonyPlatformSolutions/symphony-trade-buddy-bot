#!/bin/sh
export TOKEN=abcdefghijklmnopqrstuvwxyz
docker run --rm --name trade-buddy -p 8080:8080 \
    -e "PROFILE=prod" \
    -e "ENCRYPT_PASSPHRASE=$TOKEN" \
    -e "MONITORING_TOKEN=$TOKEN" \
    -e "MANAGEMENT_TOKEN=$TOKEN" \
    -e "GITHUB_TOKEN=$TOKEN" \
    -e "LOADER_PATH=/symphony/lib,/symphony/lib2" \
    -v ./application-prod.yaml:/symphony/application-prod.yaml \
    -v ./rsa:/symphony/rsa \
    -v ./data:/symphony/data \
    -v ./lib:/symphony/lib2 \
    -v ./workflows:/symphony/workflows \
    -u $(id -u):$(id -g) \
    finos/symphony-wdk-studio:1.6.3
