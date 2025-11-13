FROM bellsoft/liberica-runtime-container:jre-21-glibc
EXPOSE 33301
COPY ./build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
