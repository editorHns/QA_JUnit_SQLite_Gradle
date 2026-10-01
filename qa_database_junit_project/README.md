# QA: JUnit 5 + SQLite + Gradle

Реализована связка **Software + QA(Unit test) + Database**.

- тестовые наборы хранятся в SQLite;
- JUnit 5 `@ParameterizedTest` + `@MethodSource` получает их SQL-запросом;
- Gradle формирует HTML-отчёт;
- JaCoCo считает покрытие;
- JUnit extension сохраняет результаты запуска в `test_runs`;
- есть заведомо неверный тест для демонстрации отчёта.

## Команды

```bash
gradle test
gradle jacocoTestReport
gradle updateQaMetrics
gradle qaReport
```

HTML JUnit: `build/reports/tests/test/index.html`

HTML JaCoCo: `build/reports/jacoco/test/html/index.html`

База: `qa-results/qa-tests.db`

Демонстрация ошибки:

```bash
gradle test -PdemoFailure=true
```

SQLite:

```sql
SELECT * FROM test_cases;
SELECT * FROM test_runs;
```
