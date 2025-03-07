## j4middle_intershop: Учебный проект магазина

### Требования:
- `JAVA 21`+

### Запуск локально:
- Обеспечить наличие развёрнутой БД `postgres` на `5432` порту, креды по-умолчанию `postgres:postgres`, задать другие значения можно в `application.properties`, блок `spring.datasource`
- MAC / LINUX:
    - Задать директорию для хранения изображений каталога в системную переменную `$INTERSHOP_ITEM_IMG_DIR`
    - `./gradlew bootJar`
    - скопировать файл `intershop/build/libs/intershop-1.0.jar` в желаемую директорию (опционально)
    - `chmod +x intershop-1.0.jar`
    - `./intershop-1.0.jar`

-  WINDOWS
    - Задать директорию для хранения изображений каталога в системную переменную `$INTERSHOP_ITEM_IMG_DIR`
    - `.\gradlew.bat bootJar`
    - скопировать файл `intershop\build\libs\intershop-1.0.jar` в желаемую директорию (опционально) 
    - `java -jar intershop-1.0.jar`

### Запуск в docker
- `docker build -t intershop:1.0 .`
- `docker-compose up --build`

### Детали использования: 
- Главная страница приложения - `http://localhost:8080/main/items`
- На главной странице есть кнопка ADMIN, ведущая к созданию товаров