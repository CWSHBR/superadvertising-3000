# SUPERADVERTISING - 3300

- [Описание проекта](#описание-проекта)
- [Запуск проекта](#запуск-проекта)
- [Точки входа](#точки-входа)
- [Выбор технологий](#выбор-технологий)
- [Используемые библиотеки](#используемые-библиотеки)
- [Описание схемы данных](#описание-схемы-данных)

## Описание проекта
SUPERADVERTISING - это рекламная платформа, позволяющая управлять рекламными кампаниями, анализировать статистику и использовать AI для генерации рекламных текстов. Проект использует стек технологий, обеспечивающий надежность, масштабируемость и удобство работы с данными.

## Запуск проекта
Запуск производится командой:
```
docker compose up -d
```

Файл `docker-compose.yml` содержит описание всех необходимых сервисов для работы проекта, включая:
- **rabbitmq** (rabbitmq:3.10.7-management) - брокер сообщений для обработки очередей
- **db** (postgres:15) - основная база данных
- **minio** (docker.io/bitnami/minio:2025) - объектное хранилище
- **redis** (redis:7) - NоSQL СУБД (используется Superset в качестве кэша)
- **superset** - один из сервисов для локального запуска Superset
- **superset-init** - инициализация перед запуском Superset
- **superset-worker** - один из сервисов Superset
- **superset-worker-beat** - один из сервисов Superset
- **advertising** - сервис бэкенда платформы
- **tgbot** - сервис телеграм-бота

#### Порядок зависимостей:
    (db, redis) -> (superset-init) -> (superset, superset-worker, superset-worker-beat) -> (advertising) -> (tgbot)

Это требуется для корректного импорта дашбордов в Superset и проведения миграций базы данных.

#### Взаимодействие с платформой
Backend - сервис будет отвечать на порте `8080` \
Визуализация (superset) - доступен на порте `8088`. Логин/пароль: `admin`/`admin` \
Telegram bot - отвечает в [Телеграме](https://t.me/advertizerplatformbot)

## Точки входа

### Рекламные объявления
- `GET /ads` - получение рекламы пользователем
- `POST /ads/{adId}/click` - засчитать клик пользователя

### Рекламодатели
- `POST /advertisers/bulk` - массовое добавление рекламодателей
- `GET /advertisers/{advertiserId}` - получение рекламодателя по ID
- `GET /advertisers/{advertiserId}/campaigns` - получение списка всех кампаний у рекламодателя (пагинация: `size` & `page`, `page` отсчет с нуля)
- `POST /advertisers/{advertiserId}/campaigns` - создание новой рекламной кампании
- `GET /advertisers/{advertiserId}/campaigns/{campaignId}` - получение рекламной кампании
- `PUT /advertisers/{advertiserId}/campaigns/{campaignId}` - изменение рекламной кампании
- `DELETE /advertisers/{advertiserId}/campaigns/{campaignId}` - удаление рекламной кампании
- `POST /advertisers/{advertiserId}/campaigns/{campaignId}/generatetext` - генерация рекламного текста с помощью YandexGPT

### Изображения
- `GET /advertisers/{advertiserId}/campaigns/{campaignId}/image` - получение изображения рекламной кампании
- `POST /advertisers/{advertiserId}/campaigns/{campaignId}/image` - добавление или изменение изображения рекламной кампании

### Клиенты
- `POST /clients/bulk` - массовое добавление клиентов
- `GET /clients/{clientId}` - получение клиента по ID

### ML-оценка
- `POST /ml-scores` - добавление или изменение параметра ML-score для пары рекламодатель-клиент

### Модерация
- `POST /moderation` - включение или выключение модерации
- `POST /moderation/addrestrictedwords` - добавление запрещенных слов для модерации текста

### Статистика
- `GET /stats/advertisers/{advertiserId}/campaigns` - общая статистика за все время по кампаниям рекламодателя
- `GET /stats/advertisers/{advertiserId}/campaigns/daily` - дневная статистика по кампаниям рекламодателя
- `GET /stats/campaigns/{campaignId}` - общая статистика за все время по кампании
- `GET /stats/campaigns/{campaignId}/daily` - дневная статистика по кампании

### Управление временем
- `POST /time/advance` - изменение дня в системе

## Выбор технологий

### PostgreSQL (Главная база данных)
- Надежность и соответствие ACID
- Хорошая поддержка JSONB, что полезно для хранения метаданных
- Масштабируемость и высокая производительность

### Minio (S3-совместимое объектное хранилище)
- Поддержка S3 API, удобство интеграции
- Гибкость и отказоустойчивость
- Хорошая производительность при работе с изображениями

### RabbitMQ (Очередь сообщений)
- Используется для очередей запросов к YandexGPT
- Гибкость в обработке фоновых задач
- Высокая надежность и поддержка подтверждений доставки

### Superset (Визуализация данных)
- Уже есть опыт работы с этим инструментом
- Удобный UI для анализа данных
- Возможность построения сложных дашбордов

### YandexGPT (Генерация текстов)
- Доступность благодаря гранту
- Хорошее качество генерации рекламных текстов

## Используемые библиотеки
- `Ktor server` - серверный фреймворк
- `Ktor client` - клиент для взаимодействия с внешними API
- `AWS SDK` - работа с S3 (Minio)
- `Exposed` - ORM для PostgreSQL
- `HikariCP` - пул соединений с базой данных
- `Logback` - логирование
- `ktor-server-test-host` - тестирование серверной части
- `kotlin-test-junit` - тестирование
- `damirdenis-tudor:ktor-server-rabbitmq` - интеграция с RabbitMQ
- `dev.inmo:tgbotapi` - работа с Telegram API


## Описание схемы данных
![ER-диаграмма](images/advertisers.png)
#### Краткое описание таблиц:
- `advertisers` - Рекламодатели
- `clients` - Клиенты
- `ml_scores` - ML-score для пары рекламодатель-клиент _(Записи попарно уникальны. То есть пара adv1-client1 встретится лишь единожды)_
- `campaigns` - Рекламные кампании
- `campaign_target` - Настройки таргетирования для кампании _(Уникальность по campaign_id)_
- `images` - Таблица сопоставления названия файла картинки в хранилище к ID кампании. _(Уникальность по campaign_id)_
- `impressions` - Просмотры рекламы. _(Записи попарно уникальны. Сохранияется цена на момент просмотра. Не удаляется при отсутсвии кампании или клиента.)_
- `clicks` - Клик по рекламе. _(Записи попарно уникальны. Сохранияется цена на момент клика. Не удаляется при отсутсвии кампании или клиента.)_

## Что можно добавить?
- Раздел о деплое и окружении
- Инструкция по развертыванию проекта
- Описание схемы данных (ER-диаграмма)
- Описание архитектуры приложения
- Раздел о безопасности API

---

# TAK

Этот файл можно дополнить детальной инструкцией по запуску и развертыванию. Если что-то нужно уточнить или дополнить, скажи!
