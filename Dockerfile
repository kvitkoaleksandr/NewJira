# Используем JDK 19 от Amazon
FROM amazoncorretto:19.0.2
# Копируем скомпилированный .jar файл
COPY build/libs/managment_service-1.0.jar /app.jar
# (Необязательно) копируем application.yml, если он не в jar
COPY src/main/resources/application.yml /application.yml
# Запуск приложения
ENTRYPOINT ["java", "-jar", "/app.jar"]