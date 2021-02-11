![https://img.shields.io/badge/Status-WIP-red](https://img.shields.io/badge/Status-WIP-red) ![https://img.shields.io/badge/Realese-No-red](https://img.shields.io/badge/Realese-No-red)

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

# IOTMe
  Http wrapper for interaction in admin-user-module system     
  Kotlin [203-1.4.21] +    
  [Построено по примеру](https://play.kotlinlang.org/hands-on/Creating%20HTTP%20APIs%20with%20Ktor/01_introduction)
____
#### Open server: Нет.
#### Список задач:
- [ ] Доабвить тесты
- [ ] Сохранение информации в базе данных
# Применение 
### Где?
  - Помошник, при администрировании iot систем небольшого масштаба с выходом в интернет
  - Созданеи чат-ботов и иных инструментариев для управления умным домом

### Как? (после развертывания сети)
1. Зарегистрировать администратора сети и задать ему используемые модули
2. Подключить ваш код(обработчик) к нашему серверу или своему
3. Зарегистрировать пользователей сети(членов семьи, аналитиков данных, собаку)

### Зачем? 
 - Готовая структура, иерархия
 - Не нужен аптайм обработчика, серверов чат-ботов и прочего
 - Быстро
____
# Использование 
  - Понять иерархию "module"-"user"-"user admin" системы    
` [Server] + Module (ex. датчик) -> Создание Module[system admin] (задающий конфигурацию тех или иных модулей, принимает debug информацию, ex, вы, raspberry pi(arduino)) -> Создание Module[User] (принимает и обрабатывает информацию с модуля, ex, чат-бот)  
`
  - Клонировать репозиторий
  - Открыть в IntelliJ IDEA (убедиться, что версия Kotlin соответствует минимально требуемой)
  - Отредактировать resources \ [application.conf](https://ktor.io/docs/a-ktor-application.html)
      - Задать порт
      - Задать ip (для теста оставить localhost)
  - Запустить проект(точка входа main -> ..\src\main\kotlin\com\jetbrains\handson\httpapi\Application.kt)
  - Создать необходимых юзеров/админов по иерархии и пользоваться  
  - Радоваться жизни:)
  
____
# Описание API 
  #### Обозначения
  Далее 
  **guid** - id (администратора системы/модуля/конечных пользователей продукта)     
  **secret** - пароль администратора    
  Ответ **Bad Request** - ошибка при составлении запроса, некорректные данные    
  Ответ json **status=ok** - запрос отработал корректно    
  Если ответ на запрос не указан, то кроме **status=ok** иного ответа не подразумевается    
  Если не указан тим запроса, считать, что GET    
  
  ## Updates API - взаимодействие между устройствами
  ### POST /message/send - Отправка сообщения
  Следующий заголовок обязателен
  ```
  Content-Type: application/json 
  ```
  #### Параметры (json)
  Пример: 
  `
  {"from":"myModule","to":"none","type":"AdminMessage","message":"testMessage","unixTimes":1}
  `
  - **from** ID отправителя
  - **to** ID получателя(если путь не user-module, не admin-module, то `none`) 
  - **type** Один из типов ` UserMessage, AdminMessage, ConfigMessage, UserInfo ` 
  - **message** Текст сообщения
  - **unixTimes** время, которое считать временем отправки сообщения, нет ограничений на будущее или прошлое
  
  
  ### /messages/get/{type}/{guid}/{secret} - Получение новых сообщений `аналог get updates`
  #### Параметры
  - **guid** ID Получателя
  - **secret** его пароль, если такого нет, то считать, что `none`
  - **type** один из типов получателя `other, admin `
  
  
  ## Admin API
  ### /admin/add/{guid}/{secret} - Регистрация администратора (системного)
  #### Параметры
  - **guid** ID администратора
  - **secret** его пароль 
  
  ### /admin/set/{guid}/{secret}/{module} - Добавление модуля администратору
  #### Параметры
  - **guid** ID администратора
  - **secret** его пароль 
  - **module** guid модуля, добавляемого вами
  
  ### /admin/get/{guid}/{secret} - Получение списка модулей администратора
  #### Параметры
  - **guid** ID администратора
  - **secret** пароль администратора
  #### Ответ
  - При верном запросе вернется JSON массив из списка модулей
  - При неверном запросе вернется *null* при невеврных авторизационных данных
  
  ### /user/add/{guid}/{admin}/{secret} - Создание пользователя, подконтрольному данному администратору сети
  #### Параметры
  - **admin** ID администратора
  - **secret** пароль администратора
  - **guid** guid нового пользователя 
  
  ### /user/set/{guid}/{module}/{admin}/{secret} - Подключение пользователя к модулю(устройству)
  #### Параметры
  - **admin** ID администратора
  - **secret** пароль администратора
  - **guid** guid пользователя 
  - **module** guid модуля, добавляемого вами
  
  
