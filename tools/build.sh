#!/bin/bash

VUE_APP_PATH=""
SPRING_APP_PATH=""
DOCKER_REGISTRY="localhost:5000"

echo "Vue path:" $VUE_APP_PATH
echo "Spring path:" $SPRING_APP_PATH
function build_vue() {
    cd $VUE_APP_PATH
    npm run build
}

function move_vue_dist() {
    SPRING_PUBLIC=$SPRING_APP_PATH/src/main/resources/public/
    cd $VUE_APP_PATH
    cd dist
    cp -r * $SPRING_PUBLIC
}

function build_image() {
    cd $SPRING_APP_PATH
    ./mvnw spring-boot:build-image
    docker build . --tag honmon_app
}

function publish_image() {
    docker image tag honmon_app $DOCKER_REGISTRY/honmon_app
    docker push $DOCKER_REGISTRY/honmon_app
}

build_vue
move_vue_dist
build_image
publish_image
read -s -n 1 -p "Press any key to finalize.." 
