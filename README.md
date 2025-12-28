# ORM Learning Platform

Это учебный проект на Kotlin и Spring Boot, реализующий систему управления курсами, уроками, заданиями, тестам и пользователями.

## Структура проекта

- src/main/kotlin — исходный код приложения
- src/test/kotlin — интеграционные тесты
- [pom.xml](pom.xml) — Maven-конфигурация
- [Dockerfile](Dockerfile) — Docker образ для приложения
- [db.changelog-master.xml](src/main/resources/db/changelog/db.changelog-master.xml) — Liquibase миграции базы данных
- [application.yml](src/main/resources/application.yml) — настройки приложения
- [application-test.yml](src/test/resources/application-test.yml) — настройки тестового окружения

## Domain

- **[BaseEntity.kt](src/main/kotlin/org/orm/domain/base/BaseEntity.kt)** - базовая сущность для переиспользования остальными.
- **[Assignment.kt](src/main/kotlin/org/orm/domain/content/Assignment.kt)** – задание, которое относится к уроку, студенты могут сдавать решения.
- **[Lesson.kt](src/main/kotlin/org/orm/domain/content/Lesson.kt)** – урок внутри модуля, может иметь задания.
- **[Module.kt](src/main/kotlin/org/orm/domain/content/Module.kt)** – модуль курса, состоит из уроков и может содержать викторину (1-1 связь).
- **[Submission.kt](src/main/kotlin/org/orm/domain/content/Submission.kt)** – решение студента для задания, содержит оценку и комментарий преподавателя.
- **[Category.kt](src/main/kotlin/org/orm/domain/course/Category.kt)** – категория курса, например "Программирование" или "Математика".
- **[Course.kt](src/main/kotlin/org/orm/domain/course/Course.kt)** – курс, который ведет преподаватель и на который могут записываться студенты.
- **[CourseReview.kt](src/main/kotlin/org/orm/domain/course/CourseReview.kt)** - отзыв о курсе
- **[Enrollment.kt](src/main/kotlin/org/orm/domain/course/Enrollment.kt)** – запись студента на курс, связывает студента с курсом.
- **[Tag.kt](src/main/kotlin/org/orm/domain/course/Tag.kt)** - тег курса
- **[AnswerOption.kt](src/main/kotlin/org/orm/domain/quiz/AnswerOption.kt)** – вариант ответа на вопрос викторины, один из них может быть правильным.
- **[Question.kt](src/main/kotlin/org/orm/domain/quiz/Question.kt)** – вопрос викторины, может иметь варианты ответа.
- **[Quiz.kt](src/main/kotlin/org/orm/domain/quiz/Quiz.kt)** – викторина, привязанная к модулю (1-1), содержит вопросы.
- **[QuizSubmission.kt](src/main/kotlin/org/orm/domain/quiz/QuizSubmission.kt)** – результат прохождения викторины студентом, содержит оценку и время прохождения.
- **[User.kt](src/main/kotlin/org/orm/domain/user/User.kt)** – пользователь системы, может быть преподавателем или студентом.
- **[UserProfile.kt](src/main/kotlin/org/orm/domain/user/UserProfile.kt)** - профиль пользователя

Проект покрыт интеграционными тестами, покрытие методов сервиса составляет 100%

## Технологии

- Kotlin 1.9
- Spring Boot 3.5.8
- Spring Data JPA (Hibernate)
- PostgreSQL
- Liquibase
- Maven
- JUnit 5

## Требования

- Java 17
- Maven
- Docker (опционально, для контейнеризации)
- PostgreSQL

## Как пользоваться системой

1. Клонируйте репозиторий:  

```bash
git clone <repository-url>
cd <repository-folder>
```

2. Настройте базу данных PostgreSQL и укажите параметры в application.yml:  

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/learning
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: none // !важно, потому что Liquibase будет создавать таблицы
```

3. Соберите проект с maven:  
```bash
mvn clean package
```

4. Запустите приложение:  
```bash
java -jar target/orm-0.0.1-SNAPSHOT.jar
```

5. Для запуска тестов после шага 3 введите `mvn test` или запустите тесты из IDE.