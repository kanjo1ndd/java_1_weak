# Java JSON Statistics Parser

## Опис проекту

Консольна програма на Java, яка парсить JSON-файли основної сутності (наприклад, книги) і формує статистику за вибраним атрибутом.  
Програма підтримує багатопоточний розбір файлів і формує результат у вигляді XML-файлу, відсортованого за кількістю від більшого до меншого.

**Приклад предметної області:**  
- Основна сутність: **Book (Книга)**  
- Додаткова сутність: **Author (Автор)**, зв'язок «багато до одного»  

**Атрибути книги:**  
- `title` — назва книги  
- `author` — автор книги  
- `year_published` — рік публікації  
- `genre` — жанри (через кому, кілька значень)

---

## Приклад вхідного JSON

```json
[
  {
    "title": "1984",
    "author": "George Orwell",
    "year_published": 1949,
    "genre": "Dystopian, Political Fiction"
  },
  {
    "title": "Pride and Prejudice",
    "author": "Jane Austen",
    "year_published": 1813,
    "genre": "Romance, Satire"
  },
  {
    "title": "Romeo and Juliet",
    "author": "William Shakespeare",
    "year_published": 1597,
    "genre": "Romance, Tragedy"
  }
]

Запуск програми
java -cp "target\classes;target/dependency/*" com.example.Main <шлях_до_папки> <атрибут>


Приклади:

java -cp "target\classes;target/dependency/*" com.example.Main "data/json_folder" author
java -cp "target\classes;target/dependency/*" com.example.Main "data/json_folder" genre

<шлях_до_папки> — шлях до папки з JSON-файлами
<атрибут> — ім'я атрибута для формування статистики (author, year_published, genre)

Приклад вихідного XML (statistics_by_genre.xml)
<statistics>
  <item>
    <value>Romance</value>
    <count>2</count>
  </item>
  <item>
    <value>Dystopian</value>
    <count>1</count>
  </item>
  <item>
    <value>Political Fiction</value>
    <count>1</count>
  </item>
  <item>
    <value>Satire</value>
    <count>1</count>
  </item>
  <item>
    <value>Tragedy</value>
    <count>1</count>
  </item>
</statistics>

Особливості реалізації

Парсинг JSON-файлів відбувається поток за потоком, щоб не завантажувати великі файли у пам'ять повністю.

Використовується ExecutorService для багатопотокової обробки.

Поле з кількома значеннями (genre) коректно розбивається за комою.

Статистика зберігається у StatisticsAggregator та сортується за спаданням кількості.

Результат зберігається у XML-файл statistics_by_{attribute}.xml.

Unit-тести перевіряють:

Коректний парсинг JSON-файлів

Підрахунок статистики за різними атрибутами

Експерименти з кількістю потоків
Кількість потоків	Час обробки (приклад)
1	                            1200 ms
2	                            700 ms
4	                            400 ms
8	                            350 ms

Чим більше потоків, тим швидше обробка, особливо при великій кількості файлів.

Unit-тести

Проект містить unit-тести для перевірки коректності парсингу JSON та підрахунку статистики.
Приклад тесту:

@Test
public void parsesArrayOfBooks() throws Exception {
    String json = """
        [
          {"title":"A","author":"X","year_published":2000,"genre":"G1"},
          {"title":"B","author":"Y","year_published":2001,"genre":"G2,G3"}
        ]
        """;

    Path tmp = Files.createTempFile("books", ".json");
    Files.writeString(tmp, json);

    JsonFileParser parser = new JsonFileParser();
    List<Book> consumed = new ArrayList<>();
    parser.parseFile(tmp.toFile(), consumed::add);

    assertEquals(2, consumed.size());
    assertEquals("A", consumed.get(0).getTitle());
    assertEquals("X", consumed.get(0).getAuthor());
    assertEquals(2000, consumed.get(0).getYear_published());
    assertEquals("G1", consumed.get(0).getGenre());
}
