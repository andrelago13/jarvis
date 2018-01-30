FROM oracle/glassfish:latest

ADD jarvis-backend /jarvis-backend
COPY backend-deploy.sh /
#WORKDIR /

EXPOSE 8080
EXPOSE 4848

ENTRYPOINT ["/backend-deploy.sh"]

#RUN asadmin deploy out/artifacts/jarvis_backend_war
#CMD ["asadmin", "deploy", "/jarvis-backend/out/artifacts/jarvis_backend_war"]