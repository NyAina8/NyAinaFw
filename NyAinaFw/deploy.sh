#!/bin/bash

set -e

TOMCAT_PATH="${TOMCAT_PATH:-/opt/tomcat}"
TOMCAT_LIB_PATH="$TOMCAT_PATH/lib"

if [ ! -d "$TOMCAT_LIB_PATH" ]; then
    echo "Dossier Tomcat introuvable : $TOMCAT_LIB_PATH"
    echo "Utilisation : TOMCAT_PATH=/chemin/vers/tomcat ./deploy.sh"
    exit 1
fi

echo "Construction de NyAinaFw..."
mvn clean package

JAR_FILE=$(find target -name "*.jar" \
    ! -name "*sources*" \
    ! -name "*javadoc*" \
    ! -name "original-*" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Aucun JAR trouvé."
    exit 1
fi

echo "Copie du JAR vers Tomcat..."
cp "$JAR_FILE" "$TOMCAT_LIB_PATH/"

echo "Déploiement terminé : $JAR_FILE"
