# Отчёт: Unit-тестирование с данными из базы данных

## 1. Цель

Реализовать автоматизированное Unit-тестирование, в котором тестовые наборы хранятся в БД, JUnit 5 получает параметры через `@MethodSource`, Gradle создаёт подробный HTML-отчёт, а результаты запуска сохраняются в БД.

## 2. Технологии

Java 17, JUnit 5, Gradle, SQLite, JaCoCo.

## 3. Тестовые данные

Таблица `test_cases` содержит `test_set`, `input`, `expected`. Набор `chess-counting` содержит несколько значений для проверки функции квадрата.

## 4. ParameterizedTest + MethodSource

Метод `databaseCases()` выполняет запрос:

```sql
SELECT input, expected FROM test_cases WHERE test_set=? ORDER BY id
```

Каждая строка преобразуется в `Arguments.of(input, expected)`, после чего JUnit выполняет отдельный экземпляр параметризованного теста.

## 5. HTML-отчёт Gradle

```bash
gradle test
```

Отчёт: `build/reports/tests/test/index.html`.

В нём отображаются тесты, время, успешные и проваленные случаи и stack trace для ошибок.

## 6. Демонстрация заведомой ошибки

```bash
gradle test -PdemoFailure=true
```

Включается тест с ложным утверждением `assertEquals(5, 2 + 2)`. Это позволяет получить пример отчёта с ошибкой. Обычный запуск проходит без него.

## 7. Сохранение результатов в БД

Таблица `test_runs` содержит дату, тестовый набор, количество тестов, успешные и проваленные тесты, время выполнения, покрытие и поле для дополнительной метрики сложности.

JUnit extension записывает итоговый результат после тестового класса.

## 8. Покрытие

JaCoCo:

```bash
gradle jacocoTestReport
gradle updateQaMetrics
```

HTML: `build/reports/jacoco/test/html/index.html`.

Процент покрытия также записывается в `test_runs.coverage_percent`.

## 9. Итог

Получена требуемая цепочка:

`SQLite -> SQL -> MethodSource -> JUnit 5 -> Gradle HTML -> JaCoCo -> test_runs`.

Тестовые наборы находятся в БД, а результаты тестирования сохраняются в отдельной таблице.
