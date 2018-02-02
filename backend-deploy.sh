#!/bin/sh

asadmin start-domain
asadmin deploy /jarvis-backend/out/artifacts/jarvis_backend_war/jarvis_war.war
asadmin stop-domain
asadmin start-domain --verbose