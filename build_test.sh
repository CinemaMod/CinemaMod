#!/bin/bash
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"

mvn compile
mvn install
sleep 0.5
cp -f target/CinemaMod.jar ../CinemaTestServer/plugins/CinemaMod.jar
tmux send-keys -t CinemaTestServer.0 "plugman reload CinemaMod" ENTER
